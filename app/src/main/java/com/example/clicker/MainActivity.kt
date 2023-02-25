package com.example.clicker

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // save data
    private lateinit var shared: SharedPreferences
    private lateinit var button: ImageButton
    private lateinit var scoreText: TextView

    // spawned cookies
    private lateinit var newView: ImageView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var rng: Random


    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.mainLayout)

        shared = getSharedPreferences("ClickerDB", Context.MODE_PRIVATE)
        // maybe i should put it in OnResume or OnContinue
        readData()

        scoreText = findViewById(R.id.scoreText)
        scoreText.text = getString(R.string.player_score, score.toString())

        rng = Random(System.currentTimeMillis())

        button = findViewById(R.id.imageButton)

        button.setOnClickListener { buttonOnClickAddScore() }
    }

    private fun buttonOnClickAddScore() {
        increaseScoreOnClick()
        spawnAndMoveCookie()
        // maybe i should put it in other place
        saveData()
    }

    private fun increaseScoreOnClick() {
        score++
        scoreText.text = getString(R.string.player_score, score.toString())
    }

    private fun spawnAndMoveCookie() {
        newView = ImageView(this)

        mainLayout.addView(newView, 0)

        newView.layoutParams.height = rng.nextInt(150, 250)
        newView.layoutParams.width = rng.nextInt(150, 250)
        newView.x = rng.nextFloat() * 1000
        newView.y = -300F
        newView.alpha = 0.5F
        newView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.cookie))
        // move cookie
        ObjectAnimator.ofFloat(newView, "translationY", 2080f).apply {
            duration = rng.nextLong(1500, 3000)
            start()
        }
    }

    private fun readData() {
        score = shared.getInt("score", score)
    }

    private fun saveData() {
        val editData = shared.edit()
        editData.putInt("score", score)
        editData.apply()
    }

}