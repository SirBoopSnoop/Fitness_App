package com.example.testapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.databinding.ActivityCreateBinding
import kotlinx.android.synthetic.main.activity_create.view.*

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

        binding.cancelButton.setOnClickListener {finish()}
    }
}