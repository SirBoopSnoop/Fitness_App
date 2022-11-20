package com.example.testapplication.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.testapplication.R
import com.example.testapplication.YouTubeConfig
import com.example.testapplication.databinding.YoutubeFragmentBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_viewexercise.*
import java.util.regex.Pattern

class YouTubeFragment :  YouTubeBaseActivity(){

    lateinit var binding : YoutubeFragmentBinding
    private lateinit var exercisePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = YoutubeFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val extras : Bundle? = intent.extras
        if (extras != null){
            val exerciseId : String? = extras.getString("videoKey")
            exercisePath = exerciseId.toString()
            viewData(exercisePath)
        }

        binding.cancelViewButton.setOnClickListener {
            finish()
        }

    }

    private fun viewData(path:String){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(path).get().addOnSuccessListener {
            if(it.exists()){
                val video = it.child("videoUrl").value



                if ((video != null) && (video.toString() != "")){
                    getYoutubeVideoIdFromUrl(video.toString())?.let { it1 -> initializePlayer(it1) }
                }else{
                    binding.youtubePlay.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                    errorView.text = "No video has been added for this exercise"}

            }
        }
    }

    private fun initializePlayer(videoId : String){
        val config = YouTubeConfig()
        binding.youtubePlay.initialize(config.API_KEY,object : YouTubePlayer.OnInitializedListener{
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
                binding.youtubePlay.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                errorView.text = "There was an issue initializing the Youtube player. Reason: $p1"
                println(p1)
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