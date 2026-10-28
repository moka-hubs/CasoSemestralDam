package com.example.rackstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.rackstracker.local.AppDatabase
import com.example.rackstracker.local.DatabaseSeeder
import com.example.rackstracker.repository.RoomAuthRepository
import com.example.rackstracker.repository.RoomRackRepository
import com.example.rackstracker.session.SessionManager
import com.example.rackstracker.navigation.Navegacion
import com.example.rackstracker.ui.theme.EjemploTheme
import com.example.rackstracker.viewmodel.AddRackViewModel
import com.example.rackstracker.viewmodel.LoginViewModel
import com.example.rackstracker.viewmodel.ProfileViewModel
import com.example.rackstracker.viewmodel.RackDetailViewModel
import com.example.rackstracker.viewmodel.RacksViewModel
import com.example.rackstracker.viewmodel.RecoveryViewModel
import com.example.rackstracker.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "racks_tracker_db"
        ).build()
    }

    private val authRepository by lazy { RoomAuthRepository(db.appDao()) }
    private val rackRepository by lazy { RoomRackRepository(db.appDao()) }

    private val loginViewModel by lazy { LoginViewModel(authRepository) }
    private val recoveryViewModel by lazy { RecoveryViewModel(authRepository) }
    private val registerViewModel by lazy { RegisterViewModel(authRepository) }
    private val profileViewModel by lazy { ProfileViewModel(authRepository) }
    private val racksViewModel by lazy { RacksViewModel(rackRepository) }
    private val addRackViewModel by lazy { AddRackViewModel(rackRepository) }
    private val rackDetailViewModel by lazy { RackDetailViewModel(rackRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.attach(db.appDao(), authRepository)
        lifecycleScope.launch {
            DatabaseSeeder.sembrarSiEsNecesario(db)
            SessionManager.restaurar()
        }
        enableEdgeToEdge()
        setContent {
            EjemploTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navegacion(
                        loginViewModel = loginViewModel,
                        recoveryViewModel = recoveryViewModel,
                        registerViewModel = registerViewModel,
                        racksViewModel = racksViewModel,
                        profileViewModel = profileViewModel,
                        addRackViewModel = addRackViewModel,
                        rackDetailViewModel = rackDetailViewModel
                    )
                }
            }
        }
    }
}
