package io.github.pirocks.util

import java.util.*
import java.util.logging.Logger

object UUIDUtil {
    val logger = Logger.getLogger(UUIDUtil.javaClass.name)

    val usedUUIDS: MutableSet<UUID> = mutableSetOf()//todo don't forget to serialize
    //    var uuidCount = 0;
    fun generateUUID(): UUID {
        val randomUUID = UUID.randomUUID()
        if (randomUUID in usedUUIDS) {
            logger.warning("A uuid collision would have occurred if a set of previous uuids hadn't been created")
            return generateUUID()
        }
        usedUUIDS.add(randomUUID)
        return randomUUID
    }

    fun toPrettyString(uuid: UUID): String = "VariableNumber" + Math.abs((uuid.mostSignificantBits + uuid.leastSignificantBits).toInt()).toShort().toString(16).replace('-','0')

    fun toMathML2(uuid: UUID): String = "<mi>" + toPrettyString(uuid) + "</mi>"

}