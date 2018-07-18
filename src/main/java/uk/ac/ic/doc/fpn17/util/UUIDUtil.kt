package uk.ac.ic.doc.fpn17.util

import java.util.*

object UUIDUtil {
    val usedUUIDS: MutableSet<UUID> = mutableSetOf()//todo don't forget to serialize
    //    var uuidCount = 0;
    fun generateUUID(): UUID {
        val randomUUID = UUID.randomUUID()
        if (randomUUID in usedUUIDS) {
            return generateUUID()
        }
        usedUUIDS.add(randomUUID)
        return randomUUID
    }
}