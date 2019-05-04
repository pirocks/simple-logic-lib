package io.github.pirocks.provers

import io.github.pirocks.logic.And
import io.github.pirocks.logic.Predicate
import io.github.pirocks.logic.PredicateAtom
import io.github.pirocks.util.UUIDUtil
import org.junit.Test

class Prover9Test{

    val a = PredicateAtom.newSimpleAtom()
    val b = PredicateAtom.newSimpleAtom()

    val expectedRes = And(a,b)

    @Test
    fun doTest(){
        val prover = Prover9()
        prover.prove(listOf(a,b),expectedRes)
    }
}