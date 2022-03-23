package com.arezoonazer.player.util.track

data class TrackEntity(
    val title: String,
    val index: Int,
    val isSelected: Boolean,
    val onItemClick: (Int) -> Unit
)