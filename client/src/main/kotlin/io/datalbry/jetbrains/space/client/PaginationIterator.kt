package io.datalbry.jetbrains.space.client

import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo

/**
 * PaginationIterator allows to simply iterate over the batches returned by the JetBrains Space client with the default
 * batch size of 100.
 *
 * @param fetchNextBatch: A function which takes a BatchInfo object and produces a new batch.
 * @param transformer: A function which transforms the objects from the batch.
 */
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