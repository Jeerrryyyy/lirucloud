package de.liruhg.lirucloud.library.database.extension

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.liruhg.lirucloud.library.thread.InternalThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
val threadPool: ThreadPoolExecutor =
    Executors.newFixedThreadPool(16, InternalThreadFactory("lirucloud-db")) as ThreadPoolExecutor