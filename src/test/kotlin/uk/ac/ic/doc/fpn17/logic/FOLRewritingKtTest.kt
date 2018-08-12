package uk.ac.ic.doc.fpn17.logic

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class FOLRewritingKtTest {

    val folVar = VariableName()
    val existsVar = VariableName()
    val folVarExpected = VariableName()
    val existsVarExpected = VariableName()
    lateinit var testExpression:ForAll;
    lateinit var expected:ForAll;

    @Before
    fun setUp() {
        val alwaysFalse = Relation({ false })
        val zeroParameterPredicate = RelationAtom(alwaysFalse, arrayOf())
        val twoParameterPredicateTest = RelationAtom(alwaysFalse, arrayOf(folVar, existsVar))
        val twoParameterPredicateExpected = RelationAtom(alwaysFalse, arrayOf(folVarExpected, existsVarExpected))
        testExpression = ForAll(Exists(And(zeroParameterPredicate,twoParameterPredicateTest),existsVar),folVar)
        expected = ForAll(Exists(And(zeroParameterPredicate,twoParameterPredicateExpected),existsVarExpected),folVarExpected)
    }

    @Test
    fun renameVar() {
        assertTrue(renameVar(renameVar(testExpression,folVar,folVarExpected),existsVar,existsVarExpected) == expected)
        assertTrue(expected == renameVar(renameVar(testExpression,folVar,folVarExpected),existsVar,existsVarExpected))
        assertTrue(expected == renameVar(renameVar(testExpression,existsVar,existsVarExpected),folVar,folVarExpected))
        assertTrue(renameVar(renameVar(testExpression,existsVar,existsVarExpected),folVar,folVarExpected) == expected)
    }
}