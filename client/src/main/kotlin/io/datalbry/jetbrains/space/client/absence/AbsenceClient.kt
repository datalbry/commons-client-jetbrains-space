package io.datalbry.jetbrains.space.client.absence

import io.datalbry.jetbrains.space.models.absence.Absence
import io.datalbry.jetbrains.space.models.absence.AbsenceIdentifier

interface AbsenceClient {

    fun getAbsence(identifier: AbsenceIdentifier): Absence

    fun getAbsenceIdentifier(): Iterator<AbsenceIdentifier>
}