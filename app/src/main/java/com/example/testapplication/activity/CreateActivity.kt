package com.example.testapplication.activity

import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.R
import com.example.testapplication.adapter.ExerciseAdapter
import com.example.testapplication.databinding.ActivityCreateBinding
import com.example.testapplication.model.Exercise
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots


class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding //defining the binding class
    var reps : Int? = 0
    var message : String = ""
    var nameList = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.M)
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

        getExerciseNames()

        // Get spinner group selected status
        binding.saveButton.setOnClickListener {
            // Get the checked spinner id from radio group
            val category = categoryDropdown.selectedItem

            //Check if the user is connected to the database or not
            if(!isNetworkAvailable()) {
                Toast.makeText(
                    baseContext, "Please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }

            /*checkName(binding.exerciseName.text.toString())*/
            //checkIfExerciseNameExists()

            if (category == null) { // If none of the options are selected
                Toast.makeText(applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }
            //Make sure input values are reasonable
            else if (TextUtils.isEmpty(binding.exerciseName.text)){
                binding.exerciseName.error = "This field is required"
            }
            else if(checkIfExerciseNameExists()) {
                binding.exerciseName.error = "Name conflict detected"
            }
            else if (TextUtils.isEmpty(binding.setsValue.text)){
                binding.setsValue.error = "This field is required"
            }else if(binding.setsValue.text.toString().toInt() < 1){
                binding.setsValue.error = "Cannot be zero"
            }else if (TextUtils.isEmpty(binding.intensityValue.text)){
                binding.intensityValue.error = "This field is required"
            }else if(binding.intensityValue.text.toString().toDouble() < 0.2){
                binding.intensityValue.error = "Cannot be less than 0.2"
            }else if (TextUtils.isEmpty(binding.breakTimeValue.text)){
                binding.breakTimeValue.error = "This field is required"
            }else if(binding.breakTimeValue.text.toString().toInt() < 1){
                binding.breakTimeValue.error = "Cannot be zero"
            }else if(binding.categoryDropdown.selectedItem != "Cardio"){
                if (TextUtils.isEmpty(binding.repsValue.text)){
                    binding.repsValue.error = "This field is required"
                }else if(binding.repsValue.text.toString().toInt() < 1){
                    binding.repsValue.error = "Cannot be zero"
                }else{
                    createExercise()
                }
            }
            else {
                createExercise()
            }
        }

        binding.intensityQuestion.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(true)
            dialog.setMessage(message)
            dialog.setNeutralButton("Dismiss"){dialogInterface , which ->
                dialogInterface.cancel()
            }
            dialog.show()
        }

        binding.breakQuestion.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(true)
            //dialog.setTitle("Break time")
            dialog.setMessage("Define how many seconds you want to wait before starting a new set")
            dialog.setNeutralButton("Dismiss"){dialogInterface , which ->
                dialogInterface.cancel()
            }
            dialog.show()
        }

        //Makes the cancel button goes back to the main activity
        binding.cancelButton.setOnClickListener {finish()}

        binding.categoryDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (categoryDropdown.selectedItem.equals("Cardio")){
                    binding.repsLayout.visibility = View.GONE
                    binding.intensityLayout.hint = "Duration"
                    message = getString(R.string.duration_description)
                }else{
                    message = getString(R.string.intensity_description)
                    binding.setsLayout.visibility = View.VISIBLE
                    binding.repsLayout.visibility = View.VISIBLE
                    binding.intensityLayout.hint = "Intensity"
                    binding.intensityQuestion.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun createExercise(){
        val exerciseName = binding.exerciseName.text.toString()
        if (binding.categoryDropdown.selectedItem != "Cardio"){
            reps = binding.repsValue.text.toString().toInt()
        }else{
            reps = null
        }
        val sets = binding.setsValue.text.toString().toInt()
        val intensity = binding.intensityValue.text.toString().toDouble()
        val breakTime = binding.breakTimeValue.text.toString().toInt()
        val category = binding.categoryDropdown.selectedItem.toString()
        val videoUrl = binding.youtubeLink.text.toString()

        val exercise = Exercise(exerciseName, videoUrl, reps, sets, intensity, breakTime, category, "")
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("TestData")

        database.child(exerciseName).setValue(exercise).addOnSuccessListener {
            /*Toast.makeText(applicationContext, "Successfully saved", Toast.LENGTH_SHORT).show()*/
            finish()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed...", Toast.LENGTH_SHORT).show() }
    }

    private fun checkIfExerciseNameExists() : Boolean {
        val exerciseName = binding.exerciseName.text.toString()
        var check = false
            for (name in nameList) {
            if (name == exerciseName) {
                check = true
            }
        }
        return check
    }

// Removes focus from input fields when you click away from them
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable() : Boolean {
        val connection = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connection.getNetworkCapabilities(connection.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }

    private fun getExerciseNames(){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("TestData")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (exerciseSnapshot in snapshot.children) {
                        val exerciseName = exerciseSnapshot.getValue(Exercise::class.java)!!.exerciseName!!
                        nameList.add(exerciseName)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateActivity, "Database Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        nameList.clear()
        getExerciseNames()
    }
}