package com.example.playit.dataclass

data class TrackDetails(
    val id: String,
    val title: String,
    val album: String,
    val artists: String,
    val duration: Long = 0,
    val path: String,
    val artURI: String,
)