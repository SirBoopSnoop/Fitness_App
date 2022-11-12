package com.example.testapplication.activity
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.adapter.ExerciseAdapter
import com.example.testapplication.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import com.example.testapplication.model.Exercise
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ExerciseAdapter.OnItemClickListener {

    //Variables
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var database : DatabaseReference
    private lateinit var exerciseList : ArrayList<Exercise>
    //Using the temp ArrayList for filtering the exerciseList
    private lateinit var tempArrayList : ArrayList<Exercise>
    private lateinit var recyclerView : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.exercise_RecyclerView)
        // Creates a vertical Layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        exerciseList = arrayListOf<Exercise>()
        tempArrayList = arrayListOf<Exercise>()
        getData()


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
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.searchView) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        //Finding the search.xml from SearchView as Id
        val item = menu!!.findItem(R.id.searchView)
        val searchView = item!!.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }
            @SuppressLint("NotifyDataSetChanged")
            //Whenever an user type something the function will listen to each letter
            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                //Makes the text letter user types into lower case in order to remove case sensitive
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    exerciseList.forEach {
                        if (it.exerciseName!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempArrayList.add(it)
                        }
                    }
                    exercise_RecyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempArrayList.clear()
                    tempArrayList.addAll(exerciseList)
                    exercise_RecyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        database = FirebaseDatabase.getInstance("https://fitnessapp-11fe0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("TestData")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                exerciseList.clear()
                for (exerciseSnapshot in snapshot.children) {
                    val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                    exerciseList.add(exercise!!)
                }
                // Access the RecyclerView Adapter and load the data into it
                tempArrayList.addAll(exerciseList)
                recyclerView.adapter = ExerciseAdapter(tempArrayList, this@MainActivity)
            }
        }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Failed to load the exercise.",
                    Toast.LENGTH_SHORT).show()
            }
        })

        //Add all data into tempArrayList for search query
        tempArrayList.addAll(exerciseList)
    }


    override fun onItemClick(position: Int) {
        val clickedItem : Exercise = exerciseList[position]

        //Sends the data intent to ViewExerciseActivity
            Intent(this, ViewExerciseActivity::class.java).also{
                it.putExtra("viewKey", clickedItem.exerciseId)
                startActivity(it)
            }

        recyclerView.adapter?.notifyItemChanged(position)
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
//            R.id.nav_cardio -> Intent(this, CardioView::class.java).also { startActivity(it) }
            R.id.nav_cardio -> Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        return true
    }
}
