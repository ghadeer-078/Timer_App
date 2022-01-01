package com.example.timer_app.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timer_app.Model.TimerState
import com.example.timer_app.util.NotificationUtil
import com.example.timer_app.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //show notification
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }

}