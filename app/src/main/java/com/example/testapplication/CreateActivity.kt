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

        // Get radio group selected status and text using button click event
        binding.saveButton.setOnClickListener {
            // Get the checked radio button id from radio group
            val id: Int = category_group.checkedRadioButtonId
            if (id != -1) { //If none of the radio button is selected
                exerciseDataSender()
                val selectedRadio: RadioButton = findViewById(id)
                Toast.makeText(
                    applicationContext, "Exercise plan created:" + "${selectedRadio.text}",
                    Toast.LENGTH_SHORT).show()
                    finish()
            } else {
                // If none of the radio button is selected
                Toast.makeText(
                    applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }
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
    }
}