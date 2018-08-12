package uk.ac.ic.doc.fpn17.logic

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuantifierTest {

    val singleVarRelation: Relation = Relation({ assert(it.size == 1);false; })
    val doubleVarRelation: Relation = Relation({ assert(it.size == 2);true; })
    lateinit var simpleExpr1: ForAll
    var var1: VariableName = VariableName()
    var var2: VariableName = VariableName()
    var var3: VariableName = VariableName()
    var var4: VariableName = VariableName()
    lateinit var simpleExpr1Var1: ForAll
    lateinit var simpleExpr1Var2: ForAll
    lateinit var multiQuantifier1Var1Var2: ForAll
    lateinit var multiQuantifier1Var3Var4: ForAll


    @Before
    fun setUp() {
        setUpSimpleExpr()
        setUpMultiQuantifier()
    }

    private fun setUpMultiQuantifier() {
        val complexExpressionExistsVar = VariableName();
        val complexExpressionForAllVar = VariableName();
        val singleVarPredicateAtomExistsVar = RelationAtom(singleVarRelation, arrayOf(complexExpressionExistsVar))
        val singleVarPredicateAtomForAllVar = RelationAtom(singleVarRelation, arrayOf(complexExpressionForAllVar))
        val doubleVarPredicateAtom = RelationAtom(doubleVarRelation, arrayOf(complexExpressionExistsVar, complexExpressionForAllVar))
        val complexExpressionPartOne = Exists(IFF(Or(Negation(singleVarPredicateAtomForAllVar), And(singleVarPredicateAtomExistsVar,Negation(False()))), Implies(singleVarPredicateAtomForAllVar, doubleVarPredicateAtom)), complexExpressionExistsVar)
        val complexExpressionPartTwo = IFF(singleVarPredicateAtomForAllVar, False())
        val complexExpression = ForAll(And(And(complexExpressionPartOne, complexExpressionPartOne), complexExpressionPartTwo), complexExpressionForAllVar)
        multiQuantifier1Var1Var2 = renameVar(renameVar(complexExpression, complexExpressionExistsVar, var1), complexExpressionForAllVar, var2) as ForAll
        multiQuantifier1Var3Var4 = renameVar(renameVar(complexExpression, complexExpressionExistsVar, var3), complexExpressionForAllVar, var4) as ForAll
    }

    private fun setUpSimpleExpr() {
        val simpleExprVar = VariableName()
        val simpleExprRelationAtom: RelationAtom = RelationAtom(singleVarRelation, arrayOf(simpleExprVar))
        simpleExpr1 = ForAll(Implies(And(simpleExprRelationAtom, True()), simpleExprRelationAtom), simpleExprVar)
        simpleExpr1Var1 = renameVar(simpleExpr1, simpleExprVar, var1) as ForAll
        simpleExpr1Var2 = renameVar(simpleExpr1, simpleExprVar, var2) as ForAll
    }

    @Test
    fun simpleExpr1(){
        assertTrue(simpleExpr1.sameAs(simpleExpr1Var2))
        assertTrue(simpleExpr1.sameAs(simpleExpr1Var1))
        assertTrue(simpleExpr1Var2.sameAs(simpleExpr1Var1))
        assertTrue(simpleExpr1.equals(simpleExpr1Var2))
        assertTrue(simpleExpr1.equals(simpleExpr1Var1))
        assertTrue(simpleExpr1Var2.equals(simpleExpr1Var1))
    }

    @Test
    fun multiQuantifier(){
        assertTrue(multiQuantifier1Var3Var4.sameAs(multiQuantifier1Var3Var4))
        assertTrue(multiQuantifier1Var3Var4.sameAs(multiQuantifier1Var1Var2))
        assertTrue(multiQuantifier1Var1Var2.sameAs(multiQuantifier1Var3Var4))
        assertTrue(multiQuantifier1Var3Var4.equals(multiQuantifier1Var1Var2))
        assertTrue(multiQuantifier1Var1Var2.equals(multiQuantifier1Var3Var4))
    }

}