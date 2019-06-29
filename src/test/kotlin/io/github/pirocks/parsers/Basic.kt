package io.github.pirocks.parsers

import io.github.pirocks.logic.*
import io.github.pirocks.provers.Prover9
import org.junit.Assert
import org.junit.Test

class PrefixNotationTest{
    val forAllVar = VariableName()
    val existsVar = VariableName()
    val predicate = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(forAllVar, existsVar))
    val baseExpression = Exists(ForAll(And(Or(IFF(predicate, True()), False()), Implies(True(), False())),forAllVar),existsVar)
    @Test
    fun doTest(){
        val str = baseExpression.toPrefixNotation()
        println(str)
        val folParser = PrefixNotationFOLParser()
        val parsed = folParser.parse(str)
        Assert.assertEquals(baseExpression,parsed)
    }

}
