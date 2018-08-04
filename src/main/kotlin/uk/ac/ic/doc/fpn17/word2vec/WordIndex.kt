package uk.ac.ic.doc.fpn17.word2vec

import net.openhft.chronicle.map.ChronicleMap
import net.openhft.chronicle.map.ChronicleMapBuilder
import uk.ac.ic.doc.fpn17.logic.*
import java.io.File
import kotlin.streams.toList


public val wordIndex : ChronicleMap<FOLFormula, String> = ChronicleMapBuilder.of(FOLFormula::class.java,String::class.java).name("All_Formulas_in_Space_1").entries(128*1024*16).averageKey(Or(Or(Or(False(),True()),True()), And(False(), True()))).averageValue("Word12345689").createOrRecoverPersistedTo(File(/*System.getProperty("java.io.tmpdir") + */"wordIndex.bin"))
public val wordReverseIndex : ChronicleMap<String,FOLFormula> = ChronicleMapBuilder.of(String::class.java,FOLFormula::class.java).name("All_Formulas_in_Space_1_Reverse").entries(128*1024*16).averageValue(Or(Or(Or(False(),True()),True()), And(False(), True()))).averageKey("Word12345689").createOrRecoverPersistedTo(File(/*System.getProperty("java.io.tmpdir") + */"wordIndexReverse.bin"))

@Synchronized fun getNextWord():  String{
    val res = "word" + wordIndex.size.toString().chars().boxed().map { (it.toInt() - '0'.toInt() + 'a'.toInt()).toChar().toString() }.reduce { s1: String?, s2: String? -> s1 + s2 }.get()
    return res;
}