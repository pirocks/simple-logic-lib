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
        toProve = True() and (False() or (False() implies True())) iff True()
        basicNDProof = proof(emptySet(), toProve) {
            val trueIntro = trueIntro()
            val orIntroLeft = orIntro(False(), implies(assume(False())) {
                trueIntro()
            })
            val lhs = andIntro(trueIntro, orIntroLeft)
            val impliesOne = implies(assume(True())) {
                idIntro(lhs)
            }
            val impliesTwo = implies(assume(toProve)) {
                trueIntro()
            }
            iFFIntro(impliesOne, impliesTwo)
        }
    }

    @Test
    fun doTest() {
        Assert.assertTrue(basicNDProof.verify())
    }
}

class LEMProof {
    val p = RelationAtom(Relation({ TODO() }), arrayOf())
    val toProve: FOLFormula = p or not(p)
    val proof = proof(emptySet(), toProve) {
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

