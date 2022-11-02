package com.example.testapplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Variables
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var database : DatabaseReference
    lateinit var exerciseList : ArrayList<Exercise>
    lateinit var recyclerView : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*test_button.setOnClickListener {

            val database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/")
            val testData = database.getReference("TestData")
            testData.child("test").setValue("New test").addOnSuccessListener {
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show() }

        }*/
        recyclerView = findViewById(R.id.exercise_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        exerciseList = arrayListOf<Exercise>()
        getData()



        btnPlay.setOnClickListener {
            Intent(this, ViewExerciseActivity::class.java).also{
                val value = "New exercise"
                it.putExtra("key", value)
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

        // Creates a vertical Layout Manager
/*        exercise_RecyclerView.layoutManager = LinearLayoutManager(this)

        // Access the RecyclerView Adapter and load the data into it
        exercise_RecyclerView.adapter = adapter
        exercise_RecyclerView.setHasFixedSize(true)*/
    }

    private fun getData() {
        database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                for (exerciseSnapshot in snapshot.children) {
                    val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                    exerciseList.add(exercise!!)
                }
                recyclerView.adapter = ExerciseAdapter(exerciseList)
            }
        }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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

        when(item.itemId){
            //R.id.nav_cardio -> Intent(this, CardioView::class.java).also { startActivity(it) }
        }
        return true
    }

}
