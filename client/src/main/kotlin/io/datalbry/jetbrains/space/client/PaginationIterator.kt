package io.datalbry.jetbrains.space.client

import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo

class PaginationIterator<T, S>(
    private val fetchNextBatch: (BatchInfo) -> Batch<T>,
    private val transformer: (T) -> S
) : Iterator<S> {
    private var batchInfo = BatchInfo("0", 100)
    private var currentBatch: Batch<T> = fetchNextBatch(batchInfo)
    private var currentBatchElements = currentBatch.data.map(transformer).iterator()

    private fun loadNextBatch() {
        batchInfo = BatchInfo(currentBatch.next, 100)
        val currentBatchTmp = fetchNextBatch(batchInfo)
        if (currentBatchTmp.next != "") {
            currentBatch = currentBatchTmp
            currentBatchElements = currentBatch.data.map(transformer).iterator()
        }
    }

    override fun hasNext(): Boolean {
        if (!currentBatchElements.hasNext()) {
            loadNextBatch()
        }
        return currentBatchElements.hasNext()
    }

    override fun next(): S {
        if (!currentBatchElements.hasNext()) {
            loadNextBatch()
        }

        return currentBatchElements.next()
    }
}