package com.htf.system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.htf.system.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.helloWorld.text = getString(R.string.hello_world)
    }
    
    fun onButtonClick(view: android.view.View) {
        // TODO: Implementar acción del botón
    }
}
