package com.example.rackstracker.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rackstracker.model.RackStatus
import com.example.rackstracker.components.BackLink
import com.example.rackstracker.components.PrimaryButton
import com.example.rackstracker.components.RacksDropdownField
import com.example.rackstracker.components.RacksTextField
import com.example.rackstracker.components.SecondaryButton
import com.example.rackstracker.components.SectionHeader
import com.example.rackstracker.ui.theme.CardBorder
import com.example.rackstracker.ui.theme.Mint
import com.example.rackstracker.ui.theme.SurfaceElevated
import com.example.rackstracker.ui.theme.TextMuted
import com.example.rackstracker.ui.theme.TextPrimary
import com.example.rackstracker.ui.theme.TextSecondary
import com.example.rackstracker.viewmodel.AddRackViewModel
import java.io.File

@Composable
fun AddRackScreen(
    navController: NavController,
    viewModel: AddRackViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    fun crearUriImagen(): Uri {
        val directorio = File(context.cacheDir, "images")
        if (!directorio.exists()) directorio.mkdirs()
        val archivo = File.createTempFile("rack_", ".jpg", directorio)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImagenChange(uri.toString())
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exitoso ->
        if (exitoso) {
            tempCameraUri?.let { viewModel.onImagenChange(it.toString()) }
        }
        tempCameraUri = null
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = crearUriImagen()
            tempCameraUri = uri
            camaraLauncher.launch(uri)
        }
    }

    LaunchedEffect(state.guardado) {
        if (state.guardado) {
            viewModel.consumirGuardado()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        BackLink(text = "Volver a mis racks", onClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Text("Agregar rack", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(
                "Registra un nuevo rack para empezar a gestionar tu infraestructura.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(10.dp))
            Text("* Campos obligatorios", color = TextMuted, fontSize = 12.sp)

            FormSection(icon = { Icon(Icons.Filled.GridView, null, tint = Mint, modifier = Modifier.size(18.dp)) }, title = "Datos del rack")
            RacksTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = "Nombre del rack*",
                placeholder = "Ej. Rack A-03"
            )
            Spacer(Modifier.height(20.dp))
            RacksTextField(
                value = state.codigo,
                onValueChange = viewModel::onCodigoChange,
                label = "Identificador o código*",
                placeholder = "Ej. MAD-A-03",
                helperText = "Usa un código único para identificarlo.",
                leadingIcon = { Icon(Icons.Filled.Numbers, null, tint = TextMuted) }
            )

            FormSection(icon = { Icon(Icons.Filled.LocationOn, null, tint = Mint, modifier = Modifier.size(18.dp)) }, title = "Ubicación")
            RacksDropdownField(
                label = "Sede *",
                selected = state.sede,
                options = AddRackViewModel.sedes,
                optionLabel = { it.toString() },
                onSelect = { viewModel.onSedeChange(it.toString()) },
                placeholder = "Selecciona una sede"
            )
            Spacer(Modifier.height(20.dp))
            RacksDropdownField(
                label = "Sala",
                selected = state.sala,
                options = AddRackViewModel.salas,
                optionLabel = { it.toString() },
                onSelect = { viewModel.onSalaChange(it.toString()) },
                placeholder = "Selecciona una sala"
            )
            Spacer(Modifier.height(20.dp))
            RacksTextField(
                value = state.posicion,
                onValueChange = viewModel::onPosicionChange,
                label = "Posición *",
                placeholder = "Ej. Fila A • Posición 03",
                helperText = "Indica dónde se encuentra dentro de la sala."
            )

            FormSection(icon = { Icon(Icons.Filled.GridView, null, tint = Mint, modifier = Modifier.size(18.dp)) }, title = "Capacidad y estado")
            RacksTextField(
                value = state.capacidad,
                onValueChange = viewModel::onCapacidadChange,
                label = "Capacidad en unidades U*",
                placeholder = "Ej. 42",
                keyboardType = KeyboardType.Number,
                helperText = "Altura útil del rack. Introduce un número entero."
            )
            Spacer(Modifier.height(20.dp))
            RacksDropdownField(
                label = "Estado *",
                selected = state.estado.label,
                options = RackStatus.entries,
                optionLabel = { (it as RackStatus).label },
                onSelect = { viewModel.onEstadoChange(it as RackStatus) },
                placeholder = "Selecciona un estado",
                helperText = "Activo, Inactivo o en mantenimiento."
            )

            FormSection(icon = { Icon(Icons.Filled.Image, null, tint = Mint, modifier = Modifier.size(18.dp)) }, title = "Imagen de rack")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlineActionButton(
                    text = "Seleccionar archivo",
                    icon = { Icon(Icons.Filled.CloudUpload, null, tint = TextPrimary, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        galeriaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                OutlineActionButton(
                    text = "Tomar foto",
                    icon = { Icon(Icons.Filled.AddAPhoto, null, tint = TextPrimary, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permiso == PackageManager.PERMISSION_GRANTED) {
                            val uri = crearUriImagen()
                            tempCameraUri = uri
                            camaraLauncher.launch(uri)
                        } else {
                            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
            }
            Spacer(Modifier.height(12.dp))

            if (state.imagenUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = state.imagenUri,
                        contentDescription = "Imagen del rack",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { viewModel.quitarImagen() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SurfaceElevated.copy(alpha = 0.9f))
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Quitar foto", tint = TextPrimary)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Text(
                "Añade una imagen del rack para identificarlo más rápido.",
                color = TextMuted,
                fontSize = 12.sp
            )

            FormSection(icon = { Icon(Icons.Filled.Numbers, null, tint = Mint, modifier = Modifier.size(18.dp)) }, title = "Notas")
            RacksTextField(
                value = state.notas,
                onValueChange = viewModel::onNotasChange,
                label = "Notas",
                placeholder = "Añade observaciones, instrucciones de acceso o detalles del rack",
                singleLine = false,
                minLines = 4,
                maxLines = 6,
                optionalTag = "Opcional"
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("${state.notas.length} / 500 caracteres", color = TextMuted, fontSize = 12.sp)
            }

            Spacer(Modifier.height(8.dp))
            InfoNote("El rack se registrará sin equipos. Podrás añadirlos desde su ficha.")

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = "Registrar rack",
                onClick = viewModel::registrar,
                enabled = state.formularioValido
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Cancelar y volver",
                onClick = { navController.popBackStack() }
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FormSection(icon: @Composable () -> Unit, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.size(8.dp))
        SectionHeader(text = title)
    }
}

@Composable
private fun OutlineActionButton(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceElevated,
            contentColor = TextPrimary
        ),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        icon()
        Spacer(Modifier.size(8.dp))
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoNote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Mint.copy(alpha = 0.08f))
            .border(1.dp, Mint.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Info, null, tint = Mint, modifier = Modifier.size(16.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
