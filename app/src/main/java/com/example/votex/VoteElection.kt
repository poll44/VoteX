package com.example.votex

data class VoteElection(
    val createdBy: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "Election",
    val unicId: String = "",
    val voteClosed: Boolean = false,
    val options: Map<String, Option> = mapOf(),
    val credential: Credential = Credential()
) {
    data class Option(
        val teamAlias: String = "",
        val candidateFullName: String = "",
        val motto: String = "",
        val selectedPhoto: String = "",
        val voter: List<String> = listOf()
    )
    data class Credential(
        val voterCredentialType: List<CredentialType> = listOf(),
        val voterCredential: Map<String, Map<String, String>> = mapOf()
    ) {
        data class CredentialType(
            val nameTagType: String,
            val typeTag: String,
            val maxInput: Int,
            val isMandatory: Boolean
        )
    }
}