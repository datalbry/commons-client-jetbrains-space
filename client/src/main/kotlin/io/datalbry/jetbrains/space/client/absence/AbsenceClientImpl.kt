package io.datalbry.jetbrains.space.client.absence

import io.datalbry.jetbrains.space.models.Absence
import io.datalbry.jetbrains.space.models.AbsenceIdentifier
import io.datalbry.jetbrains.space.models.ProfileIdentifier
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.absences

class AbsenceClientImpl(private val space: SpaceHttpClientWithCallContext) : AbsenceClient {

    override fun getAbsence(identifier: AbsenceIdentifier): Absence {
        val absenceRecord = runBlocking {
            space.absences.getAbsence(id = identifier.id)!!
        }
        return with(absenceRecord) {
            Absence(
                id = id,
                approved = approval?.approved,
                approvedAt = approval?.approvedAt?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime(),
                approvedBy = if (approval?.approvedBy?.id != null) ProfileIdentifier(approval?.approvedBy?.id!!) else null,
                archived = archived,
                available = available,
                description = description,
                icon = icon,
                member = ProfileIdentifier(member.id),
                since = since.toJavaLocalDate().atStartOfDay(),
                till = till.toJavaLocalDate().atStartOfDay()
            )
        }
    }

    override fun getAbsenceIdentifier(): Iterator<AbsenceIdentifier> {
        return object : Iterator<AbsenceIdentifier> {

            private var batchInfo = BatchInfo("0", 100)
            private var currentBatch = runBlocking {
                space.absences.getAllAbsences(batchInfo=batchInfo) {
                    id()
                }
            }
            private var currentBatchElements = currentBatch.data.map { AbsenceIdentifier(it.id) }.iterator()

            private fun loadNextBatch() {
                val currentBatchTmp = runBlocking {
                    space.absences.getAllAbsences(batchInfo = batchInfo) {
                        id()
                    }
                }

                if (currentBatchTmp.next != "") {
                    currentBatch = currentBatchTmp
                    currentBatchElements = currentBatch.data.map { AbsenceIdentifier(it.id) }.iterator()
                    batchInfo = BatchInfo(currentBatch.next, 100)
                }
            }

            override fun hasNext(): Boolean {
                if (!currentBatchElements.hasNext()) {
                    loadNextBatch()
                }
                return currentBatchElements.hasNext()
            }

            override fun next(): AbsenceIdentifier {
                if (!currentBatchElements.hasNext()) {
                    loadNextBatch()
                }

                return currentBatchElements.next()
            }
        }
    }
}