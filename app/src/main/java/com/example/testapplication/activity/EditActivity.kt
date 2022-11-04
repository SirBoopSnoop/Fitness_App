package com.example.testapplication.activity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityEditBinding
import com.google.firebase.database.FirebaseDatabase
import com.example.testapplication.model.Exercise

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding //defining the binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val extras : Bundle? = intent.extras
        if (extras != null) {
            var value: String? = extras.getString("key")
            val path = value.toString()
            fillContents(path)
        }

        binding.saveButton.setOnClickListener {
            updateExercise()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun updateExercise(){
        val exerciseName = binding.exerciseName.text.toString()
        val reps = binding.repsValue.text.toString().toInt()
        val sets = binding.setsValue.text.toString().toInt()
        val intensity = binding.intensityValue.text.toString().toDouble()
        val breakTime = binding.breakTimeValue.text.toString().toInt()
        val category = binding.categoryDropdown.selectedItem.toString()

        val exercise = Exercise(exerciseName, null, reps, sets, null, intensity, breakTime, category, null)
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
        val testData = database.getReference("TestData")
        testData.child(exerciseName).setValue(exercise).addOnSuccessListener {
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }
    }

    private fun fillContents(path:String){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(path).get().addOnSuccessListener {

            if(it.exists()){
                val exerciseName = it.child("exerciseName").value.toString()
                val breakTime = it.child("breakTime").value.toString()
                val category = it.child("category").value.toString()
                val intensity = it.child("intensity").value.toString()
                val reps = it.child("reps").value.toString()
                val sets = it.child("sets").value.toString()

                binding.exerciseName.setText(exerciseName, TextView.BufferType.EDITABLE)
                binding.breakTimeValue.setText(breakTime, TextView.BufferType.EDITABLE)
                binding.repsValue.setText(reps, TextView.BufferType.EDITABLE)
                binding.setsValue.setText(sets, TextView.BufferType.EDITABLE)
                binding.intensityValue.setText(intensity, TextView.BufferType.EDITABLE)

                val index : Int

                when(category){
                    "Cardio" -> index=0
                    "Arms" -> index=1
                    "Chest" -> index=2
                    "Abs" -> index=3
                    "Legs" -> index=4
                    "Back" -> index=5
                    else -> { index=0 }
                }

                binding.categoryDropdown.setSelection(index)

            }
        }
    }

}