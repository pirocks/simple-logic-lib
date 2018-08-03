package uk.ac.ic.doc.fpn17.word2vec

import uk.ac.ic.doc.fpn17.equivalences.*
import uk.ac.ic.doc.fpn17.logic.FOLFormula
import uk.ac.ic.doc.fpn17.logic.False
import uk.ac.ic.doc.fpn17.logic.Formula
import java.util.*
import kotlin.streams.toList

/**
 * copy pasta https://stackoverflow.com/questions/47850156/get-a-random-item-from-list-using-kotlin-streams
 * Returns a random element using the specified [random] instance as the source of randomness.
 */
fun <E> List<E>.random(random: java.util.Random): E? = if (size > 0) get(random.nextInt(size)) else null

val equivalencesAvailable: List<Equivalence> = arrayOf(OrAssociativityReverse(), OrAssociativity(), OrIntroductionFalseVariant(), OrIntroductionTrueVariant1(), AndAssociativityReverse(), AndAssociativity(), AndContradiction(), AndFalse1(), AndFalse2(),ReverseAndTrue1(), ReverseAndTrue2(), ReverseAAndA(), AndTrue1(), AndTrue2(), AAndA(), CommutativityAnd(), CommutativityOr()).asList()

fun doWalks(start: FOLFormula = False(), numWalks: Int = (1024 * 1024), lengthWalks: Int = 16): Array<Array<String>> {
    return (0 until numWalks).toList().parallelStream().map { _ ->
        doWalk(start, lengthWalks)
    }.toList().toTypedArray()
}

fun doWalk(start: FOLFormula, lengthWalks: Int = 16, random: Random = Random()): Array<String> {
    fun selectMatchingEquivalence(formula: FOLFormula): Equivalence{
        val candidate = equivalencesAvailable.random(random)!!
        if(candidate.matches(formula) > 0){
            return candidate;
        }
        return selectMatchingEquivalence(formula);
    }
    fun selectMatchToOperateOn(formula: FOLFormula,equivalence: Equivalence): Int{
        val matches = equivalence.matches(formula);
        return random.ints(0,matches).findFirst().asInt;
    }
    fun nextFormula(formula: FOLFormula):FOLFormula{
        val equivalence = selectMatchingEquivalence(formula)
        return equivalence.apply(formula,selectMatchToOperateOn(formula,equivalence))
    }
    fun formulaToWord(formula: FOLFormula): String{
        if(wordIndex.contains(formula))
            return wordIndex[formula]!!
        else{
            val word = getNextWord()
            wordIndex[formula] = word
            return word;
        }
    }
    var current = start;
    val res = (0 until lengthWalks).map {
        val prev = current
        current = nextFormula(current);
        formulaToWord(prev)
    }.toTypedArray()
    return res;
}

fun main(args: Array<String>) {
    doWalks()
}