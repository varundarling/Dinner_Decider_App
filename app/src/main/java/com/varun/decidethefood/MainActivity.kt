package com.varun.decidethefood

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.varun.decidethefood.R.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val foodItems = arrayListOf("Fast Food", "Biriyani", "Sharwama", "Pizza", "Burger", "Street Food")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)

        val decideBtn: Button = findViewById(id.decideBtn)
        val selectedFoodTxt: TextView = findViewById(id.selectedFoodTxt)
        val addFoodTxt: EditText = findViewById(id.addFoodTxt)
        val addFoodBtn: Button = findViewById(id.addFoodBtn)

        decideBtn.setOnClickListener{
            val random = Random
            val randomFood =  random.nextInt(foodItems.count())
            selectedFoodTxt.text = foodItems[randomFood]
        }

        addFoodBtn.setOnClickListener {
            val newFood = addFoodTxt.text.toString()
            foodItems.add(newFood)
            if(newFood.isEmpty()){
                Snackbar.make(addFoodTxt, "Please Enter the Food.....", Snackbar.LENGTH_LONG).show()
            }
            addFoodTxt.text.clear()
            println(foodItems)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}