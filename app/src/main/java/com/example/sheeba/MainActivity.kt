package com.example.sheeba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.sheeba.app.PostOfficeApp

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostOfficeApp()
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = Setting.entryScreen) {
//                composable(route = Setting.entryScreen) {
//                    EntryScreen(onButtonClicked = { navController.navigate(Setting.setUpUsernameScreen) })
//                }
//                composable(route = Setting.setUpUsernameScreen) {
//                    SetUpUsernameScreen(onButtonClicked = { navController.navigate(Setting.setUpEmailScreen) }, onBackButtonClicked = { navController.navigateUp() })
//                }
//                composable(route = Setting.setUpEmailScreen) {
//                    SetUpEmailScreen(onButtonClicked = { navController.navigateUp() }, onBackButtonClicked = { navController.navigateUp() })
//                }
//            }
        }
    }
}
