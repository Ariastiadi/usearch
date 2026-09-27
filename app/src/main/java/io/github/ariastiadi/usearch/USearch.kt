package io.github.ariastiadi.usearch

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import java.text.Normalizer
import java.util.Locale

/**
 * u search: a tiny, private app search.
 *
 * Opens as a transparent layer over the current home screen with the search
 * bar at the bottom and the keyboard up. Results appear above the bar while
 * typing; the best match sits right above the bar and opens on Enter.
 * No network, no permissions, nothing is stored.
 */
class SearchActivity : Activity() {

    private class AppEntry(
        val label: String,
        val key: String,
        val initials: String,
        val component: ComponentName,
    )

    private var apps: List<AppEntry> = emptyList()
    private val iconCache = HashMap<ComponentName, Drawable>()
    private var topResult: AppEntry? = null

    private lateinit var query: EditText
    private lateinit var results: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildUi())

        query.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = showResults()
        })
        query.setOnEditorActionListener { _, actionId, event ->
            val enter = actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            if (enter) topResult?.let { launch(it) }
            enter
        }

        Thread {
            val loaded = loadApps()
            runOnUiThread {
                if (!isDestroyed) {
                    apps = loaded
                    showResults()
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        query.requestFocus()
        query.post {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(query, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    // ---------------------------------------------------------------- UI

    private fun buildUi(): View {
        val root = FrameLayout(this).apply {
            setBackgroundColor(getColor(R.color.us_scrim))
            // Tapping the dimmed area closes search.
            setOnClickListener { finish() }
        }

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            isClickable = true // taps inside the panel don't close it
        }

        results = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bar_bg)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            visibility = View.GONE
        }
        panel.addView(
            results,
            LinearLayout.LayoutParams(MATCH, WRAP).apply { bottomMargin = dp(10) },
        )

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.bar_bg)
            setPadding(dp(20), 0, dp(20), 0)
        }
        bar.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_search)
                alpha = 0.7f
            },
            LinearLayout.LayoutParams(dp(22), dp(22)),
        )
        query = EditText(this).apply {
            background = null
            hint = getString(R.string.search_hint)
            setHintTextColor(getColor(R.color.us_text_dim))
            setTextColor(getColor(R.color.us_text))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            typeface = Typeface.MONOSPACE
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            // Ask the keyboard not to learn from what is typed here.
            imeOptions = EditorInfo.IME_ACTION_GO or
                EditorInfo.IME_FLAG_NO_EXTRACT_UI or
                EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }
        bar.addView(
            query,
            LinearLayout.LayoutParams(0, MATCH, 1f).apply { marginStart = dp(14) },
        )
        bar.addView(
            View(this).apply { setBackgroundResource(R.drawable.red_dot) },
            LinearLayout.LayoutParams(dp(8), dp(8)).apply { marginStart = dp(12) },
        )
        panel.addView(bar, LinearLayout.LayoutParams(MATCH, dp(56)))

        root.addView(
            panel,
            FrameLayout.LayoutParams(MATCH, WRAP, Gravity.BOTTOM),
        )

        // Keep the bar just above the keyboard / navigation bar.
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
            root.setOnApplyWindowInsetsListener { v, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime())
                v.setPadding(0, bars.top, 0, bars.bottom)
                insets
            }
        }
        return root
    }

    private fun showResults() {
        val q = normalize(query.text.toString())
        results.removeAllViews()
        topResult = null
        if (q.isEmpty()) {
            results.visibility = View.GONE
            return
        }

        val matches = apps
            .map { it to score(q, it) }
            .filter { it.second >= 0 }
            .sortedByDescending { it.second }
            .take(MAX_RESULTS)
            .map { it.first }
        topResult = matches.firstOrNull()

        if (matches.isEmpty()) {
            results.addView(
                TextView(this).apply {
                    text = getString(R.string.no_results)
                    setTextColor(getColor(R.color.us_text_dim))
                    typeface = Typeface.MONOSPACE
                    setPadding(dp(12), dp(12), dp(12), dp(12))
                },
            )
        } else {
            // Best match goes last, right above the search bar.
            for (app in matches.asReversed()) {
                results.addView(resultRow(app, app === topResult))
            }
        }
        results.visibility = View.VISIBLE
    }

    private fun resultRow(app: AppEntry, best: Boolean): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            val ripple = TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackground, ripple, true)
            setBackgroundResource(ripple.resourceId)
            setOnClickListener { launch(app) }
        }
        row.addView(
            ImageView(this).apply { setImageDrawable(iconFor(app)) },
            LinearLayout.LayoutParams(dp(40), dp(40)),
        )
        row.addView(
            TextView(this).apply {
                text = app.label
                setTextColor(getColor(R.color.us_text))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            },
            LinearLayout.LayoutParams(0, WRAP, 1f).apply { marginStart = dp(16) },
        )
        if (best) {
            row.addView(
                View(this).apply { setBackgroundResource(R.drawable.red_dot) },
                LinearLayout.LayoutParams(dp(8), dp(8)).apply { marginStart = dp(12) },
            )
        }
        return row
    }

    // ------------------------------------------------------------ search

    private fun loadApps(): List<AppEntry> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .map { info ->
                val label = info.loadLabel(pm).toString().trim()
                val key = normalize(label)
                AppEntry(
                    label = label,
                    key = key,
                    initials = key.split(' ').filter { it.isNotEmpty() }.joinToString("") { it.take(1) },
                    component = ComponentName(info.activityInfo.packageName, info.activityInfo.name),
                )
            }
            .sortedBy { it.key }
    }

    /** Higher is better, -1 means no match. */
    private fun score(q: String, app: AppEntry): Int {
        val k = app.key
        if (k == q) return 1000
        if (k.startsWith(q)) return 900 - k.length
        if (k.split(' ').any { it.startsWith(q) }) return 800 - k.length
        if (app.initials.startsWith(q.replace(" ", ""))) return 700
        val index = k.indexOf(q)
        if (index >= 0) return 600 - index
        // Letters in order, e.g. "wtsp" finds "whatsapp".
        var matched = 0
        for (c in k) {
            if (matched < q.length && c == q[matched]) matched++
        }
        return if (matched == q.length) 100 - k.length else -1
    }

    private fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
            .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
            .trim()

    private fun iconFor(app: AppEntry): Drawable =
        iconCache.getOrPut(app.component) {
            try {
                packageManager.getActivityIcon(app.component)
            } catch (e: PackageManager.NameNotFoundException) {
                packageManager.defaultActivityIcon
            }
        }

    private fun launch(app: AppEntry) {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(app.component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // App was removed or cannot be opened; just close search.
        }
        finish()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val MAX_RESULTS = 8
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}

/** Home screen widget: a search bar that opens [SearchActivity] when tapped. */
class SearchWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, widgetIds: IntArray) {
        val intent = Intent(context, SearchActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        for (id in widgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_search)
            views.setOnClickPendingIntent(R.id.widget_root, pending)
            manager.updateAppWidget(id, views)
        }
    }
}
