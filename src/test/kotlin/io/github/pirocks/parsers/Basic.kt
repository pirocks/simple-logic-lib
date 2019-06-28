package io.github.pirocks.parsers

import io.github.pirocks.logic.*
import org.junit.Test

class BasicTest{
    val forAllVar = VariableName()
    val existsVar = VariableName()
    val predicate = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(forAllVar, existsVar))
    val baseExpression = Exists(ForAll(And(Or(IFF(predicate, True()), False()), Implies(True(), False())),forAllVar),existsVar)
    @Test
    fun doTest(){
        val str = baseExpression.toPrefixNotation()

    }

}
