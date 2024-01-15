//package com.example.sheeba.util
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.rememberNavController
//import com.example.sheeba.screens.entryScreens.EntryScreen
//
//// Navigationを管理
//@Composable
//fun DisplayNav() {
//
//    val navController = rememberNavController()
//
//    NavHost(navController = navController,
//        startDestination = "First Screen") {
//
//        composable(route = "First Screen") {
//            EntryScreen()
//        }
//
//        composable(route = "Second Screen") {
//            SetUpUsernameScreen()
//        }
//    }
//}