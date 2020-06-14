package com.kneelawk.modpackeditor.tasks.chain

import com.kneelawk.modpackeditor.tasks.execNow
import javafx.concurrent.Task

/**
 * A Task designed for mapping the output of another task.
 */
class MapTask<I, O>(state: TaskChainState, private val initial: Task<I>, private val mapper: (I) -> O) :
        AbstractChainTask<O>(state) {
    init {
        registerListeners(initial, 0.0, 1.0)
    }

    override fun call(): O {
        initial.execNow()

        return mapper(initial.get())
    }

    override fun cancelled() {
        initial.cancel()
    }
}
