package com.example.clicker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SharedMemory
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // save data
    private lateinit var shared: SharedPreferences
    private lateinit var button: ImageButton
    private lateinit var scoreText: TextView
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shared = getSharedPreferences("ClickerDB", Context.MODE_PRIVATE)

        // maybe i should put it in OnResume or OnContinue
        readData()

        scoreText = findViewById(R.id.scoreText)
        scoreText.text =  getString(R.string.player_score, score.toString())

        button = findViewById(R.id.imageButton)

        button.setOnClickListener{ buttonOnClickAddScore() }
    }

    private fun buttonOnClickAddScore(){
        score++;
        scoreText.text =  getString(R.string.player_score, score.toString())

        // maybe i should put it in other place
        saveData()
    }

    private fun readData(){
        score = shared.getInt("score",score);
    }

    private fun saveData(){
        val editData = shared.edit();
        editData.putInt("score",score);
        editData.apply();
    }

}