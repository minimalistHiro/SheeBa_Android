package com.example.sheeba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sheeba.screens.entryScreens.EntryScreen
import com.example.sheeba.screens.signUpScreens.SetUpUsernameScreen
import com.example.sheeba.util.Setting

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            PostOfficeApp()
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Setting.entryScreen) {
                composable(route = Setting.entryScreen) {
                    EntryScreen(onButtonClicked = { navController.navigate(Setting.setUpUsernameScreen) })
                }
                composable(route = "setUpUsernameScreen") {
                    SetUpUsernameScreen(onButtonClicked = { navController.navigateUp() })
                }
            }
        }
    }
}
