package de.liruhg.lirucloud.library.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileUtils {

    companion object {
        private val gson: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        fun getAllFiles(startDirectory: File): MutableList<File> {
            return Files.walk(startDirectory.toPath()).map { it.toFile() }.collect(Collectors.toList())
        }

        fun deleteFullDirectory(path: Path) {
            val files = path.toFile().listFiles() ?: return

            Arrays.stream(files).forEach {
                if (it.isDirectory) deleteFullDirectory(it.toPath())
                else it.delete()
            }

            path.toFile().delete()
        }

        fun deleteFullDirectory(path: File) {
            deleteFullDirectory(path.toPath())
        }

        fun deleteFullDirectory(path: String) {
            deleteFullDirectory(Paths.get(path))
        }

        fun copyFile(from: Path, to: Path) {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyFile(from: String, to: String) {
            copyFile(Paths.get(from), Paths.get(to))
        }

        fun copyFile(from: File, to: File) {
            copyFile(from.toPath(), to.toPath())
        }

        fun copyFileFromStream(inputStream: InputStream, to: String) {
            Files.copy(inputStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyCompiledFile(from: String, to: String) {
            val inputStream = this::class.java.classLoader.getResourceAsStream(from) ?: return
            Files.copy(inputStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyCompiledFile(from: Path, to: Path) {
            copyCompiledFile(from.toString(), to.toString())
        }

        fun copyCompiledFile(from: File, to: File) {
            copyCompiledFile(from.toString(), to.toString())
        }

        fun renameFile(file: File, name: String) {
            file.renameTo(File(name))
        }

        fun renameFile(file: Path, name: String) {
            renameFile(file.toFile(), name)
        }

        fun renameFile(file: String, name: String) {
            renameFile(Paths.get(file), name)
        }

        fun copyAllFiles(directory: Path, targetDirectory: String) {
            if (!Files.exists(directory)) return

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        fun copyAllFiles(directory: Path, targetDirectory: String, excluded: String) {
            if (!Files.exists(directory)) return

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    if (file.fileName.equals(excluded)) return FileVisitResult.CONTINUE

                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        fun copyAllFiles(directory: Path, targetDirectory: String, excluded: Array<String>) {
            if (!Files.exists(directory)) return

            val excludedFiles: MutableList<Path> =
                Arrays.stream(excluded).map { Paths.get(it) }.collect(Collectors.toList())

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    if (excludedFiles.contains(file)) return FileVisitResult.CONTINUE

                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        private fun tryAndCopy(targetDirectory: String, directory: Path, file: Path): FileVisitResult {
            val target = Paths.get(targetDirectory, directory.relativize(file).toString())
            val parent = target.parent

            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent)

            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
            return FileVisitResult.CONTINUE
        }

        fun deleteIfExists(path: Path) {
            Files.deleteIfExists(path)
        }

        fun deleteIfExists(path: File) {
            deleteIfExists(path.toPath())
        }

        fun deleteIfExists(path: String) {
            deleteIfExists(Paths.get(path))
        }

        fun deleteOnExit(file: File) {
            file.deleteOnExit()
        }

        fun deleteOnExit(path: Path) {
            deleteOnExit(path.toFile())
        }

        fun createDirectory(path: Path) {
            path.toFile().mkdirs()
        }

        fun readStringFromFile(file: File): String {
            return file.bufferedReader(StandardCharsets.UTF_8).use { it.readLine() }
        }

        fun writeStringToFile(file: File, value: String) {
            file.bufferedWriter(StandardCharsets.UTF_8).use { it.write(value) }
        }

        fun <T> writeClassToJsonFile(file: File, value: T) {
            writeStringToFile(file, this.gson.toJson(value))
        }

        fun <T> readClassFromJson(file: File, clazz: Class<T>): T {
            val jsonString = file.reader(StandardCharsets.UTF_8).use { it.readText() }
            return this.gson.fromJson(jsonString, clazz)
        }

        fun zipMultipleFiles(zipFile: File, filesToZip: Set<File>) {
            val fileOutputStream = FileOutputStream(zipFile)
            val zipOutputStream = ZipOutputStream(fileOutputStream)
            val buffer = ByteArray(1024)

            for (file in filesToZip) {
                val inputFile = FileInputStream(file)
                val entry = ZipEntry(file.name)

                zipOutputStream.putNextEntry(entry)

                var length: Int
                while (inputFile.read(buffer).also { length = it } > 0) {
                    zipOutputStream.write(buffer, 0, length)
                }

                inputFile.close()
            }

            zipOutputStream.close()
            fileOutputStream.close()
        }


        fun zipDirectory(startDir: String, zipFile: File) {
            val fileOutputStream = FileOutputStream(zipFile)
            val zipOutputStream = ZipOutputStream(fileOutputStream)

            zipDirectory(startDir, startDir, zipOutputStream)

            zipOutputStream.close()
            fileOutputStream.close()
        }

        private fun zipDirectory(startDir: String, currentDir: String, zipOutputStream: ZipOutputStream) {
            val buffer = ByteArray(1024)
            val dir = File(currentDir)
            val files = dir.listFiles() ?: return

            for (file in files) {
                if (file.isDirectory) {
                    zipDirectory(startDir, file.path, zipOutputStream)
                    continue
                }

                val fileInputStream = FileInputStream(file)
                val zipEntry = ZipEntry(file.path.substring(startDir.length + 1))

                zipOutputStream.putNextEntry(zipEntry)

                var length: Int
                while (fileInputStream.read(buffer).also { length = it } > 0) {
                    zipOutputStream.write(buffer, 0, length)
                }

                fileInputStream.close()
            }
        }

        fun unzipFile(zipFile: File, extractDir: String) {
            val buffer = ByteArray(1024)
            val zipInputStream = ZipInputStream(FileInputStream(zipFile))

            var entry: ZipEntry? = zipInputStream.nextEntry

            while (entry != null) {
                val newFile = File(extractDir, entry.name)

                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile.mkdirs()

                    if (!newFile.exists()) newFile.createNewFile()

                    val fileOutputStream = FileOutputStream(newFile)
                    var length: Int

                    while (zipInputStream.read(buffer).also { length = it } > 0) {
                        fileOutputStream.write(buffer, 0, length)
                    }

                    fileOutputStream.close()
                }

                entry = zipInputStream.nextEntry
            }

            zipInputStream.close()
        }
    }
}