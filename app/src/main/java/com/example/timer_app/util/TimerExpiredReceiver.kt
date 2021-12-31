package com.example.timer_app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timer_app.Model.TimerState

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: show notification

        PrefUtil.setTimerState(TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}