package de.liruhg.lirucloud.library.user

data class CloudWebUser(
    val id: String,
    val ingameName: String,
    val email: String,
    val password: String,
    val roles: List<String>
)