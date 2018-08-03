package uk.ac.ic.doc.fpn17.word2vec

import net.openhft.chronicle.map.ChronicleMap
import net.openhft.chronicle.map.ChronicleMapBuilder
import uk.ac.ic.doc.fpn17.logic.FOLFormula
import java.io.File


public val wordIndex : ChronicleMap<FOLFormula, String> = ChronicleMapBuilder.of(FOLFormula::class.java,String::class.java).name("All_Formulas_in_Space_1").entries(1_000_000).create()

fun getNextWord():  String{
    return "Word" + wordIndex.size;
}