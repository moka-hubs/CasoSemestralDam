package com.example.rackstracker

import com.example.rackstracker.model.Usuario
import com.example.rackstracker.session.SessionManager
import com.example.rackstracker.viewmodel.LoginViewModel
import com.example.rackstracker.viewmodel.ProfileViewModel
import com.example.rackstracker.viewmodel.RecoveryViewModel
import com.example.rackstracker.viewmodel.RegisterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        TestSession.reset()
    }

    @Test
    fun `login exitoso actualiza estado y sesion`() = runTest {
        val usuario = Usuario.GENERICO
        val repositorio = FakeAuthRepository().apply {
            loginResult = Result.success(usuario)
        }
        val viewModel = LoginViewModel(repositorio)

        viewModel.onCorreoChange("  Admin@Racks.com  ")
        viewModel.onContrasenaChange("admin1234")
        viewModel.iniciarSesion()
        advanceUntilIdle()

        assertEquals("Admin@Racks.com", repositorio.ultimoLogin?.first)
        assertEquals("admin1234", repositorio.ultimoLogin?.second)
        assertTrue(viewModel.uiState.value.loginExitoso)
        assertEquals(usuario, viewModel.uiState.value.usuario)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        assertEquals(usuario, SessionManager.usuario.value)
    }

    @Test
    fun `login fallido muestra el error y no abre sesion`() = runTest {
        val repositorio = FakeAuthRepository().apply {
            loginResult = Result.failure(Exception("Credenciales inválidas"))
        }
        val viewModel = LoginViewModel(repositorio)

        viewModel.iniciarSesion()
        advanceUntilIdle()

        assertEquals("Credenciales inválidas", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.loginExitoso)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(SessionManager.usuario.value)
    }

    @Test
    fun `registro exitoso guarda la sesion`() = runTest {
        val usuario = Usuario.GENERICO
        val repositorio = FakeAuthRepository().apply {
            registroResult = Result.success(usuario)
        }
        val viewModel = RegisterViewModel(repositorio)

        viewModel.onNombreChange("Ana")
        viewModel.onApellidoChange("Pérez")
        viewModel.onRutChange("12.345.678-5")
        viewModel.onCorreoChange("ana@racks.com")
        viewModel.onContrasenaChange("password123")
        assertTrue(viewModel.uiState.value.formularioValido)

        viewModel.crearCuenta()
        advanceUntilIdle()

        val args = repositorio.ultimoRegistro
        assertNotNull(args)
        assertEquals("Ana", args!!.nombre)
        assertEquals("ana@racks.com", args.correo)
        assertEquals("password123", args.contrasena)
        assertTrue(viewModel.uiState.value.registroExitoso)
        assertEquals(usuario, SessionManager.usuario.value)
    }

    @Test
    fun `registro con campos incompletos no es valido`() = runTest {
        val repositorio = FakeAuthRepository()
        val viewModel = RegisterViewModel(repositorio)

        viewModel.onNombreChange("Ana")
        viewModel.onContrasenaChange("corta")

        assertFalse(viewModel.uiState.value.formularioValido)
        assertNull(repositorio.ultimoRegistro)
    }

    @Test
    fun `recuperacion exitosa muestra solo confirmacion`() = runTest {
        val repositorio = FakeAuthRepository()
        val viewModel = RecoveryViewModel(repositorio)

        viewModel.onCorreoChange("  Admin@Racks.com  ")
        viewModel.enviarMensaje()
        advanceUntilIdle()

        assertEquals("Admin@Racks.com", repositorio.ultimoCorreoRecuperacion)
        assertTrue(viewModel.uiState.value.enviado)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `recuperacion fallida muestra error`() = runTest {
        val repositorio = FakeAuthRepository().apply {
            recuperacionResult = Result.failure(Exception("Sin conexión"))
        }
        val viewModel = RecoveryViewModel(repositorio)

        viewModel.onCorreoChange("admin@racks.com")
        viewModel.enviarMensaje()
        advanceUntilIdle()

        assertEquals("Sin conexión", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.enviado)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `perfil guarda los cambios contra Xano`() = runTest {
        val usuario = Usuario.GENERICO
        TestSession.iniciar(usuario)
        val repositorio = FakeAuthRepository()
        val viewModel = ProfileViewModel(repositorio)

        viewModel.iniciarEdicion()
        viewModel.onNombreChange("  Nuevo  ")
        viewModel.onApellidoChange(" Nombre ")
        viewModel.guardar()
        advanceUntilIdle()

        val args = repositorio.ultimaActualizacion
        assertNotNull(args)
        assertEquals(usuario.correo, args!!.first)
        assertEquals("Nuevo", args.second.nombre)
        assertEquals("Nombre", args.second.apellido)
        assertTrue(viewModel.uiState.value.guardado)
        assertNull(viewModel.uiState.value.error)
        assertEquals("Nuevo", SessionManager.usuario.value?.nombre)
    }

    @Test
    fun `perfil muestra error cuando falla la actualizacion`() = runTest {
        val usuario = Usuario.GENERICO
        TestSession.iniciar(usuario)
        val repositorio = FakeAuthRepository().apply {
            actualizacionResult = Result.failure(Exception("Error del servidor"))
        }
        val viewModel = ProfileViewModel(repositorio)

        viewModel.iniciarEdicion()
        viewModel.onNombreChange("Otro nombre")
        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Error del servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.guardado)
        assertEquals(usuario.nombre, SessionManager.usuario.value?.nombre)
    }
}
