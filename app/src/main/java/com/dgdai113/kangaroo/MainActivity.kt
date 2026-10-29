package com.dgdai113.kangaroo

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var logView: TextView
    private lateinit var configInput: EditText
    private lateinit var threadsInput: EditText
    private lateinit var dpInput: EditText

    private external fun nativeEngineStatus(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try { System.loadLibrary("kangaroo_native") } catch (_: UnsatisfiedLinkError) { }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        content.addView(label("Kangaroo Mobile", 25, true))
        content.addView(label("Android ARM64 front end • early development build", 13, false))
        status = label("Checking native bridge…", 14, true)
        content.addView(status)
        content.addView(label("CPU threads", 14, true))
        threadsInput = field("1").apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        content.addView(threadsInput)
        content.addView(label("Distinguished-point bits (optional)", 14, true))
        dpInput = field("").apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        content.addView(dpInput)
        content.addView(label("Puzzle/config file contents", 14, true))
        configInput = field("")
        configInput.hint = "Paste Kangaroo input configuration here"
        configInput.minLines = 5
        configInput.gravity = android.view.Gravity.TOP
        configInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        content.addView(configInput)

        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(Button(this).apply {
            text = "Prepare run"
            setOnClickListener { prepareRun() }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        buttons.addView(Button(this).apply {
            text = "Reset"
            setOnClickListener {
                configInput.setText("")
                dpInput.setText("")
                threadsInput.setText("1")
                status.text = "Ready • engine execution not connected"
                logView.text = "Fields reset. No computation has been started."
            }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        content.addView(buttons)
        content.addView(label("Activity log", 16, true))
        logView = label("App opened. No computation has been started.", 13, false)
        content.addView(logView)
        setContentView(root)
        status.text = nativeStatusSafely()
    }

    private fun prepareRun() {
        val threads = threadsInput.text.toString().toIntOrNull()
        if (threads == null || threads !in 1..256) {
            status.text = "Check input"
            logView.text = "CPU threads must be a number from 1 to 256."
            return
        }
        val dpText = dpInput.text.toString().trim()
        val dp = if (dpText.isEmpty()) null else dpText.toIntOrNull()
        if (dpText.isNotEmpty() && (dp == null || dp !in 1..255)) {
            status.text = "Check input"
            logView.text = "DP bits must be blank or between 1 and 255."
            return
        }
        val config = configInput.text.toString().trim()
        if (config.isEmpty()) {
            status.text = "Configuration required"
            logView.text = "Paste a valid Kangaroo configuration before preparing a run."
            return
        }
        status.text = "Configuration validated • engine not connected"
        logView.text = "UI validation only\nThreads: " + threads +
            "\nDP bits: " + (dp?.toString() ?: "automatic") +
            "\nConfig length: " + config.length +
            " characters\n\nNOT RUNNING: the original C++ solver is not yet ported and linked for Android ARM64."
    }

    private fun nativeStatusSafely(): String = try {
        nativeEngineStatus()
    } catch (_: UnsatisfiedLinkError) {
        "Native library unavailable • configuration UI only"
    }

    private fun field(value: String) = EditText(this).apply {
        setText(value)
        textSize = 14f
        setPadding(dp(10), dp(8), dp(10), dp(8))
    }

    private fun label(value: String, size: Int, bold: Boolean) = TextView(this).apply {
        text = value
        textSize = size.toFloat()
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setPadding(0, dp(7), 0, dp(7))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
