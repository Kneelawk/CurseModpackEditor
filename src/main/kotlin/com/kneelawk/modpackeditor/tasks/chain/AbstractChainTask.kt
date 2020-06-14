package com.kneelawk.modpackeditor.tasks.chain

import javafx.concurrent.Task
import tornadofx.onChange

/**
 * Holds common implementations for [ChainTask] methods as well as some chain task utility functions.
 */
abstract class AbstractChainTask<O>(protected val state: TaskChainState) : Task<O>(), ChainTask<O> {
    protected fun registerListeners(task: Task<*>, offset: Double, length: Double) {
        task.progressProperty().onChange {
            if (it < 0) {
                updateProgress(-1, -1)
            } else {
                updateProgress(offset + it * length, 1.0)
            }
        }
        task.messageProperty().onChange {
            updateMessage(it)
        }
    }

    override fun <N> mapImpl(mapper: (O) -> N): Task<N> {
        state.lock.lock()
        return try {
            MapTask(state, this, mapper)
        } finally {
            state.lock.unlock()
        }
    }

    override fun <N> flatMapImpl(next: (O) -> Task<N>): Task<N> {
        state.lock.lock()
        return try {
            state.taskCount++
            FlatMapTask(state, this, next)
        } finally {
            state.lock.unlock()
        }
    }

    override fun <N> andThenImpl(next: Task<N>): Task<N> {
        state.lock.lock()
        return try {
            state.taskCount++
            FlatMapTask(state, this) { next }
        } finally {
            state.lock.unlock()
        }
    }
}