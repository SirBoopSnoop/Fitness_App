package com.example.testapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.testapplication.databinding.ActivityEditBinding
import com.example.testapplication.databinding.ActivityTimerBinding
import com.google.firebase.database.FirebaseDatabase
import java.math.RoundingMode
import kotlin.math.roundToLong

class TimerActivity : AppCompatActivity() {

    private lateinit var path: String
    lateinit var timer : CountDownTimer
    lateinit var timerText : TextView
    lateinit var button: Button
    lateinit var overview: TextView
    lateinit var message: TextView
    var intensity : Double = 0.0
    var pace : Long = 0
    var reps : Long = 0
    var counter :Int = 0
    var setCounter : Int = 1
    var breakTime : Long = 0
    var sets : Int = 0
    private lateinit var binding: ActivityTimerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("timerKey")
            path = value.toString()
        }

        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(path).get().addOnSuccessListener {

            if (it.exists()) {
                breakTime = it.child("breakTime").value as Long
                val intensity1 = it.child("intensity").value as Long
                reps = it.child("reps").value as Long
                val sets1 = it.child("sets").value as Long

                sets = sets1.toInt()
                intensity = intensity1.toDouble()*1000
            }
        }*/


        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reps = 10
        sets = 3
        intensity = ((0.5)*1000)
        breakTime = 5

        pace = intensity.roundToLong()


        overview = binding.textView3
        timerText = binding.textView
        button = binding.button
        message = binding.textView4

        //message.text = path + "  " + breakTime.toString() + "  " + intensity.toString() + "  " + reps.toString() + "  " + sets.toString()


        overview.text = "Set: $setCounter / $sets"

        button.setOnClickListener {
            startTimer()
        }
    }

    private fun startTimer(){
        message.text = "GO"
        timer = object : CountDownTimer(pace*reps, pace){
            override fun onTick(remaining: Long) {
                timerText.text = counter.toString()
                counter++
            }

            override fun onFinish() {
                if (setCounter != sets) {
                    message.text = "Get ready for the next set"
                    counter = 0
                    breakCountdown()
                }else{
                    timerText.text = ""
                    message.text = "Exercise done!"
                }
            }
        }

        timer.start()
    }

    private fun breakCountdown(){
        setCounter++
        overview.text = "Set: $setCounter / $sets"
        timer = object : CountDownTimer(breakTime*1000, 1){
            override fun onTick(remaining: Long) {
                timerText.text = remaining.div(1000).toBigDecimal().setScale(0, RoundingMode.UP).toString()
            }

            override fun onFinish() {
                message.text = ""

                startTimer()
            }
        }
        timer.start()
    }
}