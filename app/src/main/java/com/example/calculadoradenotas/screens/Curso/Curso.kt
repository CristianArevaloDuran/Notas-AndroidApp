package com.example.calculadoradenotas.screens.Curso

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calculadoradenotas.data.CursoDatabase
import com.example.calculadoradenotas.data.curso.CursoViewModel
import com.example.calculadoradenotas.ui.theme.poppinsFamily
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.calculadoradenotas.data.curso.CursoDao
import com.example.calculadoradenotas.data.curso.CursoEntity
import com.example.calculadoradenotas.data.nota.NotaDao
import com.example.calculadoradenotas.data.nota.NotaEntity
import com.example.calculadoradenotas.data.nota.NotaViewModel
import com.example.calculadoradenotas.navigation.AppScreens
import com.example.calculadoradenotas.screens.Home.UpdateNameDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Curso(navController: NavController, index: Int?) {
    val context = LocalContext.current
    val database = remember { CursoDatabase.getDatabase(context) }
    val cursoDao = database.cursoDao()
    val notaDao = database.notaDao()
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoDao)
    )
    val coroutineScope = rememberCoroutineScope()
    val notasViewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(notaDao)
    )

    val curso by cursoViewModel.getCurso(index).collectAsState(initial = null)


    LaunchedEffect(index) {
        notasViewModel.getNotas(index)
    }

    val notas by notasViewModel.notas.collectAsState()

    var showNotaDialog by remember { mutableStateOf(false) }
    var showObjetivoDialog by remember { mutableStateOf(false) }
    var showTotalNotasDialog by remember { mutableStateOf(false) }
    var showTotalNotasErrorDialog by remember { mutableStateOf(false) }

    var isEnabled = if (notas.size == curso?.totalNotas) {
        false
    } else {
        true
    }

    var promedioActual = String.format("%.2f",(notas.sumOf { it.calificacion }) / (curso?.totalNotas ?: 0))
    var notaNecesaria = String.format("%.2f",((curso?.objetivo?.toDouble() ?: 0.0) * (curso?.totalNotas?.toDouble() ?: 0.0) - notas.sumOf { it.calificacion }) / ((curso?.totalNotas ?: 0) - notas.size))
    var porcentajeCompletado: Double = 0.0

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                title = {
                    Text("${curso?.curso}",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (isEnabled) {
                    showNotaDialog = true
                    }
                },
                containerColor = if (!isEnabled) { Color(0xFFBDBDBD) } else { FloatingActionButtonDefaults.containerColor }
            ) {
                Icon(Icons.Filled.Add, "Añadir")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            if (curso?.progreso?.toFloat() != 0f) {
                LinearProgressIndicator(
                    progress = { curso?.progreso?.toFloat() ?: 0.0f },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 10.dp, bottom = 10.dp)
                        .height(10.dp)
                        .fillMaxWidth(),
                    color = curso?.let { Color(it.color) } ?: Color.Gray
                )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),

            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp),
                        onClick = {
                            showObjetivoDialog = true
                        }
                    ) {
                        Column(
                            Modifier.fillMaxSize()
                                .then(
                                    if (curso?.objetivo != 0.0 && promedioActual.toDouble() >= (curso?.objetivo ?: 0.0)) {
                                        Modifier.background(Color(0xFFD9FFDE))
                                            .border(6.dp, Color(0xFF82F0A3), shape = RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Objetivo",
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                            if (curso?.objetivo != 0.0){
                                Text("${curso?.objetivo}",
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    if (promedioActual.toDouble() >= (curso?.objetivo ?: 0.0) && (curso?.objetivo ?: 0.0) != 0.0) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Check",
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = -8.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp),
                        onClick = {
                            showTotalNotasDialog = true
                        }

                    ) {
                        Column(
                            Modifier.fillMaxSize()
                                .then(
                                    if (notas.isNotEmpty() && notas.size == curso?.totalNotas) {
                                        Modifier.background(Color(0xFFD9FFDE))
                                            .border(6.dp, Color(0xFF82F0A3), shape = RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total de notas",
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                            if (curso?.totalNotas != 0) {
                                Text("${curso?.totalNotas}",
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    if (notas.isNotEmpty() && notas.size == curso?.totalNotas) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Check",
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = -8.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),

                ) {
                if ((curso?.totalNotas ?: 0) != 0 && (curso?.objetivo ?: 0.0) != 0.0 && isEnabled) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 10.dp)
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                        ) {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Nota necesaria",
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                if (curso?.objetivo != 0.0){
                                    Text("${notaNecesaria}",
                                        fontFamily = poppinsFamily,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 5.dp),
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                if ((curso?.totalNotas ?: 0) != 0 && (curso?.objetivo ?: 0.0) != 0.0) {
                    Box(
                        modifier = Modifier.weight(1f).padding(bottom = 10.dp)
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                        ) {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Promedio actual",
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                if (curso?.totalNotas != 0) {
                                    Text("${promedioActual}",
                                        fontFamily = poppinsFamily,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 5.dp),
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            curso?.let { cursoSeguro ->
                DetallesCurso(
                    navController,
                    notas,
                    notaDao,
                    coroutineScope,
                    porcentajeCompletado,
                    cursoDao,
                    cursoSeguro
                )
            }
        }
    }
    if (showNotaDialog) {
        AddNotaDialog(
            onDismiss = {showNotaDialog=false},
            onConfirm = {nota ->
                coroutineScope.launch {
                    curso?.let { cursoSeguro ->
                        val notaNueva = NotaEntity(cursoId = cursoSeguro.id, calificacion = nota.toDouble())
                        notaDao.insertNota(notaNueva)
                        porcentajeCompletado = (((notas.sumOf { it.calificacion }.toDouble() + nota.toDouble() ) / cursoSeguro.totalNotas.toDouble()) / cursoSeguro.objetivo.toDouble()).toDouble()
                        cursoDao.updateProgreso(cursoSeguro.id, porcentajeCompletado)
                        showNotaDialog = false
                    }
                }
            }
        )
    }

    if (showObjetivoDialog) {
        ObjetivoDialog(
            onDismiss = {showObjetivoDialog = false},
            onConfirm = { objetivo ->
                coroutineScope.launch {
                    curso?.let{ cursoSeguro ->
                        val id = cursoSeguro.id
                        val objetivo = objetivo.toDouble()
                        cursoDao.updateObjetivo(id, objetivo)
                        porcentajeCompletado = if(notas.isEmpty()) {
                            0.0
                        } else {
                            (((notas.sumOf { it.calificacion }.toDouble()) / cursoSeguro.totalNotas.toDouble()) / objetivo.toDouble()).toDouble()
                        }
                        cursoDao.updateProgreso(cursoSeguro.id, porcentajeCompletado)
                        showObjetivoDialog = false
                    }
                }
            }
        )
    }

    if (showTotalNotasDialog) {
        TotalNotasDialog(
            onDismiss = {showTotalNotasDialog = false},
            onConfirm = { totalNotas ->
                if (totalNotas.toInt() < notas.size) {
                    showTotalNotasErrorDialog = true
                    showTotalNotasDialog = false
                } else {
                    coroutineScope.launch {
                        curso?.let { cursoSeguro ->
                            val id = cursoSeguro.id
                            val totalNotas = totalNotas.toInt()
                            cursoDao.updateTotalNotas(id, totalNotas)
                            porcentajeCompletado = if(notas.isEmpty()) {
                                0.0
                            } else {
                                (((notas.sumOf { it.calificacion }.toDouble()) / totalNotas.toDouble()) / cursoSeguro.objetivo.toDouble()).toDouble()
                            }
                            cursoDao.updateProgreso(cursoSeguro.id, porcentajeCompletado)
                            showTotalNotasDialog = false
                        }
                    }
                }
            }
        )
    }

    if(showTotalNotasErrorDialog) {
        ErrorDialog(
            text = "El número total de notas no puede ser menor a las que ya existen",
            onDismiss = { showTotalNotasErrorDialog = false }
        )
    }
}

@Composable
fun ErrorDialog(
    onDismiss: () -> Unit,
    text: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Alerta!",
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal
        ) },
        text = {
            Text("${text}",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ok",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}

@Composable
fun AddNotaDialog(
    onDismiss: () -> Unit,
    onConfirm:(String) -> Unit
) {
    var nota by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agregar nota",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal
            )
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = nota,
                onValueChange = { nota = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nota",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nota.isNotBlank()) {
                        onConfirm(nota.trim())
                    }
                },
                enabled = nota.toDoubleOrNull() != null && nota.toDouble() <= 5.0 && nota.toDouble() >= 0
            ) {
                Text("Agregar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )

}

@Composable
fun ObjetivoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var objetivo by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modificar objetivo",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal
            )
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = objetivo,
                onValueChange = { objetivo = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nota",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (objetivo.isNotBlank()) {
                        onConfirm(objetivo.trim())
                    }
                },
                enabled = objetivo.toDoubleOrNull() != null && objetivo.toDouble() <= 5.0 && objetivo.toDouble() >= 0
            ) {
                Text("Modificar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}

@Composable
fun TotalNotasDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var totalNotas by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modificar total de notas",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal
            )
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = totalNotas,
                onValueChange = { totalNotas = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nota",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (totalNotas.isNotBlank()) {
                        onConfirm(totalNotas.trim())
                    }
                },
                enabled = totalNotas.toIntOrNull() != null && totalNotas.toInt() >= 0
            ) {
                Text("Modificar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}

@Composable
fun DetallesCurso(
    navController: NavController,
    notas: List<NotaEntity>,
    notaDao: NotaDao,
    coroutineScope: CoroutineScope,
    porcentajeCompletado: Double,
    cursoDao: CursoDao,
    curso: CursoEntity
) {
    LazyColumn {
        items(count = notas.size) { index ->
            val nota = notas[index]
            NotaCard(nota, index+1, notaDao, coroutineScope, porcentajeCompletado, cursoDao, curso, notas)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotaCard(
    nota: NotaEntity,
    index: Int,
    notaDao: NotaDao,
    coroutineScope: CoroutineScope,
    porcentajeCompletado: Double,
    cursoDao: CursoDao,
    curso: CursoEntity,
    notas: List<NotaEntity>
) {
    var notaColor = if (nota.calificacion < 3.0) {
        Color(0xFFF0648C)
    } else {
        Color(0xFF82F0A3)
    }
    var showNotaModal by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                showNotaModal = true
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier.background(notaColor)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ){
                    Text("${nota.calificacion}",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(top = 25.dp)
                    )
                    Text("${index}",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
    if (showNotaModal) {
        NotaModalBottom(
            onDismiss = {showNotaModal = false},
            nota = nota,
            notaDao = notaDao,
            coroutineScope = coroutineScope,
            porcentajeCompletado = porcentajeCompletado,
            cursoDao = cursoDao,
            curso = curso,
            notas = notas
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaModalBottom(
    onDismiss: () -> Unit,
    nota: NotaEntity,
    notaDao: NotaDao,
    coroutineScope: CoroutineScope,
    porcentajeCompletado: Double,
    cursoDao: CursoDao,
    curso: CursoEntity,
    notas: List<NotaEntity>
) {
    var showNotaUpdate by remember { mutableStateOf(false) }
    var porcentajeNuevo: Double
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = nota.calificacion.toString(),
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showNotaUpdate = true
                    }
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Editar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Icon(Icons.Default.Edit, "Editar",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            notaDao.deleteNota(nota)
                            if (notas.size > 1) {
                                porcentajeNuevo = (((notas.sumOf { it.calificacion }.toDouble() - nota.calificacion.toDouble() ) / curso.totalNotas.toDouble()) / curso.objetivo.toDouble()).toDouble()
                                cursoDao.updateProgreso(curso.id, porcentajeNuevo)
                            } else {
                                porcentajeNuevo = 0.0
                                cursoDao.updateProgreso(curso.id, porcentajeNuevo)
                            }
                            onDismiss()
                        }
                    }
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Borrar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Red
                )
                Icon(Icons.Default.Delete, "Borrar",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Red
                )
            }

        }
    }
    if (showNotaUpdate) {
        UpdateNotaDialog(
            onDismiss = { showNotaUpdate = false },
            onConfirm = { notaCal ->
                if (notaCal.toDouble() == nota.calificacion) {
                    showNotaUpdate = false
                } else {
                    coroutineScope.launch {
                        notaDao.updateNota(nota.id, notaCal.toDouble())
                        porcentajeNuevo = ((((notas.sumOf { it.calificacion }.toDouble() - nota.calificacion) + notaCal.toDouble() ) / curso.totalNotas.toDouble()) / curso.objetivo.toDouble()).toDouble()
                        cursoDao.updateProgreso(curso.id, porcentajeNuevo)
                        showNotaUpdate = false
                    }
                }
            },
            nota = nota
        )
    }
}

@Composable
fun UpdateNotaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    nota: NotaEntity
) {
    var notaNueva by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Icono añadir",
                    modifier = Modifier.size(40.dp)
                )
                Text("Editar ${nota.calificacion}",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = notaNueva,
                onValueChange = { notaNueva = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nota",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (notaNueva.isNotBlank()) {
                        onConfirm(notaNueva.trim())
                    }
                },
                enabled = notaNueva.isNotBlank()
            ) {
                Text("Editar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}
