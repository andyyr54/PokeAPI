package com.example.pokeapi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object PokeInfo{
        lateinit var jsonResult:JSONObject

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }



    fun pokeInfo(view: View) {
        val jmpToInfo= Intent(this,InfoScreen::class.java)
        val pokeName:EditText = findViewById(R.id.editTextTextPersonName)
        jmpToInfo.putExtra("Pokemon", pokeName.text.toString().lowercase())
        this@MainActivity.startActivity(jmpToInfo)
    }

}