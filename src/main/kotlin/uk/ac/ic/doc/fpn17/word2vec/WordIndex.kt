package uk.ac.ic.doc.fpn17.word2vec

import net.openhft.chronicle.map.ChronicleMap
import net.openhft.chronicle.map.ChronicleMapBuilder
import uk.ac.ic.doc.fpn17.logic.*
import java.io.File


public val wordIndex : ChronicleMap<FOLFormula, String> = ChronicleMapBuilder.of(FOLFormula::class.java,String::class.java).name("All_Formulas_in_Space_1").entries(16_000_000).averageKey(Or(Or(Or(False(),True()),True()), And(False(), True()))).averageValue("Word12345689").create()//createOrRecoverPersistedTo(File(System.getProperty("java.io.tmpdir") + "/wordIndex.bin"))

fun getNextWord():  String{
    return "Word" + wordIndex.size;
}