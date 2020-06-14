package com.kneelawk.modpackeditor.tasks.chain

import com.kneelawk.modpackeditor.tasks.execAsync
import javafx.concurrent.Task
import java.util.concurrent.CancellationException
import java.util.concurrent.locks.ReentrantLock

/**
 * A task designed for running a series of smaller tasks.
 */
class FlatMapTask<I, O>(state: TaskChainState, private val initial: Task<I>, private val next: (I) -> Task<O>) :
        AbstractChainTask<O>(state) {
    private val secondLength = (1.0 / state.taskCount.toDouble())
    private val firstLength = 1.0 - secondLength
    private var currentTask: Task<*> = initial
    private val taskLock = ReentrantLock()

    init {
        registerListeners(initial, 0.0, firstLength)
    }

    override fun call(): O {
        initial.execAsync()

        val result = initial.get()

        taskLock.lock()
        val nextTask = try {
            if (isCancelled) throw CancellationException()

            val nextTask = next(result)
            registerListeners(nextTask, firstLength, secondLength)
            currentTask = nextTask
            nextTask.execAsync()

            nextTask
        } finally {
            taskLock.unlock()
        }

        return nextTask.get()
    }

    override fun cancelled() {
        taskLock.lock()
        try {
            currentTask.cancel()
        } finally {
            taskLock.unlock()
        }
    }
}
