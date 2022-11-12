package com.example.testapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.testapplication.YouTubeConfig
import com.example.testapplication.databinding.ActivityViewexerciseBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_viewexercise.*
import java.util.regex.Pattern
import kotlin.String

class ViewExerciseActivity : YouTubeBaseActivity() {

    private lateinit var binding : ActivityViewexerciseBinding
    private lateinit var path: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewexerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("viewKey")
            path = value.toString()
            viewData(path)
        }

        initializePlayer(getYoutubeVideoIdFromUrl("https://www.youtube.com/watch?v=hUHQdQfjlSo")!!)


        cancel_view_button.setOnClickListener {
            finish()
        }

        binding.editButton.setOnClickListener {
            Intent(this, EditActivity::class.java).also{
                it.putExtra("key", path)
                startActivity(it)
            }
        }

        binding.descriptionButton.setOnClickListener {
            Intent(this, AddDescriptionActivity::class.java).also{
                it.putExtra("descKey", path)
                startActivity(it)
            }
        }

        binding.deleteViewButton.setOnClickListener {
            deleteExercise()
        }

        binding.startButton.setOnClickListener {
            Intent(this, TimerActivity::class.java).also{
                it.putExtra("timerKey", path)
                startActivity(it)
            }
        }

    }

    private fun deleteExercise() {
        val databaseRef = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        databaseRef.child(path).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Exercise data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("viewKey")
            path = value.toString()
        }
        viewData(path)
    }

    @SuppressLint("SetTextI18n")
    private fun viewData(path:String){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(path).get().addOnSuccessListener {

            if(it.exists()){
                val exerciseName = it.child("exerciseName").value.toString()
                val breakTime = it.child("breakTime").value.toString()
                val description = it.child("description").value.toString()
                val category = it.child("category").value.toString()
                val intensity = it.child("intensity").value.toString()
                val reps = it.child("reps").value.toString()
                val sets = it.child("sets").value.toString()

                binding.exerciseNameView.text = exerciseName
                binding.breakView.text = "Break time: $breakTime"
                binding.repsView.text = "Reps: $reps"
                binding.setsView.text = "Sets: $sets"
                binding.description.text = description
                binding.intensityView.text = "Intensity: $intensity"

            }
        }
    }

    private fun initializePlayer(videoId : String){
        val config = YouTubeConfig()
        youtubePlay.initialize(config.API_KEY,object :  YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1!!.loadVideo(videoId)
                p1.play()
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(applicationContext, "Failed to initialize player", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getYoutubeVideoIdFromUrl(inUrl : String): String?{
        if (inUrl.lowercase().contains("youtu.be")){
            return inUrl.substring(inUrl.lastIndexOf("/") + 1)
        }
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(inUrl)
        return if (matcher.find()){
            matcher.group()
        }else null
    }
}