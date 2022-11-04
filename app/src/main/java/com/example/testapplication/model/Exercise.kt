package com.example.testapplication.model

data class Exercise(val exerciseName : String? = null,
                    val videoUrl : String? = null,
                    val reps : Int? = null,
                    val sets : Int? = null,
                    val duration : Double? = null,
                    val intensity : Double? = null,
                    val breakTime : Int? = null,
                    val category : String? = null,
                    val description : String? = null)


