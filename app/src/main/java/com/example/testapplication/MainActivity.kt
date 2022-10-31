package com.example.testapplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Variables
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    //Variables for RecyclerView
    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var exerciseArrayList: ArrayList<ExerciseModel>
    private lateinit var exerciseAdapter: ExerciseAdapter
    lateinit var exerciseName : Array<ExerciseModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val message = intent.getStringExtra("RADIO_MESSAGE")
//
//        val textView = findViewById<TextView>(R.id.textView2).apply {
//            text = message
//        }

        /*test_button.setOnClickListener {

            val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
            val testData = database.getReference("TestData")
            testData.child("test").setValue("New test").addOnSuccessListener {
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }

        }*/

        btnPlay.setOnClickListener {
            Intent(this, ViewExerciseActivity::class.java).also{
                startActivity(it)
            }
        }

        create_button.setOnClickListener {
            Intent(this, CreateActivity::class.java).also{
                startActivity(it)
            }
        }

        /*--------------------------Hooks--------------------------------*/
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        navigationView = findViewById<NavigationView>(R.id.nav_view)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        /*--------------------------Tool Bar-----------------------------*/

        setSupportActionBar(toolbar)

        /*-------------------------Navigation Drawer Menu--------------------------*/
        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)


/*        exerciseName = arrayOf(
            "exercise1",
            "exercise2",
            "exercise3"
        )*/

        exerciseRecyclerView = findViewById(R.id.exercise_RecyclerView)

        // Creates a vertical Layout Manager
        exercise_RecyclerView.layoutManager = LinearLayoutManager(this)
        exerciseRecyclerView.setHasFixedSize(true)


/*        val adapter = ExerciseAdapter(exerciseArrayList)
        exerciseRecyclerView.adapter = adapter*/
        exerciseAdapter = ExerciseAdapter(exerciseArrayList)

        // Access the RecyclerView Adapter and load the data into it
/*        exercise_RecyclerView.adapter = ExerciseAdapter(exerciseArrayList)*/

        exerciseArrayList = ArrayList()
        exerciseRecyclerView.adapter = exerciseAdapter
        getExerciseData()
    }

    private fun getExerciseData() {
/*        for(i in exerciseName.indices) {
            val exercise = ExerciseModel(exerciseName[i])
            exerciseArrayList.add(exercise)
        }*/

        exerciseArrayList.add(ExerciseModel("Exercise1"))
        exerciseArrayList.add(ExerciseModel("Exercise2"))
    }

    override fun onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

        super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
