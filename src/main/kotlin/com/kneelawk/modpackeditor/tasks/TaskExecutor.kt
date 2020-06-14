package com.kneelawk.modpackeditor.tasks

import javafx.concurrent.Task
import java.util.concurrent.Executors

/**
 * A private executor for executing this application's tasks.
 */
private val executor = Executors.newCachedThreadPool { Thread(it).apply { isDaemon = true } }

/**
 * Shuts down the application task executor.
 */
internal fun shutdownThreadPool() {
    executor.shutdown()
}

/**
 * Runs a task in the executor.
 */
fun executeTask(task: Task<*>) {
    executor.execute(task)
}

/**
 * Runs a task in the executor.
 */
fun <T : Task<*>> T.execAsync(): T = apply {
    executeTask(this)
}

/**
 * Runs a task in the current thread.
 */
fun <T : Task<*>> T.execNow(): T = apply {
    run()
}
