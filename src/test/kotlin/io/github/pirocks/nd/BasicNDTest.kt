package io.github.pirocks.nd

import io.github.pirocks.logic.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BasicPropNDTest {
    lateinit var toProve: FOLFormula
    lateinit var basicNDProof: NDProof
    @Before
    fun setUp() {
        val subToProof = True() and (False() or (False() implies True()))
        toProve = subToProof iff True()
        basicNDProof = proof(toProve) {
            val trueIntro = trueIntro()
            val orIntroLeft = orIntro(False(), implies(assume(False())) {
                trueIntro()
            })
            val lhs = andIntro(trueIntro, orIntroLeft)
            val impliesOne = implies(assume(True())) {
                idIntro(lhs)
            }
            assert(lhs.proves == subToProof)
            val impliesTwo = implies(assume(subToProof)) {
                trueIntro()
            }
            iFFIntro(impliesTwo, impliesOne)
        }
    }

    @Test
    fun doTest() {
        Assert.assertTrue(basicNDProof.verify())
    }
}

class LEMProof {
    val p = PredicateAtom.newSimpleAtom()
    val toProve: FOLFormula = p or not(p)
    val proof = proof(toProve) {
        val notNotToProve = negationIntro(assume(not(toProve))) { notToProve ->
            val notP = negationIntro(assume(p)) { assumption ->
                falseIntro(orIntro(assumption, not(p)), notToProve)
            }
            falseIntro(orIntro(p, notP), notToProve)
        }
        doubleNegElim(notNotToProve)
    }

    @Test
    fun doTest() {
        val res = proof.verify()
        Assert.assertTrue(res)
    }
}

class TestWithLotsOfElim {
    val a = PredicateAtom.newSimpleAtom()
    val b = PredicateAtom.newSimpleAtom()
    val c = PredicateAtom.newSimpleAtom()
    val andBlock = a and (((True() iff b) iff True()) and c)
    val given = True() implies (andBlock or False())
    val toProof = b
    val proof = io.github.pirocks.nd.proof(toProof, setOf(given)) {
        val givenStatement = given(given)
        val truth = trueIntro()
        val orExpr = impliesElim(truth, givenStatement)
        val right = implies(assume(False())) {
            falsityElim(it, toProof)
        }
        val left = implies(assume(andBlock)) {
            iffElimRight(truth, iffElimLeft(truth, andElimLeft(andElimRight(it))))
        }
        orElim(orExpr, left, right)
    }

    @Test
    fun doTest() {
        Assert.assertTrue(proof.verify())
    }
}

class ForAllOverAnd {
    val forAllVar = VariableName()
    val A = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(forAllVar))
    val B = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(forAllVar))
    val given = ForAll(A and B, forAllVar)
    val toProof = ForAll(A, forAllVar) and ForAll(B, forAllVar)
    val proof = io.github.pirocks.nd.proof(toProof, setOf(given), verify = false) {
        val givenStat = given(given)
        val leftForAll = forAllIntro { newVar ->
            val aAndb = forAllElim(givenStat, newVar)
            andElimLeft(aAndb)
        }
        val rightForAll = forAllIntro { newVar ->
            val aAndb = forAllElim(givenStat, newVar)
            andElimRight(aAndb)
        }
        andIntro(leftForAll, rightForAll)
    }

    @Test
    fun doTest() {
        Assert.assertTrue(proof.verify())
    }
}

class ExistsOverAnd {
    val existsVar = VariableName()
    val A = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(existsVar))
    val B = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(existsVar))
    val given = Exists(A and B, existsVar)
    val toProof = Exists(A, existsVar) and Exists(B, existsVar)
    val proof = io.github.pirocks.nd.proof(toProof, setOf(given), verify = false) {
        val givenStat = given(given)
        val left = existsElim(givenStat) { skolemConst: VariableName, skolemConstExpr: AssumptionStatement ->
            existsIntro(andElimLeft(skolemConstExpr), skolemConst)
        }
        val right = existsElim(givenStat) { skolemConst: VariableName, skolemConstExpr: AssumptionStatement ->
            existsIntro(andElimRight(skolemConstExpr), skolemConst)
        }
        andIntro(left, right)
    }

    @Test
    fun doTest() {
        Assert.assertTrue(proof.verify())
    }
}