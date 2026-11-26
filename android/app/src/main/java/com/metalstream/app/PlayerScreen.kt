package com.metalstream.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerScreen() {
    // Estados do Player e da API
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0.0f) }

    // Chamada à API C# em segundo plano ao abrir o ecrã
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            val response = NetworkClient.api.getSongs()
            songs = response
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Erro ao contactar API: ${e.localizedMessage}"
            isLoading = false
        }
    }

    val currentSong = songs.getOrNull(currentIndex)

    // Formatar duração (segundos para mm:ss)
    val totalDurationFormatted = currentSong?.let {
        val totalSecs = it.durationSeconds.toInt()
        val minutes = totalSecs / 60
        val seconds = totalSecs % 60
        String.format("%02d:%02d", minutes, seconds)
    } ?: "00:00"

    // Tema Dark Metal por defeito
    val metalDarkColors = darkColorScheme(
        primary = Color(0xFFE50914),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(colorScheme = metalDarkColors) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Cabeçalho
                Text(
                    text = "METALSTREAM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // 2. Capa do Álbum centralizada
                Card(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Capa do Álbum",
                                modifier = Modifier.size(120.dp),
                                tint = if (currentSong != null) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }

                // 3. Informações da Faixa + Barra de Progresso + Controlos
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                    } else {
                        // Título e Artista dinâmicos vindos da API
                        Text(
                            text = currentSong?.title ?: if (isLoading) "A carregar músicas..." else "Nenhuma música encontrada",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSong?.let { "${it.artist} • ${it.album}" } ?: "",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Barra de progresso
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )

                    // Tempos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "00:00", fontSize = 12.sp, color = Color.Gray)
                        Text(text = totalDurationFormatted, fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botões de Controlo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botão Faixa Anterior
                        IconButton(
                            onClick = {
                                if (songs.isNotEmpty()) {
                                    currentIndex = if (currentIndex - 1 < 0) songs.size - 1 else currentIndex - 1
                                }
                            },
                            enabled = songs.isNotEmpty(),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Anterior",
                                modifier = Modifier.size(36.dp),
                                tint = if (songs.isNotEmpty()) Color.White else Color.DarkGray
                            )
                        }

                        // Botão Play/Pause principal
                        FloatingActionButton(
                            onClick = { isPlaying = !isPlaying },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Botão Faixa Seguinte
                        IconButton(
                            onClick = {
                                if (songs.isNotEmpty()) {
                                    currentIndex = (currentIndex + 1) % songs.size
                                }
                            },
                            enabled = songs.isNotEmpty(),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Seguinte",
                                modifier = Modifier.size(36.dp),
                                tint = if (songs.isNotEmpty()) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}