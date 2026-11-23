package com.duoc.rackstracker

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
import com.duoc.rackstracker.data.remote.RetrofitInstance
import com.duoc.rackstracker.data.remote.TokenStore
import com.duoc.rackstracker.repository.InMemoryRackRepository
import com.duoc.rackstracker.repository.XanoAuthRepository
import com.duoc.rackstracker.repository.XanoRackRepository
import com.duoc.rackstracker.session.SessionManager
import com.duoc.rackstracker.viewmodel.navigation.Navegacion
import com.duoc.rackstracker.ui.theme.EjemploTheme
import com.duoc.rackstracker.viewmodel.AddRackViewModel
import com.duoc.rackstracker.viewmodel.LoginViewModel
import com.duoc.rackstracker.viewmodel.ProfileViewModel
import com.duoc.rackstracker.viewmodel.RackDetailViewModel
import com.duoc.rackstracker.viewmodel.RackHistoryViewModel
import com.duoc.rackstracker.viewmodel.RacksViewModel
import com.duoc.rackstracker.viewmodel.RecoveryViewModel
import com.duoc.rackstracker.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authRepository by lazy { XanoAuthRepository() }
    private val rackRepository by lazy {
        XanoRackRepository(local = InMemoryRackRepository())
    }

    private val loginViewModel by lazy { LoginViewModel(authRepository) }
    private val recoveryViewModel by lazy { RecoveryViewModel(authRepository) }
    private val registerViewModel by lazy { RegisterViewModel(authRepository) }
    private val profileViewModel by lazy { ProfileViewModel(authRepository) }
    private val racksViewModel by lazy { RacksViewModel(rackRepository) }
    private val addRackViewModel by lazy { AddRackViewModel(rackRepository) }
    private val rackDetailViewModel by lazy { RackDetailViewModel(rackRepository) }
    private val rackHistoryViewModel by lazy { RackHistoryViewModel(rackRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenStore.attach(this)
        SessionManager.attach(RetrofitInstance.api)
        lifecycleScope.launch {
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
                        rackDetailViewModel = rackDetailViewModel,
                        rackHistoryViewModel = rackHistoryViewModel
                    )
                }
            }
        }
    }
}
