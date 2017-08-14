package org.kindone.willingtodo

import android.content.Context
import org.kindone.willingtodo.data.Task

/**
 * Created by kindone on 2017. 8. 12..
 */

class PomodoroServiceStarter(private val context:Context)
{
    private val mIntentFactory = PomodoroServiceIntentFactory(context)

    fun startPomodoroTimerService(task: Task) {
        val intent = mIntentFactory.createIntentForPomodoroTimerServiceStart(task)
        context.startService(intent)
    }

    fun resumePomodoroTimerService() {
        val intent = mIntentFactory.createIntentForPomodoroTimerServiceResume()
        context.startService(intent)
    }

    fun pausePomodoroTimerService(remainingTimeMs: Long) {
        val intent = mIntentFactory.createIntentForPomodoroTimerServicePause(remainingTimeMs)
        context.startService(intent)
    }

    fun stopPomodoroTimerService() {
        val intent = mIntentFactory.createIntentForPomodoroTimerServiceStop()
        context.startService(intent)
    }


}