package com.example.testapplication.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityEditBinding
import com.example.testapplication.fragment.YouTubeFragment
import com.example.testapplication.model.Exercise
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_viewexercise.*

class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityEditBinding //defining the binding class
    private lateinit var exercisePath : String
    var reps : Int? = 0
    var message : String = ""
    var nameList = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.M)
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
            exercisePath = value.toString()
            fillContents(exercisePath)
        }

        getExerciseNames()

        binding.saveButton.setOnClickListener {
            val category = categoryDropdown.selectedItem

            //Check if the user is connected to the database or not
            if(!isNetworkAvailable()) {
                Toast.makeText(
                    baseContext, "Please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Get the checked spinner id from radio group
            if (category == null) { // If none of the options are selected
                Toast.makeText(applicationContext, "Please select a category", Toast.LENGTH_SHORT).show()
            }
            //Make sure input values are reasonable
            else if (TextUtils.isEmpty(binding.exerciseName.text)){
                binding.exerciseName.error = "This field is required"
            }else if (checkIfExerciseNameExists() && binding.exerciseName.text.toString() != exercisePath){
                binding.exerciseName.error = "An exercise with that name already exists"
            }else if (TextUtils.isEmpty(binding.setsValue.text)){
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
                    updateExercise()
                }
            }
            else {
                updateExercise()
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

        bottom_navigation.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.backpress -> {
                    Intent(this, ViewExerciseActivity::class.java).also {
                        startActivity(it)
                    }
                }

                R.id.home -> {Intent(this, MainActivity::class.java).also {
                    finish()
                    startActivity(it)
                    }
                }

                R.id.video -> {
                    Intent(this, YouTubeFragment::class.java).also {
                        it.putExtra("videoKey", exercisePath)
                        startActivity(it)
                    }
                }

                R.id.timer -> {
                    Intent(this, TimerActivity::class.java).also {
                        it.putExtra("timerKey", exercisePath)
                        startActivity(it)
                    }
                }
            }
            true
        }

    }

    private fun updateExercise(){
        val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")
        database.child(exercisePath).get().addOnSuccessListener {
        val youtubeLink = binding.youtubeLink.text.toString()
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
        val description = it.child("description").value.toString()


        val exercise = Exercise(exerciseName, youtubeLink, reps, sets, intensity, breakTime, category, description)
        if(exerciseName != exercisePath){
        database.child(exercisePath).removeValue()
        }
        database.child(exerciseName).setValue(exercise).addOnSuccessListener {
            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
            Intent(this, ViewExerciseActivity::class.java).also {
                it.putExtra("viewKey", exerciseName)
                startActivity(it)
            }
            finish()
        }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }
        }
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
                val video = it.child("videoUrl").value.toString()

                binding.youtubeLink.setText(video, TextView.BufferType.EDITABLE)
                binding.exerciseName.setText(exerciseName, TextView.BufferType.EDITABLE)
                binding.breakTimeValue.setText(breakTime, TextView.BufferType.EDITABLE)
                binding.repsValue.setText(reps, TextView.BufferType.EDITABLE)
                binding.setsValue.setText(sets, TextView.BufferType.EDITABLE)
                binding.intensityValue.setText(intensity, TextView.BufferType.EDITABLE)

                val index : Int

                when(category){
                    "Arms" -> index=0
                    "Chest" -> index=1
                    "Abs" -> index=2
                    "Legs" -> index=3
                    "Back" -> index=4
                    "Cardio" -> index=5
                    else -> { index=0 }
                }

                binding.categoryDropdown.setSelection(index)

            }
        }
    }

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

    private fun checkIfExerciseNameExists() : Boolean {
        val exerciseName = binding.exerciseName.text.toString()
        var check = false
        for (name in nameList) {
            if (name == exerciseName) {
                check = true
                break
            }
        }
        return check
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
                Toast.makeText(this@EditActivity, "Database Error", Toast.LENGTH_LONG).show()
            }
        })
    }

}