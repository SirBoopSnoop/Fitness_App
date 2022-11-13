package com.example.testapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityCreateBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create.*
import com.example.testapplication.model.Exercise


class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding //defining the binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater) //initializing the binding class
        setContentView(binding.root) //contentView as binding.root

        val dropdown : Spinner = binding.categoryDropdown
        ArrayAdapter.createFromResource(
            this,
            R.array.category_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dropdown.adapter = adapter
        }

        // Get radio group selected status and text using button click event
        binding.saveButton.setOnClickListener {
            // Get the checked radio button id from radio group
            val category = categoryDropdown.selectedItem
            if (category != null) { //If a category has been selected
                createExercise()
            } else {
                // If none of the options are selected
                Toast.makeText(
                    applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }
        }

        //Makes the cancel button goes back to the main activity
        binding.cancelButton.setOnClickListener {finish()}
    }

    private fun createExercise(){
        val exerciseName = binding.exerciseName.text.toString()
        val reps = binding.repsValue.text.toString().toInt()
        val sets = binding.setsValue.text.toString().toInt()
        val intensity = binding.intensityValue.text.toString().toDouble()
        val breakTime = binding.breakTimeValue.text.toString().toInt()
        val category = binding.categoryDropdown.selectedItem.toString()
        val videoUrl = binding.youtubeLink.text.toString()

        val exercise = Exercise(exerciseName, exerciseName,videoUrl, reps, sets, intensity, breakTime, category, null)
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
        val testData = database.getReference("TestData")
        testData.child(exerciseName).setValue(exercise).addOnSuccessListener {
            Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }
    }

/*    private fun exerciseDataSender() {
        val senderIntent = Intent(this, MainActivity::class.java).also {
            it.putExtra("NAME_MESSAGE", binding.exerciseName.text.toString())
            it.putExtra("REPS_MESSAGE", binding.repsValue.text.toString())
            it.putExtra("SETS_MESSAGE", binding.setsValue.text.toString())
            it.putExtra("INTENSITY_MESSAGE", binding.intensityValue.text.toString())
            it.putExtra("BREAK_MESSAGE", binding.breakTimeValue.text.toString())
            val id: Int = category_group.checkedRadioButtonId
            val selectedRadio: RadioButton = findViewById(id)
            it.putExtra("RADIO_MESSAGE", selectedRadio.text.toString())
            startActivity(it)
        }


        val index = 1
        val newItem = ExerciseModel(binding.exerciseName.text.toString())
        val mainActivity = MainActivity()
        mainActivity.exerciseList.add(index, newItem)
        mainActivity.adapter.notifyItemInserted(index)
    }*/
}