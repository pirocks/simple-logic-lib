package uk.ac.ic.doc.fpn17.equivalences

import org.junit.Test
import uk.ac.ic.doc.fpn17.logic.*

class SimpleSwapForAllTest{
    val v1 = VariableName()
    val v2 = VariableName()
    val predicateAtom = PredicateAtom(Predicate({false}), arrayOf(v1,v2))
    val c = And(Or(True(), False()),predicateAtom)
    val expr = ForAll(ForAll(c,v1),v2)
    val expected = ForAll(ForAll(c,v2),v1)

    @Test
    fun doTest() {
        val res = SwapForAll().apply(expr,0)
        assert(res == expected)
    }
}

class SimpleSwapExistTest{
    val v1 = VariableName()
    val v2 = VariableName()
    val predicateAtom = PredicateAtom(Predicate({false}), arrayOf(v1,v2))
    val c = predicateAtom
    val expr = Exists(Exists(c,v1),v2)
    val expected = Exists(Exists(c,v2),v1)

    @Test
    fun doTest() {
        val res = SwapExists().apply(expr,0)
        assert(res == expected)
    }
}


class SimplePushNegationTest{
    val v1 = VariableName()
    val v2 = VariableName()
    val predicateAtom = PredicateAtom(Predicate({false}), arrayOf(v1,v2))
    val c = predicateAtom
    val expr = Not(ForAll(Exists(c,v1),v2))
    val expected = Exists(ForAll(Not(c),v1),v2)

    @Test
    fun doTest() {
        val res = PushNegationThroughExist().apply(PushNegationThroughForAll().apply(expr,0),0)
        assert(res == expected)
        val original = PushNegationThroughForAllReverse().apply(PushNegationThroughExistReverse().apply(expected,0),0)
        assert(original == expr)
    }
}

class SimpleDistributeForAllOverAnd{
    val v1 = VariableName()
    val right = And(True(), False())
    val left = PredicateAtom(Predicate({ false }), arrayOf(v1))
    val c = And(left, right)
    val expr = ForAll(c,v1)
    val expected = And(ForAll(left,v1),ForAll(right,v1))

    @Test
    fun doTest() {
        val res = DistributeForAllOverAnd().apply(expr,0)
        assert(res == expected)
        assert(expr == DistributeForAllOverAndReverse().apply(res,0))
    }
}

class SimpleDistributeExistsOverOr{
    val v1 = VariableName()
    val right = Or(True(), False())
    val left = PredicateAtom(Predicate({ false }), arrayOf(v1))
    val c = Or(left, right)
    val expr = Exists(c,v1)
    val expected = Or(Exists(left,v1),Exists(right,v1))

    @Test
    fun doTest() {
        val res = DistributeExistOverOr().apply(expr,0)
        assert(res == expected)
        assert(expr == DistributeExistOverOrReverse().apply(res,0))
    }
}