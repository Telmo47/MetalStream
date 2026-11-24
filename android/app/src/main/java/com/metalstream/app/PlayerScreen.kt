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
    // Estados locais da UI (para testar os cliques e a barra)
    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0.35f) }

    // Tema Dark Metal por defeito
    val metalDarkColors = darkColorScheme(
        primary = Color(0xFFE50914), // Vermelho vibrante Metal
        background = Color(0xFF121212), // Preto profundo
        surface = Color(0xFF1E1E1E), // Cinza escuro para cartões
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
                // 1. Top Bar / Cabeçalho
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
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Capa do Álbum",
                            modifier = Modifier.size(120.dp),
                            tint = Color.Gray
                        )
                    }
                }

                // 3. Informações da Faixa + Barra de Progresso + Controlos
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título e Artista
                    Text(
                        text = "Master Of Puppets",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Metallica - The Greatest Hits CD1",
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Barra de progresso (Slider)
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

                    // Tempos (Decorrente vs Total)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "03:00", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "08:34", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botões de Controlo (SkipPrevious, Play/Pause, SkipNext)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* Anterior */ },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Anterior",
                                modifier = Modifier.size(36.dp),
                                tint = Color.White
                            )
                        }

                        // Botão Play/Pause principal destacado
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

                        IconButton(
                            onClick = { /* Seguinte */ },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Seguinte",
                                modifier = Modifier.size(36.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}