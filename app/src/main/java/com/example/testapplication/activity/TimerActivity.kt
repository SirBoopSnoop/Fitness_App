package com.example.testapplication.activity

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityEditBinding
import com.example.testapplication.databinding.ActivityTimerBinding
import com.example.testapplication.model.Exercise
import com.google.android.gms.common.api.Response
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode
import kotlin.math.roundToLong

class TimerActivity : AppCompatActivity() {


    private lateinit var path: String
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
    var intensity : Double? = 0.0
    var pace : Long? = 0
    var reps : Long? = 0
    var counter :Int = 0
    var setCounter : Int = 1
    var breakTime : Long? = 0
    var sets : Int? = 0
    var switch : Boolean = false
    private lateinit var binding: ActivityTimerBinding
    private var database : DatabaseReference = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("timerKey")
            path = value.toString()
        }
        setData(path)

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
                startTimer()
            }
            switch = true
        }
        reset.setOnClickListener {
            switch = false
            setData(path)
            setCounter = 1
            counter = 0
            timerText.text = ""
            countdown.text = ""
            message.text = ""
            timer.cancel()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setData(path:String){

        database.child(path).get().addOnSuccessListener {

            if (it.exists()) {
                breakTime = it.child("breakTime").getValue(Long::class.java)
                intensity = it.child("intensity").getValue(Double::class.java)?.times(1000)
                reps = it.child("reps").getValue(Long::class.java)
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
                timerText.text = counter.toString()
                if (counter != 0) {
                    beep.start()
                }
                counter++
            }

            override fun onFinish() {
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
        overview.text = "Set: $setCounter / $sets"
        timer = object : CountDownTimer(breakTime?.times(1000)!!, 1){
            override fun onTick(remaining: Long) {
                countdown.text = remaining.div(1000).toBigDecimal().setScale(0, RoundingMode.UP).toString()
            }

            override fun onFinish() {
                message.text = ""
                beepLong.start()
                startTimer()
            }
        }
        timer.start()
    }
}