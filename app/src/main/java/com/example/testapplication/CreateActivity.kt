package com.example.testapplication

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.databinding.ActivityCreateBinding
import kotlinx.android.synthetic.main.activity_create.*


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
                Toast.makeText(applicationContext," On checked change :"+
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
            })

        val exerciseName = binding.exerciseName
        val reps = binding.repsValue
        val sets = binding.setsValue
        val intensity = binding.intensityValue
        val breakTime = binding.breakTimeValue
/*        val radioId: Int = binding.categoryGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(radioId)*//*
        binding.saveButton.setOnClickListener {
            Toast.makeText(
                applicationContext, "Following data: " + " ${exerciseName.text}\n" +
                        "${reps.text}\n" +
                        "${sets.text}\n" +
                        "${intensity.text}\n" +
                        "${breakTime.text}\n" *//*+
                        "${radioButton.text}\n"*//*, Toast.LENGTH_LONG
            ).show()
        }*/

        // Get radio group selected status and text using button click event
        binding.saveButton.setOnClickListener {
            // Get the checked radio button id from radio group
            val id: Int = category_group.checkedRadioButtonId
            if (id != -1) { //If none of the radio button is selected
                val selectedRadio: RadioButton = findViewById(id)
                Toast.makeText(
                    applicationContext, "Exercise plan created:" + "${selectedRadio.text}",
                    Toast.LENGTH_SHORT).show()
            } else {
                // If none of the radio button is selected
                Toast.makeText(
                    applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {finish()}
    }
}