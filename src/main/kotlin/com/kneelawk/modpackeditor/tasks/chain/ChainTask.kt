package com.kneelawk.modpackeditor.tasks.chain

import javafx.concurrent.Task

/**
 * Describes a task that can be chained.
 */
interface ChainTask<O> {
    /**
     * Creates a new map task with this task as its input.
     */
    fun <N> mapImpl(mapper: (O) -> N): Task<N>

    /**
     * Creates a new flat map task with this task as its input.
     */
    fun <N> flatMapImpl(next: (O) -> Task<N>): Task<N>

    /**
     * Creates a new task that runs [next] after this one successfully completes.
     */
    fun <N> andThenImpl(next: Task<N>): Task<N>
}