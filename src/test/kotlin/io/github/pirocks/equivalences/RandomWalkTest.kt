package io.github.pirocks.equivalences

import io.github.pirocks.logic.*
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * copy pasta https://stackoverflow.com/questions/47850156/get-a-random-item-from-list-using-kotlin-streams
 * Returns a random element using the specified [random] instance as the source of randomness.
 */
private fun <E> List<E>.random(random: java.util.Random): E? = if (size > 0) get(random.nextInt(size)) else null


private fun doWalk(start: FOLFormula, lengthWalk: Int = 2048, random: Random = Random()): Array<FOLFormula> {
    fun selectMatchingEquivalence(formula: FOLFormula): PatternBasedRewriter {
        val candidate = availablePropositionalEquivalences.toList().random(random)!!
        if (candidate.matches(formula) > 0) {
            return candidate;
        }
        return selectMatchingEquivalence(formula);
    }

    fun selectMatchToOperateOn(formula: FOLFormula, patternBasedRewriter: PatternBasedRewriter): Int {
        val matches = patternBasedRewriter.matches(formula);
        return random.ints(0, matches).findFirst().asInt;
    }

    fun nextFormula(formula: FOLFormula): FOLFormula {
        val equivalence = selectMatchingEquivalence(formula)
        return equivalence.apply(formula, selectMatchToOperateOn(formula, equivalence))
    }

    var current = start;
    val res = (0 until lengthWalk).map {
        current = nextFormula(current);
        current
    }.toTypedArray()
    return res;
}

class RandomWalkTest {
    lateinit var falseWalks: Array<FOLFormula>
    lateinit var trueWalks: Array<FOLFormula>

    @Before
    fun setUp() {
        val seed = System.currentTimeMillis()
        val random = Random()
        random.setSeed(seed)
        falseWalks = doWalk(False(), random = random)
        trueWalks = doWalk(True(), random = random)
        println("""Seed is ${seed}""")
    }

    @Test
    fun doTest() {
        var prev: FOLFormula = False()//for debugging purposes
        falseWalks.iterator().forEach {
            val ev = EvalContext(Signature(emptySet(), emptySet()), mutableMapOf())
            assert(!it.evaluate(ev))
            prev = it
        }
        prev = True()
        trueWalks.iterator().forEach {
            val ev = EvalContext(Signature(emptySet(), emptySet()), mutableMapOf())
            assert(it.evaluate(ev))
            prev = it
        }
    }
}