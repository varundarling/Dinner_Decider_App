package com.varun.decidethefood

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowMetrics
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
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.varun.decidethefood.R.*
import com.varun.decidethefood.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val foodItems = arrayListOf<String>()
    private lateinit var foodTextView: TextView
    private var adView: AdView? = null
    private lateinit var binding: ActivityMainBinding

    // Get the ad size with screen width.
    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

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

    private fun loadBanner() {
        // [START create_ad_view]
        // Create a new ad view.
        val adView = AdView(this)
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        adView.setAdSize(adSize)
        this.adView = adView

        // Replace ad container with new ad view.
        binding.bannerAdView.removeAllViews()
        binding.bannerAdView.addView(adView)
        // [END create_ad_view]

        // [START load_ad]
        // Start loading the ad in the background.
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // [END load_ad]
    }

}