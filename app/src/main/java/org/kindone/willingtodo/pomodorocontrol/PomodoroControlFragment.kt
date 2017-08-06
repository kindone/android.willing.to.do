package org.kindone.willingtodo.pomodorocontrol

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.kindone.willingtodo.MainActivity
import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.pomodorotimer.StopWatch

class PomodoroControlFragment : Fragment() {

    private var mStopWatch: StopWatch? = null

    private var mListener: OnPomodoroControlListener? = null
    private var mTitleView: TextView? = null
    private var mTickView: TextView? = null
    private var mPauseButton: View? = null
    private var mResumeButton: View? = null

    init {
        initializeClock()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        saveState()
        super.onPause()
    }

    override fun onResume() {
        loadState()
        super.onResume()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView(view!!)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return initializeFragmentLayout(inflater!!, container!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // may throw cast exception
        attachEventListener(context as OnPomodoroControlListener)
    }

    override fun onDetach() {
        super.onDetach()
        detachEventListener()
    }


    fun startTimer(task: Task, maxDuration: Long) {
        setTitle(task.title)
        mStopWatch!!.startTimer(maxDuration)
    }

    private fun restartTimer(durationMs: Long, startedTimeMs: Long) {
        mStopWatch!!.restartTimer(durationMs, startedTimeMs)
    }


    private fun initializeClock() {
        mStopWatch = StopWatch(Runnable { activity.runOnUiThread { updateTick() } })
    }

    private fun initializeView(layout: View) {
        //        final LinearLayout layout = (LinearLayout) view;
        initializeTitleView(layout)
        initializeCloseButton(layout)
        initializePauseButton(layout)
        initializeResumeButton(layout)
        initializeTickView(layout)
    }

    private fun initializeFragmentLayout(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.fragment_pomodoro_control, container, false)
    }


    private fun initializeTitleView(layout: View) {
        mTitleView = layout.findViewById(R.id.pomodoro_item_title) as TextView
        mTitleView!!.text = "Blah"

    }

    private fun initializeCloseButton(layout: View) {
        val closeButton = layout.findViewById(R.id.pomodoro_control_close)
        closeButton.setOnClickListener(OnCloseButtonClicked())
    }

    private fun initializePauseButton(layout: View) {
        mPauseButton = layout.findViewById(R.id.button_pause)
        mPauseButton!!.setOnClickListener(OnPauseButtonClicked())
    }

    private fun initializeResumeButton(layout: View) {
        mResumeButton = layout.findViewById(R.id.button_play)
        mResumeButton!!.setOnClickListener(OnResumeButtonClicked())
    }

    private fun initializeTickView(layout: View) {
        mTickView = layout.findViewById(R.id.pomodoro_control_tick) as TextView
    }


    private fun updateTick() {
        setTickValue(mStopWatch!!.remainingTimeStr)
        Log.v(TAG, mStopWatch!!.remainingTimeStr)
    }


    private fun setTitle(title: String) {
        mTitleView!!.text = title
    }

    private fun setTickValue(tickText: String) {
        mTickView!!.text = tickText
    }

    private fun showPauseButton() {
        mPauseButton!!.visibility = View.VISIBLE
        mResumeButton!!.visibility = View.INVISIBLE
    }

    private fun showResumeButton() {
        mPauseButton!!.visibility = View.INVISIBLE
        mResumeButton!!.visibility = View.VISIBLE
    }


    internal inner class OnCloseButtonClicked : View.OnClickListener {
        override fun onClick(v: View) {
            mStopWatch!!.stopTimer()
            dispatchClosePomodoroControlEvent()
        }
    }

    internal inner class OnPauseButtonClicked : View.OnClickListener {
        override fun onClick(v: View) {
            Log.v(TAG, "pause button")
            if (mStopWatch!!.isRunning) {
                mStopWatch!!.pauseTimer()
                dispatchPausePomodoroTimerEvent(mStopWatch!!.remainingTimeMs)
            }
            showResumeButton()
        }
    }

    internal inner class OnResumeButtonClicked : View.OnClickListener {
        override fun onClick(v: View) {
            Log.v(TAG, "play button")
            mStopWatch!!.resumeTimer()
            dispatchResumePomodoroTimerEvent()

            if (!mStopWatch!!.isRunning)
                updateTick()

            showPauseButton()
        }
    }


