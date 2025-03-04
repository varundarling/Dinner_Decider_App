package com.varun.decidethefood

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.varun.decidethefood.R.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    val foodItems = arrayListOf<String>()
    lateinit var foodTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)

        val decideBtn: Button = findViewById(id.decideBtn)
        val selectedFoodTxt: TextView = findViewById(id.selectedFoodTxt)
        val addFoodTxt: EditText = findViewById(id.addFoodTxt)
        val addFoodBtn: Button = findViewById(id.addFoodBtn)
        foodTextView = findViewById(R.id.foodTextView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        decideBtn.setOnClickListener{
//            val random = Random
//            val randomFood =  random.nextInt(foodItems.count())
//            selectedFoodTxt.text = foodItems[randomFood]
            selectedFoodTxt.text = "Selecting food..." // Show loading text
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
            }, 2000)
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