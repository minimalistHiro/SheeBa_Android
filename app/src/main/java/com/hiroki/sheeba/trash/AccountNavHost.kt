//package com.hiroki.sheeba.screens.accountScreens
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.hiroki.sheeba.util.Setting
//import com.hiroki.sheeba.viewModel.ViewModel
//
//@ExperimentalMaterial3Api
//@Composable
//fun AccountNavHost(viewModel: ViewModel, padding: PaddingValues) {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController,
//        startDestination = "AccountScreen") {
//
//        composable(route = "AccountScreen") {
//            AccountScreen(viewModel = viewModel, navController = navController)
//        }
//        composable(route = Setting.updateUsernameScreen) {
//            UpdateUsernameScreen(viewModel = viewModel, navController = navController)
//        }
//    }
//}