    private fun saveState() {
        val prefs = preferenceEditor
        writeIsRunningPref(prefs)
        writeDurationPref(prefs)
        writeStartedTimePref(prefs)
        writeTitlePref(prefs)
        writeTitlePref(prefs)
        prefs.commit()
    }

    private fun loadState() {
        val prefs = preference
        setTitle(readTitlePref(prefs))

        val isRunning = readIsRunningPref(prefs)

        if (isRunning) {
            val durationMs = readDurationPref(prefs)
            val startedTimeMs = readStartedTimePref(prefs)

            if (isTimeDurationStillValid(durationMs, startedTimeMs)) {
                restartTimer(durationMs, startedTimeMs)
            }
            dispatchShowPomodoroControlEvent()
        } else {
            dispatchHidePomodoroControlEvent()
        }
    }

    private val preference: SharedPreferences
        get() = activity.getSharedPreferences(MainActivity.PREF_FILENAME, Context.MODE_PRIVATE)

    private val preferenceEditor: SharedPreferences.Editor
        get() = activity.getSharedPreferences(MainActivity.PREF_FILENAME, Context.MODE_PRIVATE).edit()

    private fun writeTitlePref(prefs: SharedPreferences.Editor) {
        prefs.putString(PREF_TITLE, mTitleView!!.text.toString())
    }

    private fun readTitlePref(prefs: SharedPreferences): String {

        return prefs.getString(PREF_TITLE, defaultTitle)
    }

    private fun writeIsRunningPref(prefs: SharedPreferences.Editor) {
        prefs.putBoolean(PREF_ISRUNNING, mStopWatch!!.isRunning)
    }

    private fun readIsRunningPref(prefs: SharedPreferences): Boolean {
        return prefs.getBoolean(PREF_ISRUNNING, defaultIsRunning)
    }

    private fun writeDurationPref(prefs: SharedPreferences.Editor) {
        prefs.putLong(PREF_DURATIONMS, mStopWatch!!.getDurationMs())
    }

    private fun readDurationPref(prefs: SharedPreferences): Long {
        return prefs.getLong(PREF_DURATIONMS, defaultDuration)
    }

    private fun writeStartedTimePref(prefs: SharedPreferences.Editor) {
        prefs.putLong(PREF_STARTEDTIMEMS, mStopWatch!!.startedTimeMs)
    }

    private fun readStartedTimePref(prefs: SharedPreferences): Long {
        return prefs.getLong(PREF_STARTEDTIMEMS, defaultStartedTime)
    }


    private fun attachEventListener(listener: OnPomodoroControlListener) {
        mListener = listener
    }

    private fun detachEventListener() {
        mListener = null
    }


    private fun dispatchShowPomodoroControlEvent() {
        mListener!!.onShowPomodoroControl()
    }

    private fun dispatchHidePomodoroControlEvent() {
        mListener!!.onHidePomodoroControl()
    }

    private fun dispatchClosePomodoroControlEvent() {
        mListener!!.onClosePomodoroControl()
    }

    private fun dispatchPausePomodoroTimerEvent(remainingTimeMs: Long) {
        mListener!!.onPausePomodoroTimer(remainingTimeMs)
    }

    private fun dispatchResumePomodoroTimerEvent() {
        mListener!!.onResumePomodoroTimer()
    }


    private fun isTimeDurationStillValid(durationMs: Long, startedTimeMs: Long): Boolean {
        val now = System.currentTimeMillis()
        val elapsedTimeMs = now - startedTimeMs
        return elapsedTimeMs < durationMs
    }


    interface OnPomodoroControlListener {

        fun onResumePomodoroTimer()
        fun onPausePomodoroTimer(remainingTimeMs: Long)
        fun onStopPomodoroTimer()
        fun onClosePomodoroControl()
        fun onShowPomodoroControl()
        fun onHidePomodoroControl()
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val TAG = "PomoCon"
        private val PREF_ISRUNNING = "timer.isRunning"
        private val PREF_DURATIONMS = "timer.durationMs"
        private val PREF_STARTEDTIMEMS = "timer.startedTimeMs"
        private val PREF_TITLE = "timer.title"

        private val defaultIsRunning = false
        private val defaultDuration = 0L
        private val defaultStartedTime = 0L
        private val defaultTitle = ""

        // Factory method
        fun create(): PomodoroControlFragment {
            val fragment = PomodoroControlFragment()
            return fragment
        }
    }
}
