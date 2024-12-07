package com.example.votex

data class User(
    val profilePhoto: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val birthDate: String = "",
    val place: String = "",
    val hasVotedAt: List<String>? = null
) {
    // Konstruktor tanpa argumen dibutuhkan oleh Firebase
    constructor() : this("", "", "", "", "", "", null)
}
