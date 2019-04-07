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
        basicNDProof = proof(emptySet(), toProve) {
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
    val p = RelationAtom.newSimpleAtom()
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

