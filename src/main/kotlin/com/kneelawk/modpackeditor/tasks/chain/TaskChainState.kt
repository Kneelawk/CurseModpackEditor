package com.kneelawk.modpackeditor.tasks.chain

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * State of an entire chain of composite tasks.
 */
data class TaskChainState(val lock: Lock = ReentrantLock(), var taskCount: Long)