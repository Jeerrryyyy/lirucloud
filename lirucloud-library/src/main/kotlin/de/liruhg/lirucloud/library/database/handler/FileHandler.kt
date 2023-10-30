package de.liruhg.lirucloud.library.database.handler

import java.io.File

interface FileHandler {

    fun uploadFile(file: File, fileNameHash: String)
    fun downloadFile(fileNameHash: String, file: File)
    fun deleteFile(fileNameHash: String)
    fun replaceFile(file: File, fileNameHash: String)
}