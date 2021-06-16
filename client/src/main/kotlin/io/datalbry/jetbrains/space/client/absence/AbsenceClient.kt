package io.datalbry.jetbrains.space.client.absence

import io.datalbry.jetbrains.space.models.absence.Absence
import io.datalbry.jetbrains.space.models.absence.AbsenceIdentifier

/**
 * The AbsenceClient interface provides all needed endpoints to retrieve data Absence Data from JetBrains Space.
 */
interface AbsenceClient {

    /**
     * Given an identifier retrieves an Absence
     *
     * @param identifier: An absence identifier
     * @return an Absence object
     */
    fun getAbsence(identifier: AbsenceIdentifier): Absence

    /**
     * Retrives all Absence identifier from the JetBrains Space
     *
     * @return All Absence identifiers in the given JetBrains Space in form of an iterator
     */
    fun getAbsenceIdentifier(): Iterator<AbsenceIdentifier>
}