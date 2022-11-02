package com.example.testapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_create.view.*
import kotlinx.android.synthetic.main.recyclerview_design.view.*




class ExerciseAdapter(private val exerciseList : ArrayList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        //This is where you inflate the layout. Giving a look to our rows
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_design, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        //Assigning values to the views were created in the recycler_design layout file
        //Based on the position of the recycler view
        val currentItem = exerciseList[position]
/*        holder.exerciseImage.setImageResource(currentItem.category)*/

        holder.textView1.text = currentItem.exerciseName
    }

    override fun getItemCount(): Int {
        //The recycler view just wants to know the number of items you want displayed
        return exerciseList.size
    }

    class ExerciseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        //Grabbing the views from our recycler_design layout file

        val textView1 : TextView = itemView.exercise_textView
/*        val exerciseImage : ImageView = itemView.findViewById(R.id.exercise_image)*/

    }
}
