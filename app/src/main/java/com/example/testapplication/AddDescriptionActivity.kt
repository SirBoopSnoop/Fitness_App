package com.example.testapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.testapplication.databinding.ActivityAddDescriptionBinding
import com.google.firebase.database.FirebaseDatabase

class AddDescriptionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_description)

        binding = ActivityAddDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras : Bundle? = intent.extras
        var value : String? = extras!!.getString("descKey")
        val path = value.toString()
        if (extras != null){

            val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
            database.child(path).get().addOnSuccessListener {

                if(it.exists()){
                    val description = it.child("description").value.toString()

                    binding.editDescription.setText(description)
                }
            }
        }

        binding.saveDescButton.setOnClickListener {
            updateExercise(path)
            finish()
        }

        binding.cancelDescButton.setOnClickListener {
            finish()
        }
    }

    private fun updateExercise(path : String){

        val description = binding.editDescription.text.toString()

        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
        val testData = database.getReference("TestData").child(path)
        testData.child("description").setValue(description).addOnSuccessListener {
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }
    }
}