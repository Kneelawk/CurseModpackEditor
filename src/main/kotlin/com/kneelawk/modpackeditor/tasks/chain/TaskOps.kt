package com.kneelawk.modpackeditor.tasks.chain

import javafx.concurrent.Task

/**
 * Creates a new task that maps this task's output to another type.
 *
 * Note: The resulting task must be started manually (e.g. via [execAsync]).
 */
fun <I, O> Task<I>.map(mapper: (I) -> O): Task<O> {
    return if (this is ChainTask<*>) mapImpl {
        @Suppress("UNCHECKED_CAST")
        mapper(it as I)
    }
    else MapTask(TaskChainState(taskCount = 1), this, mapper)
}

/**
 * Chains this task with another task, using this task's output as the input for the next task's producer, and
 * automatically starting the next task when this one completes successfully.
 *
 * Note: The resulting task must be started manually (e.g. via [execAsync]).
 */
fun <I, O> Task<I>.flatMap(next: (I) -> Task<O>): Task<O> {
    return if (this is ChainTask<*>) flatMapImpl {
        @Suppress("UNCHECKED_CAST")
        next(it as I)
    }
    else FlatMapTask(TaskChainState(taskCount = 2), this,
        next)
}

/**
 * Chains this task with another task and automatically starts the next task once this one completes successfully.
 *
 * Note: The resulting task must be started manually (e.g. via [execAsync]).
 */
fun <O> Task<*>.andThen(next: Task<O>): Task<O> {
    return if (this is ChainTask<*>) andThenImpl(next)
    else FlatMapTask(TaskChainState(taskCount = 2),
        this) { next }
}
