package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import kotlinx.android.synthetic.main.recyclerview_design.view.*
import com.example.testapplication.model.Exercise


class ExerciseAdapter(private val exerciseList: ArrayList<Exercise>,
                      private val listener: OnItemClickListener
) :
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
        holder.textView2.text = currentItem.category

    }

    override fun getItemCount(): Int {
        //The recycler view just wants to know the number of items you want displayed
        return exerciseList.size
    }

    inner class ExerciseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        //Grabbing the views from our recycler_design layout file
        val textView1: TextView = itemView.exercise_textView
        val textView2: TextView = itemView.exercise_textView2
        val exerciseImage: ImageView = itemView.exercise_image

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
