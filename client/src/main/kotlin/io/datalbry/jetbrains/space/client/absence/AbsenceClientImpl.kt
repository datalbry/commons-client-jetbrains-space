package io.datalbry.jetbrains.space.client.absence

import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.models.absence.Absence
import io.datalbry.jetbrains.space.models.absence.AbsenceIdentifier
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.absences
import space.jetbrains.api.runtime.types.AbsenceRecord

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
        return PaginationIterator(
            { getNextBatch(it) },
            { AbsenceIdentifier(it.id) }
        )
    }

    private fun getNextBatch(batchInfo: BatchInfo): Batch<AbsenceRecord> {
        return runBlocking {
            space.absences.getAllAbsences(batchInfo = batchInfo) {
                id()
            }
        }
    }
}