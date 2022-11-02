package com.example.testapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_viewexercise.*
import java.util.regex.Pattern
import kotlin.String

class ViewExerciseActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewexerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras : Bundle? = intent.extras
        if (extras != null){
            var value : String? = extras.getString("key")
            val path = value.toString()
            val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
            database.child(path).get().addOnSuccessListener {

                if(it.exists()){

                    val exerciseName = it.child("exerciseName").value.toString()
                    val breakTime = it.child("breakTime").value.toString()
                    val category = it.child("category").value.toString()
                    val intensity = it.child("intensity").value.toString()
                    val reps = it.child("reps").value.toString()
                    val sets = it.child("sets").value.toString()

                    binding.exerciseNameView.text = exerciseName
                    binding.breakView.append(breakTime)
                    binding.repsView.append(reps)
                    binding.setsView.append(sets)

                }
            }
        }


        initializePlayer(getYoutubeVideoIdFromUrl("https://www.youtube.com/watch?v=DkWokwdxCIU")!!)
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