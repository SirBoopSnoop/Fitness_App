package com.example.testapplication

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.databinding.ActivityCreateBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.view.*
import kotlinx.android.synthetic.main.activity_main.*

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding //defining the binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater) //initializing the binding class
        setContentView(binding.root) //contentView as binding.root

        //Event listener for the selected radio button and sends a toast message of the selected one
        category_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                Toast.makeText(applicationContext,"On checked change: "+
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
            })



        }
            }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }
                finish()
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
            testData.child(exerciseName).setValue(exercise).addOnSuccessListener {
            val testData = database.getReference("TestData")
            val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
            val exercise = Exercise(exerciseName, null, reps, sets, null, intensity, breakTime, category, null)

            val category = binding.root.findViewById<RadioButton>(id).text.toString()
            val id = binding.categoryGroup.checkedRadioButtonId
            val breakTime = binding.breakTimeValue.text.toString().toInt()
            val intensity = binding.intensityValue.text.toString().toDouble()
            val sets = binding.setsValue.text.toString().toInt()
            val reps = binding.repsValue.text.toString().toInt()
            val exerciseName = binding.exerciseName.text.toString()
        fun createExercise(){
        // Get radio group selected status and text using button click event
        binding.saveButton.setOnClickListener {
            // Get the checked radio button id from radio group
            val id: Int = category_group.checkedRadioButtonId
            if (id != -1) { //If one of the radio buttons are selected
                /*val selectedRadio: RadioButton = findViewById(id)
                Toast.makeText(
                    applicationContext, "Exercise plan created:" + "${selectedRadio.text}",
                    Toast.LENGTH_SHORT).show()*/
                createExercise()
            } else {
                Toast.makeText(
                // If none of the radio buttons are selected
                    applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }

        //Makes the cancel button goes back to the main activity
        binding.cancelButton.setOnClickListener {finish()}
    }

    private fun exerciseDataSender() {
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


/*        val index = 1
        val newItem = ExerciseModel(binding.exerciseName.text.toString())
        val mainActivity = MainActivity()
        mainActivity.exerciseList.add(index, newItem)
        mainActivity.adapter.notifyItemInserted(index)*/
    }
}