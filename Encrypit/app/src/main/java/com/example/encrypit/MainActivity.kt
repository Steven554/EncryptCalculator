package com.example.encrypit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val context = applicationContext

        var calcPasscode = "45"
        val key = "thisKey"
        val calc = context.getSharedPreferences("holder", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = calc.edit()
        editor.putString(key, calcPasscode)
        editor.apply()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Numbers
        One.setOnClickListener { appendOnExpression("1", true) }
        Two.setOnClickListener { appendOnExpression("2", true) }
        Three.setOnClickListener { appendOnExpression("3", true) }
        Four.setOnClickListener { appendOnExpression("4", true) }
        Five.setOnClickListener { appendOnExpression("5", true) }
        Six.setOnClickListener { appendOnExpression("6", true) }
        Seven.setOnClickListener { appendOnExpression("7", true) }
        Eight.setOnClickListener { appendOnExpression("8", true) }
        Nine.setOnClickListener { appendOnExpression("9", true) }
        Zero.setOnClickListener { appendOnExpression("0", true) }
        Dot.setOnClickListener { appendOnExpression(".", true) }

        // Operators
        Plus.setOnClickListener { appendOnExpression("+", false) }
        Minus.setOnClickListener { appendOnExpression("-", false) }
        Divide.setOnClickListener { appendOnExpression("/", false) }
        Multiply.setOnClickListener { appendOnExpression("*", false) }
        Open.setOnClickListener { appendOnExpression("(", false) }
        Close.setOnClickListener { appendOnExpression(")", false) }

        Clear.setOnClickListener {
            topDisplay.text = ""
            bottomDisplay.text = ""
        }

        Back.setOnClickListener {
            val string = topDisplay.text.toString()
            if (string.isNotEmpty()) {
                topDisplay.text = string.substring(0, string.length - 1)
            }
            bottomDisplay.text = ""
        }

        Equals.setOnClickListener {
            val expressionString = topDisplay.text.toString()
            if (expressionString == calcPasscode) {
                loadNewActivity()
            }
                val expression = ExpressionBuilder(topDisplay.text.toString()).build()
                val result = expression.evaluate()
                val longResult = result.toLong()
                if (result == longResult.toDouble())
                    bottomDisplay.text = longResult.toString()
                else
                    bottomDisplay.text = result.toString()
            }
    }

    private fun loadNewActivity(){
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
        finish()
    }

    private fun appendOnExpression(string:String, canClear:Boolean){
        if(canClear){
            bottomDisplay.text = ""
            topDisplay.append(string)
        }else{
            topDisplay.append(bottomDisplay.text)
            topDisplay.append(string)
            bottomDisplay.text = ""
        }
    }
}

