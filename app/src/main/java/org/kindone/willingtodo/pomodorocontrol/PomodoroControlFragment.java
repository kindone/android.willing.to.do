package org.kindone.willingtodo.pomodorocontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kindone.willingtodo.MainActivity;
import org.kindone.willingtodo.R;
import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.pomodorotimer.StopWatch;

public class PomodoroControlFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PomoCon";
    private static final String PREF_ISRUNNING = "timer.isRunning";
    private static final String PREF_DURATIONMS = "timer.durationMs";
    private static final String PREF_STARTEDTIMEMS = "timer.startedTimeMs";
    private static final String PREF_TITLE = "timer.title";

    private static final boolean defaultIsRunning = false;
    private static final long defaultDuration = 0L;
    private static final long defaultStartedTime = 0L;
    private static final String defaultTitle = "";

    private StopWatch mStopWatch;

    private OnPomodoroControlListener mListener;
    private TextView mTitleView;
    private TextView mTickView;
    private View mPauseButton;
    private View mResumeButton;

    public PomodoroControlFragment() {
        initializeClock();
    }

    // Factory method
    public static PomodoroControlFragment create() {
        PomodoroControlFragment fragment = new PomodoroControlFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        saveState();
        super.onPause();
    }

    @Override
    public void onResume() {
        loadState();
        super.onResume();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeView(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initializeFragmentLayout(inflater, container);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // may throw cast exception
        attachEventListener((OnPomodoroControlListener) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detachEventListener();
    }


    public void startTimer(Task task, long maxDuration) {
        setTitle(task.title);
        mStopWatch.startTimer(maxDuration);
    }

    private void restartTimer(long durationMs, long startedTimeMs) {
        mStopWatch.restartTimer(durationMs, startedTimeMs);
    }



    private void initializeClock() {
        mStopWatch = new StopWatch(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTick();
                    }
                });
            }
        });
    }

    private void initializeView(View layout) {
//        final LinearLayout layout = (LinearLayout) view;
        initializeTitleView(layout);
        initializeCloseButton(layout);
        initializePauseButton(layout);
        initializeResumeButton(layout);
        initializeTickView(layout);
    }

    private View initializeFragmentLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_pomodoro_control, container, false);
    }


    private void initializeTitleView(View layout) {
        mTitleView = (TextView) layout.findViewById(R.id.pomodoro_item_title);
        mTitleView.setText("Blah");

    }

    private void initializeCloseButton(View layout)
    {
        View closeButton = layout.findViewById(R.id.pomodoro_control_close);
        closeButton.setOnClickListener(new OnCloseButtonClicked());
    }

    private void initializePauseButton(View layout)
    {
        mPauseButton = (View) layout.findViewById(R.id.button_pause);
        mPauseButton.setOnClickListener(new OnPauseButtonClicked());
    }

    private void initializeResumeButton(View layout)
    {
        mResumeButton = (View) layout.findViewById(R.id.button_play);
        mResumeButton.setOnClickListener(new OnResumeButtonClicked());
    }

    private void initializeTickView(View layout)
    {
        mTickView = (TextView) layout.findViewById(R.id.pomodoro_control_tick);
    }


    private void updateTick() {
        setTickValue(mStopWatch.getRemainingTimeStr());
        Log.v(TAG, mStopWatch.getRemainingTimeStr());
    }


    private void setTitle(String title)
    {
        mTitleView.setText(title);
    }

    private void setTickValue(String tickText)
    {
        mTickView.setText(tickText);
    }

    private void showPauseButton()
    {
        mPauseButton.setVisibility(View.VISIBLE);
        mResumeButton.setVisibility(View.INVISIBLE);
    }

    private void showResumeButton()
    {
        mPauseButton.setVisibility(View.INVISIBLE);
        mResumeButton.setVisibility(View.VISIBLE);
    }


    class OnCloseButtonClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            mStopWatch.stopTimer();
            dispatchClosePomodoroControlEvent();
        }
    }

    class OnPauseButtonClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "pause button");
            if(mStopWatch.isRunning()) {
                mStopWatch.pauseTimer();
                dispatchPausePomodoroTimerEvent(mStopWatch.getRemainingTimeMs());
            }
            showResumeButton();
        }
    }

    class OnResumeButtonClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "play button");
            mStopWatch.resumeTimer();
            dispatchResumePomodoroTimerEvent();

            if(!mStopWatch.isRunning())
                updateTick();

            showPauseButton();
        }
    }



    private void saveState() {
        SharedPreferences.Editor prefs = getPreferenceEditor();
        writeIsRunningPref(prefs);
        writeDurationPref(prefs);
        writeStartedTimePref(prefs);
        writeTitlePref(prefs);
        writeTitlePref(prefs);
        prefs.commit();
    }

    private void loadState() {
        SharedPreferences prefs = getPreference();
        setTitle(readTitlePref(prefs));

        boolean isRunning = readIsRunningPref(prefs);

        if (isRunning) {
            long durationMs = readDurationPref(prefs);
            long startedTimeMs = readStartedTimePref(prefs);

            if (isTimeDurationStillValid(durationMs, startedTimeMs)) {
                restartTimer(durationMs, startedTimeMs);
            }
            dispatchShowPomodoroControlEvent();
        } else {
            dispatchHidePomodoroControlEvent();
        }
    }

    private SharedPreferences getPreference()
    {
        return getActivity().getSharedPreferences(MainActivity.PREF_FILENAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getPreferenceEditor()
    {
        return getActivity().getSharedPreferences(MainActivity.PREF_FILENAME, Context.MODE_PRIVATE).edit();
    }

    private void writeTitlePref(SharedPreferences.Editor prefs)
    {
        prefs.putString(PREF_TITLE, mTitleView.getText().toString());
    }

    private String readTitlePref(SharedPreferences prefs)
    {

        return prefs.getString(PREF_TITLE, defaultTitle);
    }

    private void writeIsRunningPref(SharedPreferences.Editor prefs)
    {
        prefs.putBoolean(PREF_ISRUNNING, mStopWatch.isRunning());
    }

    private boolean readIsRunningPref(SharedPreferences prefs)
    {
        return prefs.getBoolean(PREF_ISRUNNING, defaultIsRunning);
    }

    private void writeDurationPref(SharedPreferences.Editor prefs)
    {
        prefs.putLong(PREF_DURATIONMS, mStopWatch.getDurationMs());
    }

    private long readDurationPref(SharedPreferences prefs)
    {
        return prefs.getLong(PREF_DURATIONMS, defaultDuration);
    }

    private void writeStartedTimePref(SharedPreferences.Editor prefs)
    {
        prefs.putLong(PREF_STARTEDTIMEMS, mStopWatch.getStartedTimeMs());
    }

    private long readStartedTimePref(SharedPreferences prefs)
    {
        return prefs.getLong(PREF_STARTEDTIMEMS, defaultStartedTime);
    }


    private void attachEventListener(OnPomodoroControlListener listener)
    {
        mListener = listener;
    }

    private void detachEventListener()
    {
        mListener = null;
    }


    private void dispatchShowPomodoroControlEvent() {
        mListener.onShowPomodoroControl();
    }

    private void dispatchHidePomodoroControlEvent() {
        mListener.onHidePomodoroControl();
    }

    private void dispatchClosePomodoroControlEvent() {
        mListener.onClosePomodoroControl();
    }

    private void dispatchPausePomodoroTimerEvent(long remainingTimeMs) {
        mListener.onPausePomodoroTimer(remainingTimeMs);
    }

    private void dispatchResumePomodoroTimerEvent()
    {
        mListener.onResumePomodoroTimer();
    }




    private boolean isTimeDurationStillValid(long durationMs, long startedTimeMs) {
        long now = System.currentTimeMillis();
        long elapsedTimeMs = now - startedTimeMs;
        return elapsedTimeMs < durationMs;
    }


    public interface OnPomodoroControlListener {

        void onResumePomodoroTimer();
        void onPausePomodoroTimer(long remainingTimeMs);
        void onStopPomodoroTimer();
        void onClosePomodoroControl();
        void onShowPomodoroControl();
        void onHidePomodoroControl();
    }
}
