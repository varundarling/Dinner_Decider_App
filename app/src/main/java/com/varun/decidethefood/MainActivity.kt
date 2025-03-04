package com.varun.decidethefood

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.varun.decidethefood.R.*


class MainActivity : AppCompatActivity() {
    private val foodItems = arrayListOf<String>()
    private lateinit var foodTextView: TextView

    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)

        MobileAds.initialize(this@MainActivity)
        mAdView = findViewById(id.bannerAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val decideBtn: Button = findViewById(id.decideBtn)
        val selectedFoodTxt: TextView = findViewById(id.selectedFoodTxt)
        val addFoodTxt: EditText = findViewById(id.addFoodTxt)
        val addFoodBtn: Button = findViewById(id.addFoodBtn)
        foodTextView = findViewById(id.foodTextView)
        val progressBar: ProgressBar = findViewById(id.progressBar)

        decideBtn.setOnClickListener{
            if(foodItems.isNotEmpty()){
            selectedFoodTxt.text = getString(string.selecting_food) // Show loading text
            progressBar.visibility = View.VISIBLE // Show loading animation

            val blinkAnimator = ObjectAnimator.ofFloat(selectedFoodTxt, "alpha", 0f, 1f).apply {
                duration = 500 // 0.5 seconds per blink
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE // Keep blinking until food is selected
                start()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val randomFood = foodItems.random() // Pick a random item
                blinkAnimator.cancel() // Stop blinking
                selectedFoodTxt.text = randomFood // Show result
                selectedFoodTxt.alpha = 0f
                selectedFoodTxt.animate().alpha(1f).setDuration(500).start()
                progressBar.visibility = View.GONE // Hide loading animation

                // Start Zoom In & Out Animation
                val zoomInOut = ScaleAnimation(
                    1f, 1.5f, // Scale from normal to 1.5x size
                    1f, 1.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot at center
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                zoomInOut.duration = 500 // Animation duration
                zoomInOut.repeatMode = Animation.REVERSE
                zoomInOut.repeatCount = 1 // Zoom in and then zoom out

                selectedFoodTxt.startAnimation(zoomInOut) // Start animation
            }, 2000)
        }else {
                Snackbar.make(selectedFoodTxt, "Please Add Food First", Snackbar.LENGTH_SHORT).show()
            }
        }

        addFoodBtn.setOnClickListener {
            val newFood = addFoodTxt.text.toString()
            if(newFood.isEmpty()){
                Snackbar.make(addFoodTxt, "Please Enter the Food.....", Snackbar.LENGTH_SHORT).show()
            }else {
                Snackbar.make(addFoodTxt, "Food Added Successfully", Snackbar.LENGTH_SHORT).show()
                foodItems.add(newFood)
                updateTextView()
            }
            addFoodTxt.text.clear()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun updateTextView() {
        foodTextView.text =  foodItems.joinToString(", ")
    }
}