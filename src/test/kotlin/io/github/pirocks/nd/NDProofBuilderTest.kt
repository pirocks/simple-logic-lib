package io.github.pirocks.nd

import io.github.pirocks.logic.And
import io.github.pirocks.logic.True
import org.junit.Assert
import org.junit.Test
//
//class NDProofBuilderTest{
//    @Test
//    fun testConstructionBehavior(){
//        val builder = NDProofBuilder()
//        val given = GivenBuilder(And(True(), True()))
//        builder.addStatement(0,given)
//        builder.addStatement(1,AndElimLeftBuilder(0))
//        val ndProof = builder.build()
//        Assert.assertTrue(ndProof.verifyImpl())
//        Assert.assertEquals(True(),ndProof.proves)
//    }
//}