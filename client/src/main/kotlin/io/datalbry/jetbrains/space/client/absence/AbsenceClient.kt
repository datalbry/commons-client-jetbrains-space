package io.datalbry.jetbrains.space.client.absence

import io.datalbry.jetbrains.space.models.Absence
import io.datalbry.jetbrains.space.models.AbsenceIdentifier

interface AbsenceClient {

    fun getAbsence(identifier: AbsenceIdentifier): Absence

    fun getAbsenceIdentifier(): Iterator<AbsenceIdentifier>
}