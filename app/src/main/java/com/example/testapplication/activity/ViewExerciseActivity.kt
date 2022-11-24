package com.example.testapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var binding: ActivityViewexerciseBinding
    private lateinit var exercisePath: String

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.video_item -> {
                Intent(this, YouTubeFragment::class.java).also {
                    it.putExtra("videoKey", exercisePath)
                    startActivity(it)
                    finish()
                }
            }

            R.id.edit_item -> {
                Intent(this, EditActivity::class.java).also {
                    it.putExtra("key", exercisePath)
                    startActivity(it)
                    finish()
                }
            }
            R.id.remove_item -> {

                //Check if the user is connected to the database or not
                if (!isNetworkAvailable()) {
                    Toast.makeText(
                        baseContext, "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Delete exercise")
                        .setMessage("Are you sure you want to delete this exercise")
                        .setNegativeButton("Cancel") { dialog, which ->
                            showSnackBar("Exercise has not been deleted.")
                        }
                        .setPositiveButton("Yes") { dialog, which ->
                            deleteExercise()
                        }.show()
                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewexerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras: Bundle? = intent.extras
        if (extras != null) {
            val exerciseId: String? = extras.getString("viewKey")
            exercisePath = exerciseId.toString()
            viewData(exercisePath)
        }

        binding.descriptionButton.setOnClickListener {
            Intent(this, AddDescriptionActivity::class.java).also {
                it.putExtra("descKey", exercisePath)
                startActivity(it)
            }
        }

        bottom_navigation.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.home -> {Intent(this, MainActivity::class.java).also {
                    finish()
                    startActivity(it)
                    }
                }

                R.id.video -> {
                    Intent(this, YouTubeFragment::class.java).also {
                        it.putExtra("videoKey", exercisePath)
                        startActivity(it)
                        finish()
                    }
                }

                R.id.edit -> {
                    Intent(this, EditActivity::class.java).also {
                        it.putExtra("key", exercisePath)
                        startActivity(it)
                        finish()
                    }
                }

                R.id.timer -> {
                    Intent(this, TimerActivity::class.java).also {
                        it.putExtra("timerKey", exercisePath)
                        startActivity(it)
                        finish()
                    }
                }

                R.id.backpress -> {
                    Intent(this, ViewExerciseActivity::class.java).also {
                        finish()
                    }
                }
            }
            true
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val extras : Bundle? = intent!!.extras
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

            }
        }
    }

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