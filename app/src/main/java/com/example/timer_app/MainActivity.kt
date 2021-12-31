package com.example.timer_app

import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.timer_app.Model.TimerState
import com.example.timer_app.databinding.ActivityMainBinding
import com.example.timer_app.util.PrefUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var play: FloatingActionButton
    private lateinit var puase: FloatingActionButton
    private lateinit var stop: FloatingActionButton

    private lateinit var progress_countdown: MaterialProgressBar
    private lateinit var textView_countdown: TextView

    private lateinit var timer: CountDownTimer
    private var timerLengthSecond: Long = 0 //or 0L
    private var timerState = TimerState.Stopped
    private var secondRemaining = 0L //Long


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        connectView()

        //to display the image in right...
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "       Timer"

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        play.setOnClickListener {
            startTimer()
            timerState = TimerState.Running
            updateButton()
        }

        puase.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Paused
            updateButton()
        }

        stop.setOnClickListener {
            timer.cancel()
            onTimerFinished()

        }
    }


    override fun onResume() {
        super.onResume()

        initTimer()
        //remove background timer, hide notification
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running) {
            timer.cancel()

            //start background timer, show notification
        } else if (timerState == TimerState.Paused) {
            //show notification
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSecond, this)
        PrefUtil.setSecondsRemaining(secondRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }


    private fun connectView() {
        play = findViewById(R.id.fab_play)
        puase = findViewById(R.id.fab_pause)
        stop = findViewById(R.id.fab_stop)
        progress_countdown = findViewById(R.id.progress_countdown)
        textView_countdown = findViewById(R.id.textView_countDown)

    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded
        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSecond

        //TODO: change secondsRemaining according to where the background timer stopped

        //resume where we left off
        if (timerState == TimerState.Running)
            startTimer()

        updateButton()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSecond, this)
        secondRemaining = timerLengthSecond

        updateButton()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSecond = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSecond.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSecond = PrefUtil.getPreviousTimerLengthSecond(this)
        progress_countdown.max = timerLengthSecond.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondRemaining / 60
        val secondsInMinuteUntilFinished = secondRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text =
            "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSecond - secondRemaining).toInt()
    }

    private fun updateButton() {
        when (timerState) {
            TimerState.Running -> {
                play.isEnabled = false
                puase.isEnabled = true
                stop.isEnabled = true
            }
            TimerState.Stopped -> {
                play.isEnabled = true
                puase.isEnabled = false
                stop.isEnabled = false
            }
            TimerState.Paused -> {
                play.isEnabled = true
                puase.isEnabled = false
                stop.isEnabled = true
            }
        }
    }


}