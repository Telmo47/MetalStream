package com.metalstream.app

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Double,
    val fileName: String,
    val streamUrl: String
)