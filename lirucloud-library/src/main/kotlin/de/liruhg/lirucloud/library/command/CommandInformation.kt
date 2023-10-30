package de.liruhg.lirucloud.library.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandInformation(
    val command: String,
    val description: String,
    val aliases: Array<String> = [],
)
