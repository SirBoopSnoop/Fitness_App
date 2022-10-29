package com.example.testapplication

import android.R
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.databinding.ActivityCreateBinding
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.view.*
import kotlinx.android.synthetic.main.header.*


class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding //defining the binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater) //initializing the binding class
        setContentView(binding.root) //contentView as binding.root

/*        var repValue = binding.repsValue.reps_value
        var setValue = binding.setsValue.sets_value
        var intensityValue = binding.intensityValue.intensity_value
        var breakTimeValue = binding.breakTimeValue.break_time_value*/

/*        @Override
        fun onClick(view: View) {
            val radioId: Int = binding.categoryGroup.checkedRadioButtonId

            val radioButton = findViewById<RadioButton>(radioId)

            textView2.setText("Your choice: " + radioButton.text)
        }*/

        category_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                Toast.makeText(applicationContext," On checked change :"+
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
            })




        binding.cancelButton.setOnClickListener {finish()}
    }

/*    fun ActivatedButton(view: View) {
        val radioId: Int = binding.categoryGroup.checkedRadioButtonId

        val radioButton = findViewById<RadioButton>(radioId)

        Toast.makeText(this@CreateActivity, "Selected exercise category: " + radioButton.text, Toast.LENGTH_SHORT).show()
    }*/
}