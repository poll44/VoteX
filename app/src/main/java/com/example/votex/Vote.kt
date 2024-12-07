package com.example.votex

data class Vote(
    val createdBy: String,
    val description: String,
    val endDate: String,
    val endTime: String,
    val pin: String?,
    val selectedPhoto: String?,
    val title: String,
    val type: String,
    val unicID: String,
    val voteClosed: Boolean,
    val options: Map<String, OptionDetail> // Menyimpan daftar opsi dan detailnya
)

data class OptionDetail(
    val pilihan: String,           // Teks opsi
    val voters: List<String>       // Daftar UID voters yang memilih opsi ini
)



