package com.example.clicker

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val DELAY_TO_START_WORKERS = 10000L
    private val TIMER_DELAY: Long = 10000L

    // save data
    private lateinit var shared: SharedPreferences
    private lateinit var button: ImageButton
    private lateinit var buttonPowerUp: ImageButton
    private lateinit var buttonBuyWorker: ImageButton
    private lateinit var scoreText: TextView
    private lateinit var powerOfClickText: TextView
    private lateinit var amountOfWorkersText: TextView

    // spawned cookies
    private lateinit var newView: ImageView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var rng: Random


    private var score: Int = 0
    private var powerOfClick: Int = 1
    private var amountOfFarmers = 0
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        initializeComponents()
        initializeListeners()

        createFarmers()
    }

    private fun initializeComponents(){
        mainLayout = findViewById(R.id.mainLayout)

        shared = getSharedPreferences("ClickerDB", Context.MODE_PRIVATE)
        // maybe i should put it in OnResume or OnContinue
        readData()

        scoreText = findViewById(R.id.scoreText)
        scoreText.text = getString(R.string.player_score, score.toString())
        powerOfClickText = findViewById(R.id.powerOfClickTV)
        powerOfClickText.text = getString(R.string.current_power_of_click, powerOfClick.toString())
        amountOfWorkersText = findViewById(R.id.amountOfWorkersTV)
        amountOfWorkersText.text =
            getString(R.string.current_amount_of_workers, amountOfFarmers.toString())

        rng = Random(System.currentTimeMillis())
        timer = Timer()

        button = findViewById(R.id.mainClickButton)
        buttonPowerUp = findViewById(R.id.upgradePowerButton)
        buttonBuyWorker = findViewById(R.id.workerButton)
    }

    private fun initializeListeners(){
        button.setOnClickListener { buttonOnClickAddScore() }
        buttonPowerUp.setOnClickListener { buttonOnClickAddPower() }
        buttonBuyWorker.setOnClickListener { addFarmer() }
        //powerOfClickText.setOnClickListener{ score += 10000 }
    }

    override fun onStop() {
        super.onStop()
        // maybe i should put it in other place
        saveData()
    }

    private fun buttonOnClickAddScore() {
        increaseScoreOnClick()
        spawnAndMoveCookie()
    }

    // formula of power upgrade = current power * 200;
    private fun buttonOnClickAddPower() {
        val costOfUpgrade = powerOfClick * 200

        synchronized(this) {
            if (costOfUpgrade > score) {
                Toast.makeText(applicationContext ,"Cost of upgrade is $costOfUpgrade",Toast.LENGTH_SHORT).show()
                return
            }
            score -= costOfUpgrade
        }
        powerOfClick += 1

        scoreText.text = getString(R.string.player_score, score.toString())
        powerOfClickText.text = getString(R.string.current_power_of_click, powerOfClick.toString())
    }

    private fun increaseScoreOnClick() {
        synchronized(this) {
            score += powerOfClick
        }
        scoreText.text = getString(R.string.player_score, score.toString())
    }

    private fun spawnAndMoveCookie() {
        // spawn cookie
        newView = ImageView(this)

        mainLayout.addView(newView, 0)

        // modify cookie
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
        powerOfClick = shared.getInt("powerOfClick", powerOfClick)
        amountOfFarmers = shared.getInt("amountOfFarmers", amountOfFarmers)
    }

    private fun saveData() {
        val editData = shared.edit()
        editData.putInt("score", score)
        editData.putInt("powerOfClick", powerOfClick)
        editData.putInt("amountOfFarmers", amountOfFarmers)
        editData.apply()
    }

    private fun addFarmer() {
        val costOfUpgrade = amountOfFarmers * 500

        synchronized(this) {
            if (costOfUpgrade > score) {
                Toast.makeText(applicationContext ,"Cost of upgrade is $costOfUpgrade",Toast.LENGTH_SHORT).show()
                return
            }
            score -= costOfUpgrade
        }
        amountOfFarmers += 1

        scoreText.text = getString(R.string.player_score, score.toString())
        amountOfWorkersText.text =
            getString(R.string.current_amount_of_workers, amountOfFarmers.toString())
    }

    private fun createFarmers() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                synchronized(this) {
                    score += powerOfClick * amountOfFarmers
                }

                // to handle error
                // Only the original thread that created a view hierarchy can touch its views.
                runOnUiThread {
                    scoreText.text = getString(R.string.player_score, score.toString())
                }

                Log.i(
                    "MainActivity",
                    "score = $score powerOfClick = $powerOfClick Farmers = $amountOfFarmers"
                )
            }
        }, DELAY_TO_START_WORKERS, TIMER_DELAY)
    }

}