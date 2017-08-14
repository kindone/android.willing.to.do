package org.kindone.willingtodo

import android.content.Context
import android.content.Intent
import org.kindone.willingtodo.data.Task

/**
 * Created by kindone on 2017. 8. 12..
 */
class PomodoroServiceIntentFactory(val context:Context)
{

    fun createIntentForPomodoroTimerServiceStart(task: Task): Intent {
        val intent = Intent(context, PomodoroTimerService::class.java)
        intent.action = PomodoroTimerService.ACTION_START
        intent.putExtra(PomodoroTimerService.ARG_TASK_ID, task.id)
        intent.putExtra(PomodoroTimerService.ARG_TASK_TITLE, task.title)
        intent.putExtra(PomodoroTimerService.ARG_TASK_DURATION_MS, MainActivity.defaultPomodoroDurationMs)
        return intent
    }

    fun createIntentForPomodoroTimerServiceResume(): Intent {
        val intent = Intent(context, PomodoroTimerService::class.java)
        intent.action = PomodoroTimerService.ACTION_RESUME
        return intent
    }

    fun createIntentForPomodoroTimerServicePause(remainingTimeMs: Long): Intent {
        val intent = Intent(context, PomodoroTimerService::class.java)
        intent.action = PomodoroTimerService.ACTION_PAUSE
        intent.putExtra(PomodoroTimerService.ARG_TASK_REMAINING_TIME_MS, remainingTimeMs)
        return intent
    }

    fun createIntentForPomodoroTimerServiceStop(): Intent {
        val intent = Intent(context, PomodoroTimerService::class.java)
        intent.action = PomodoroTimerService.ACTION_STOP
        return intent
    }
}