package com.devhjs.customgalary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devhjs.customgalary.presentation.MyCustomGallery
import com.devhjs.customgalary.ui.theme.CustomGalaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomGalaryTheme {
                MyCustomGallery()
            }
        }
    }
}