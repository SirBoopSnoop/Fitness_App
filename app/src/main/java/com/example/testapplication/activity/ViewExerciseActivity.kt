package com.example.testapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityViewexerciseBinding
import com.example.testapplication.fragment.YouTubeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_viewexercise.*
import kotlin.String

class ViewExerciseActivity : AppCompatActivity() {
    private lateinit var binding : ActivityViewexerciseBinding
    private lateinit var exercisePath: String
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewexerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras : Bundle? = intent.extras
        if (extras != null){
            val exerciseId : String? = extras.getString("viewKey")
            exercisePath = exerciseId.toString()
            viewData(exercisePath)
        }

        //https://www.youtube.com/watch?v=IODxDxX7oi4

        binding.videoButton.setOnClickListener {
            Intent(this, YouTubeFragment::class.java).also{
                it.putExtra("videoKey", exercisePath)
                startActivity(it)
            }
        }

        binding.editButton.setOnClickListener {
            Intent(this, EditActivity::class.java).also{
                it.putExtra("key", exercisePath)
                startActivity(it)
            }
        }

        binding.descriptionButton.setOnClickListener {
            Intent(this, AddDescriptionActivity::class.java).also{
                it.putExtra("descKey", exercisePath)
                startActivity(it)
            }
        }

        binding.deleteViewButton.setOnClickListener  {

            //Check if the user is connected to the database or not
            if(!isNetworkAvailable()) {
                Toast.makeText(
                    baseContext, "Please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete exercise")
                    .setMessage("Are you sure you want to delete this exercise")
                    .setNegativeButton("Cancel") {dialog, which ->
                        showSnackBar("Exercise has not been deleted.")
                    }
                    .setPositiveButton("Yes") {dialog, which ->
                        deleteExercise()
                    }
                    .show()
            }
        }

        binding.startButton.setOnClickListener {
            Intent(this, TimerActivity::class.java).also{
                it.putExtra("timerKey", exercisePath)
                startActivity(it)
            }
        }
    }

    private fun deleteExercise() {
        val databaseRef = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("TestData")
        databaseRef.child(exercisePath).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Exercise has been deleted", Toast.LENGTH_LONG).show()

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
            exercisePath = value.toString()
        }
        viewData(exercisePath)
    }

    @SuppressLint("SetTextI18n")
    private fun viewData(path:String){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(path).get().addOnSuccessListener {
            if(it.exists()){
                var reps = ""
                val exerciseName = it.child("exerciseName").value.toString()
                val breakTime = it.child("breakTime").value.toString()
                var description = it.child("description").value.toString()
                val category = it.child("category").value.toString()
                val intensity = it.child("intensity").value.toString()
                if(category != "Cardio") {
                    reps = it.child("reps").value.toString()
                    binding.repsView.text = "Reps: $reps"
                }else{
                    binding.repsView.visibility = View.GONE
                }
                val sets = it.child("sets").value.toString()
                val video = it.child("videoUrl").value

                binding.exerciseNameView.text = exerciseName
                binding.exerciseCategory.text = category
                binding.breakView.text = "Break time: $breakTime"
                binding.setsView.text = "Sets: $sets"
                binding.intensityView.text = "Intensity: $intensity"

                if (it.child("description").value == null){
                    binding.description.text = ""
                }else{
                    binding.description.text = description
                }

/*                if ((video != null) && (video.toString() != "")){
                getYoutubeVideoIdFromUrl(video.toString())?.let { it1 -> initializePlayer(it1) }
                }else{
                    youtubePlay.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                    errorView.text = "No video has been added for this exercise"}*/

            }
        }
    }

/*    private fun initializePlayer(videoId : String){
        val config = YouTubeConfig()
        youtubePlay.initialize(config.API_KEY,object : YouTubePlayer.OnInitializedListener{
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
                youtubePlay.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                errorView.text = "There was an issue initializing the Youtube player. Reason: $p1"
                println(p1)
            }
        })
    }*/

/*    private fun getYoutubeVideoIdFromUrl(inUrl : String): String?{
        if (inUrl.lowercase().contains("youtu.be")){
            return inUrl.substring(inUrl.lastIndexOf("/") + 1)
        }
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(inUrl)
        return if (matcher.find()){
            matcher.group()
        }else null
    }*/

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable() : Boolean {
        val connection = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connection.getNetworkCapabilities(connection.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }

}