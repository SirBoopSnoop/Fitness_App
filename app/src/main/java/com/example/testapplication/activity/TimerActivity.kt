package com.example.testapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityTimerBinding
import com.example.testapplication.fragment.YouTubeFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_viewexercise.*
import java.math.RoundingMode
import kotlin.math.roundToLong

class TimerActivity : AppCompatActivity() {

    private lateinit var exercisePath: String
    lateinit var timer : CountDownTimer
    lateinit var timerText : TextView
    lateinit var button: Button
    lateinit var overview: TextView
    lateinit var message: TextView
    lateinit var countdown : TextView
    lateinit var reset : Button
    lateinit var beep : MediaPlayer
    lateinit var beepLong : MediaPlayer
    lateinit var finish : MediaPlayer
    var category : String? = ""
    var intensity : Double? = 0.0
    var pace : Long? = 0
    var reps : Long? = 0
    var counter :Int = 0
    var breakTime : Long? = 0
    var sets : Int? = 0
    var switch : Boolean = false
    var isRunning : Boolean = false
    var setCounter : Int = 1
    private lateinit var binding: ActivityTimerBinding
    private var database : DatabaseReference = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("timerKey")
            exercisePath = value.toString()
        }
        setData(exercisePath)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        finish = MediaPlayer.create(this, R.raw.finish)
        beep = MediaPlayer.create(this, R.raw.beep)
        beepLong = MediaPlayer.create(this, R.raw.beep_long)
        overview = binding.setOverview
        timerText = binding.repCounter
        countdown = binding.countdown
        button = binding.buttonStart
        message = binding.message
        reset = binding.buttonReset

        button.setOnClickListener {
            if(!switch) {
                beepLong.start()
                if (category != "Cardio") {
                    startTimer()
                }else{
                    cardioCountdown()
                }
            }
            switch = true
        }
        reset.setOnClickListener {
            switch = false
            setData(exercisePath)
            setCounter = 1
            counter = 0
            timerText.text = ""
            countdown.text = ""
            message.text = ""
            if(isRunning){
            timer.cancel()
            }
        }

        bottom_navigation.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.backpress -> {
                    Intent(this, ViewExerciseActivity::class.java).also {
                        stopCounting()
                        it.putExtra("viewKey", exercisePath)
                        startActivity(it)
                        finish()
                    }
                }

                R.id.home -> {
                    Intent(this, MainActivity::class.java).also {
                        stopCounting()
                        finish()
                        startActivity(it)
                    }
                }

                R.id.video -> {
                    Intent(this, YouTubeFragment::class.java).also {
                        it.putExtra("videoKey", exercisePath)
                        stopCounting()
                        finish()
                        startActivity(it)
                    }
                }

                R.id.edit -> {
                    Intent(this, EditActivity::class.java).also {
                        it.putExtra("key", exercisePath)
                        stopCounting()
                        finish()
                        startActivity(it)
                    }
                }
            }
            true
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setData(path:String){

        database.child(path).get().addOnSuccessListener {

            if (it.exists()) {
                category = it.child("category").getValue(String::class.java)
                breakTime = it.child("breakTime").getValue(Long::class.java)
                intensity = it.child("intensity").getValue(Double::class.java)?.times(1000)
                if (category != "Cardio") {
                    reps = it.child("reps").getValue(Long::class.java)
                }
                sets = it.child("sets").getValue(Int::class.java)
                pace = intensity?.roundToLong()
                overview.text = "Set: $setCounter / $sets"
            }
        }

    }

    private fun startTimer(){
        message.text = "GO"
        countdown.text = ""
        timer = object : CountDownTimer(reps?.let { pace?.times(it) }!!, pace!!){

            override fun onTick(remaining: Long) {
                isRunning = true
                timerText.text = counter.toString()
                if (counter != 0) {
                    beep.start()
                }
                counter++
            }

            override fun onFinish() {
                isRunning = false
                timerText.text = counter.toString()
                beep.start()
                if (setCounter != sets) {
                    message.text = "Get ready for the next set"
                    counter = 0
                    breakCountdown()
                }else{
                    timerText.text = ""
                    message.text = "Exercise done!"
                    finish.start()
                }
            }
        }

        timer.start()
    }

    private fun breakCountdown(){
        setCounter++
        timer = object : CountDownTimer(breakTime?.times(1000)!!, 1){
            override fun onTick(remaining: Long) {
                isRunning = true
                countdown.text = remaining.div(1000).toBigDecimal().setScale(0, RoundingMode.UP).toString()
            }

            override fun onFinish() {
                overview.text = "Set: $setCounter / $sets"
                isRunning = false
                message.text = ""
                beepLong.start()
                if (category != "Cardio") {
                    startTimer()
                }else{
                    cardioCountdown()
                }
            }
        }
        timer.start()
    }

    private fun cardioCountdown(){
        message.text = "GO"
        countdown.text = ""
        timerText.text = ""
        timer = object : CountDownTimer(intensity?.toLong()!!, 1){
            override fun onTick(remaining: Long) {
                isRunning = true
                countdown.text = remaining.div(1000).toBigDecimal().setScale(0, RoundingMode.UP).toString()
            }

            override fun onFinish() {
                isRunning = false
                timerText.text = counter.toString()
                if (setCounter != sets) {
                    beepLong.start()
                    message.text = "Get ready for the next set"
                    counter = 0
                    breakCountdown()
                }else{
                    timerText.text = ""
                    message.text = "Exercise done!"
                    finish.start()
                }
            }
        }
        timer.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopCounting()

    }

    override fun onResume() {
        super.onResume()
        stopCounting()
    }

    private fun stopCounting(){
        if (isRunning){
            timer.cancel()
        }
        switch = false
        setData(exercisePath)
        setCounter = 1
        counter = 0
        timerText.text = ""
        countdown.text = ""
        message.text = ""
    }
}