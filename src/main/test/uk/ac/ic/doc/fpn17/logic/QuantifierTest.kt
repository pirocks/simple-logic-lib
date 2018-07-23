package uk.ac.ic.doc.fpn17.logic

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.ac.ic.doc.fpn17.util.UUIDUtil
import java.util.*

class QuantifierTest {

    val singleVarPredicate : Predicate = Predicate( { assert(it.size == 1);false;}, UUIDUtil.generateUUID())
    val doubleVarPredicate : Predicate = Predicate({ assert(it.size == 2);true;}, UUIDUtil.generateUUID())
    lateinit var simpleExpr1: ForAll
    var var1: UUID = UUIDUtil.generateUUID()
    var var2: UUID = UUIDUtil.generateUUID()
    var var3: UUID = UUIDUtil.generateUUID()
    var var4: UUID = UUIDUtil.generateUUID()
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
        val complexExpressionExistsVar = UUIDUtil.generateUUID();
        val complexExpressionForAllVar = UUIDUtil.generateUUID();
        val singleVarPredicateAtomExistsVar = PredicateAtom(singleVarPredicate, arrayOf(complexExpressionExistsVar))
        val singleVarPredicateAtomForAllVar = PredicateAtom(singleVarPredicate, arrayOf(complexExpressionExistsVar))
        val doubleVarPredicateAtom = PredicateAtom(doubleVarPredicate, arrayOf(complexExpressionExistsVar, complexExpressionForAllVar))
        val complexExpressionPartOne = Exists(And(Or(singleVarPredicateAtomForAllVar, True()), Implies(singleVarPredicateAtomForAllVar, doubleVarPredicateAtom)), complexExpressionExistsVar)
        val complexExpressionPartTwo = Or(singleVarPredicateAtomForAllVar, False())
        val complexExpression = ForAll(And(And(complexExpressionPartOne, complexExpressionPartOne), complexExpressionPartTwo), complexExpressionForAllVar)
        multiQuantifier1Var1Var2 = renameVar(renameVar(complexExpression, complexExpressionExistsVar, var1), complexExpressionForAllVar, var2) as ForAll
        multiQuantifier1Var3Var4 = renameVar(renameVar(complexExpression, complexExpressionExistsVar, var3), complexExpressionForAllVar, var4) as ForAll
    }

    private fun setUpSimpleExpr() {
        val simpleExprVar = UUIDUtil.generateUUID()
        val simpleExprPredicateAtom: PredicateAtom = PredicateAtom(singleVarPredicate, arrayOf(simpleExprVar))
        simpleExpr1 = ForAll(Implies(And(simpleExprPredicateAtom, True()), simpleExprPredicateAtom), simpleExprVar)
        simpleExpr1Var1 = renameVar(simpleExpr1, simpleExprVar, var1) as ForAll
        simpleExpr1Var2 = renameVar(simpleExpr1, simpleExprVar, var2) as ForAll
    }

    @Test
    fun simpleExpr1(){
        assertTrue(simpleExpr1.sameAs(simpleExpr1Var2))
        assertTrue(simpleExpr1.sameAs(simpleExpr1Var1))
        assertTrue(simpleExpr1Var2.sameAs(simpleExpr1Var1))
        assertFalse(simpleExpr1.equals(simpleExpr1Var2))
        assertFalse(simpleExpr1.equals(simpleExpr1Var1))
        assertFalse(simpleExpr1Var2.equals(simpleExpr1Var1))
    }

    @Test
    fun multiQuantifier(){
        assertTrue(multiQuantifier1Var3Var4.sameAs(multiQuantifier1Var3Var4))
        assertTrue(multiQuantifier1Var3Var4.sameAs(multiQuantifier1Var1Var2))
        assertTrue(multiQuantifier1Var1Var2.sameAs(multiQuantifier1Var3Var4))
        assertFalse(multiQuantifier1Var3Var4.equals(multiQuantifier1Var3Var4))
        assertFalse(multiQuantifier1Var3Var4.equals(multiQuantifier1Var1Var2))
        assertFalse(multiQuantifier1Var1Var2.equals(multiQuantifier1Var3Var4))
    }

}