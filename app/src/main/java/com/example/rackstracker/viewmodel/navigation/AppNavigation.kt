package com.example.rackstracker.viewmodel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.rackstracker.session.SessionManager
import com.example.rackstracker.ui.screens.AddRackScreen
import com.example.rackstracker.ui.screens.LoginScreen
import com.example.rackstracker.ui.screens.ProfileScreen
import com.example.rackstracker.ui.screens.RackDetailScreen
import com.example.rackstracker.ui.screens.RackHistoryScreen
import com.example.rackstracker.ui.screens.RacksScreen
import com.example.rackstracker.ui.screens.RecoveryScreen
import com.example.rackstracker.ui.screens.RegisterScreen
import com.example.rackstracker.viewmodel.AddRackViewModel
import com.example.rackstracker.viewmodel.LoginViewModel
import com.example.rackstracker.viewmodel.ProfileViewModel
import com.example.rackstracker.viewmodel.RackDetailViewModel
import com.example.rackstracker.viewmodel.RackHistoryViewModel
import com.example.rackstracker.viewmodel.RacksViewModel
import com.example.rackstracker.viewmodel.RecoveryViewModel
import com.example.rackstracker.viewmodel.RegisterViewModel

object Routes {
    const val LOGIN = "login"
    const val RECUPERAR = "recuperar"
    const val CREAR_CUENTA = "crear_cuenta"
    const val RACKS = "racks"
    const val PERFIL = "perfil"
    const val AGREGAR_RACK = "agregar_rack"
    const val DETALLE_RACK = "detalle_rack/{rackId}"
    fun detalleRack(id: Int) = "detalle_rack/$id"
    const val HISTORIAL_RACK = "historial_rack/{rackId}"
    fun historialRack(id: Int) = "historial_rack/$id"
}

@Composable
fun Navegacion(
    loginViewModel: LoginViewModel,
    recoveryViewModel: RecoveryViewModel,
    registerViewModel: RegisterViewModel,
    racksViewModel: RacksViewModel,
    profileViewModel: ProfileViewModel,
    addRackViewModel: AddRackViewModel,
    rackDetailViewModel: RackDetailViewModel,
    rackHistoryViewModel: RackHistoryViewModel,
    navController: NavHostController = rememberNavController()
) {
    val usuario by SessionManager.usuario.collectAsStateWithLifecycle()
    var habiaSesion by remember { mutableStateOf(usuario != null) }

    LaunchedEffect(usuario) {
        val actual = navController.currentDestination?.route
        val rutasSinSesion = setOf(Routes.LOGIN, Routes.CREAR_CUENTA, Routes.RECUPERAR)
        if (usuario == null) {
            if (habiaSesion) {
                loginViewModel.reiniciarEstado()
            }
            if (actual != null && actual !in rutasSinSesion) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else if (actual == Routes.LOGIN && !loginViewModel.uiState.value.loginExitoso) {
            navController.navigate(Routes.RACKS) {
                popUpTo(Routes.LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        }
        habiaSesion = usuario != null
    }

    val inicio = if (usuario != null) Routes.RACKS else Routes.LOGIN
    NavHost(navController = navController, startDestination = inicio) {

        composable(Routes.LOGIN) { LoginScreen(navController, loginViewModel) }

        composable(Routes.RECUPERAR) { RecoveryScreen(navController, recoveryViewModel) }

        composable(Routes.CREAR_CUENTA) { RegisterScreen(navController, registerViewModel) }

        composable(Routes.RACKS) { RacksScreen(navController, racksViewModel) }

        composable(Routes.PERFIL) { ProfileScreen(navController, profileViewModel) }

        composable(Routes.AGREGAR_RACK) { AddRackScreen(navController, addRackViewModel) }

        composable(
            route = Routes.DETALLE_RACK,
            arguments = listOf(navArgument("rackId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rackId = backStackEntry.arguments?.getInt("rackId") ?: 0
            RackDetailScreen(navController, rackId, rackDetailViewModel)
        }

        composable(
            route = Routes.HISTORIAL_RACK,
            arguments = listOf(navArgument("rackId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rackId = backStackEntry.arguments?.getInt("rackId") ?: 0
            RackHistoryScreen(navController, rackId, rackHistoryViewModel)
        }
    }
}
