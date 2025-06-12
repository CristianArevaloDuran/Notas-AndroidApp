package com.example.calculadoradenotas.screens.Home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.calculadoradenotas.data.CursoDatabase
import com.example.calculadoradenotas.data.curso.CursoDao
import com.example.calculadoradenotas.data.curso.CursoEntity
import com.example.calculadoradenotas.navigation.AppScreens
import com.example.calculadoradenotas.ui.theme.CalculadoraDeNotasTheme
import com.example.calculadoradenotas.ui.theme.poppinsFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    //context.deleteDatabase("cursos_database")
    val database = remember { CursoDatabase.getDatabase(context) }
    val cursoDao = database.cursoDao()
    val cursos by cursoDao.getCursos().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    CalculadoraDeNotasTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        Icon(Icons.Outlined.Book, "Libro", modifier = Modifier.size(40.dp))
                    },
                    title = {
                        Text("Mis cursos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            fontFamily = poppinsFamily
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(Icons.Filled.Add, "Add course")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                HomeBody(navController, cursos, cursoDao, coroutineScope)
            }
        }
    }
    if (showDialog) {
        AddCursoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { curso ->
                coroutineScope.launch {
                    val nuevoCurso = CursoEntity(curso = curso, color = getRandomColor())
                    cursoDao.insertCurso(nuevoCurso)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun HomeBody(
    navController: NavController,
    cursos: List<CursoEntity>,
    cursoDao: CursoDao,
    coroutineScope: CoroutineScope
) {
    val emptyColor = Color(0xFF949494)
    var dismissState = rememberSwipeToDismissBoxState()
    LazyColumn (
        modifier = Modifier.
            fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        if (cursos.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Icon(Icons.Filled.Add, "Añadir",
                        modifier = Modifier.size(60.dp),
                        tint = emptyColor
                    )
                    Text(
                        "No hay cursos agregados",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = emptyColor
                    )
                    Text(
                        "Toca + para añadir un nuevo curso",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = emptyColor

                    )
                }
            }
        } else {
            items(count = cursos.size) { index ->
                val curso = cursos[index]
                BannerCard(curso, navController, cursoDao, coroutineScope)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoModalBottom(
    onDismiss: () -> Unit,
    curso: CursoEntity,
    cursoDao: CursoDao,
    coroutineScope: CoroutineScope
) {
    var showCursoUpdate by remember { mutableStateOf(false) }
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
                text = curso.curso,
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showCursoUpdate = true
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
                            cursoDao.deleteCurso(curso)
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
    if (showCursoUpdate) {
        UpdateNameDialog(
            onDismiss = { showCursoUpdate = false },
            onConfirm = { cursoName ->
                if (cursoName == curso.curso) {
                    showCursoUpdate = false
                } else {
                    coroutineScope.launch {
                        cursoDao.updateName(curso.id, cursoName)
                        showCursoUpdate = false
                    }
                }
            },
            curso = curso
        )
    }
}

@Composable
fun AddCursoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var curso by remember { mutableStateOf("") }
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
                Text("Añadir curso",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = curso,
                onValueChange = { curso = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nombre del curso",
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
                    if (curso.isNotBlank()) {
                        onConfirm(curso.trim())
                    }
                },
                enabled = curso.isNotBlank()
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
fun UpdateNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    curso: CursoEntity
) {
    var cursoName by remember { mutableStateOf("") }
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
                Text("Editar ${curso.curso}",
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = cursoName,
                onValueChange = { cursoName = it },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontFamily = poppinsFamily),
                shape = RoundedCornerShape(20.dp),
                label = {
                    Text("Nombre del curso",
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
                    if (cursoName.isNotBlank()) {
                        onConfirm(cursoName.trim())
                    }
                },
                enabled = cursoName.isNotBlank()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCard(
    curso: CursoEntity,
    navController: NavController,
    cursoDao: CursoDao,
    coroutineScope: CoroutineScope
) {
    var showModal by remember { mutableStateOf(false) }
    Box {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        showModal = true
                    },
                    onClick = { navController.navigate(AppScreens.Curso.route + "/${curso.id}") }
                ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(curso.color))
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    if (curso.progreso.toFloat() != 0f) {
                        LinearProgressIndicator(
                            progress = { curso.progreso.toFloat() },
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .height(10.dp)
                                .fillMaxWidth(),
                            color = Color(curso.color),
                            trackColor = Color.White
                        )
                    }
                    Text(
                        text = curso.curso,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        if (curso.progreso >= 1) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = "Check",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = -10.dp, y = 10.dp),
                tint = Color(0xFF82F0A3)
            )
        }
    }

    if (showModal) {
        CursoModalBottom(
            onDismiss = {showModal=false},
            curso = curso,
            cursoDao = cursoDao,
            coroutineScope = coroutineScope
        )
    }
}

fun getRandomColor(): Long {
    val colors = listOf(
        0xFFE57373,
        0xFF64B5F6,
        0xFF81C784,
        0xFFFFB74D,
        0xFFBA68C8,
        0xFFFF8A65
    )
    return colors.random()
}