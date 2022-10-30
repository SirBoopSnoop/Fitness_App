package com.example.testapplication

data class ExerciseModel(val exerciseName : String,
                         val videoUrl : String? = null,
                         val reps : Int,
                         val sets : Int,
                         val intensity : Double,
                         val breakTime : Int,
                         val category : String,
                         val description : String? = null)


