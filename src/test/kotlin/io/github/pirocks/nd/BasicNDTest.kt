package io.github.pirocks.nd

import io.github.pirocks.logic.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BasicPropNDTest {
    lateinit var toProve: FOLFormula
    lateinit var ndProof: NDProof
    @Before
    fun setUp() {
        toProve = True() and (False() or (False() implies True())) iff True()
        ndProof = proof(emptySet(), toProve) {
            val trueIntro = trueIntro()
            val orIntroLeft = orIntroLeft(False(), implies(assume(False())) {
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
        Assert.assertTrue(ndProof.verify())
    }
}