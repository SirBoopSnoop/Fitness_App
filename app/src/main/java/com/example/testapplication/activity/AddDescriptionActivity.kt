package com.example.testapplication.activity

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityAddDescriptionBinding
import com.google.firebase.database.FirebaseDatabase

class AddDescriptionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddDescriptionBinding

    @RequiresApi(Build.VERSION_CODES.M)
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
                    val description = it.child("description").value

                    if (description != null){
                        binding.editDescription.setText(description.toString())
                    }else{
                        binding.editDescription.setText("")
                    }
                }
            }
        }

        binding.saveDescButton.setOnClickListener {
            //Check if the user is connected to the database or not
            if(!isNetworkAvailable()) {
                Toast.makeText(
                    baseContext, "Please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                updateExercise(path)
                finish()
            }
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable() : Boolean {
        val connection = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connection.getNetworkCapabilities(connection.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }
}