package io.github.pirocks.equivalences

import io.github.pirocks.logic.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PropositionalEquivalenceImplTest {
    // create new interface hierarchy which implements pattern, and implements matches as interface member
    lateinit var pattern1 : FOLPattern
    lateinit var pattern2 : FOLPattern
    lateinit var pattern3 : FOLPattern
    lateinit var pattern4 : FOLPattern
    lateinit var formula1 : FOLFormula
    lateinit var formula2 : FOLFormula
    lateinit var formula3 : FOLFormula
    lateinit var predicate1: PredicateAtom
    lateinit var predicate2: PredicateAtom
    lateinit var equivalance1: ArbitraryPatternBasedRewriter
    lateinit var equivalance2: ArbitraryPatternBasedRewriter
    lateinit var equivalance3: ArbitraryPatternBasedRewriter
    lateinit var equivalance4: ArbitraryPatternBasedRewriter


    @Before
    fun setUp() {
        predicate1 = PredicateAtom.newSimpleAtom()
        predicate2 = PredicateAtom.newSimpleAtom()
        formula1 = And(Or(And(True(), predicate1), False()), Or(And(True(), predicate1), True()))
        formula2 = Implies(And(False(), predicate2), And(True(), Or(predicate2, False())))
        formula3 = Implies(formula1, formula2)
        pattern1 = And(True(), AllowAllVars())
        pattern2 = Or(And(True(), AllowAllVars()), AllowAllVars())
        pattern3 = Or(AllowAllVars(), False())
        pattern4 = Implies(AllowAllVars(), AllowAllVars())
        equivalance1 = ArbitraryPatternBasedRewriter(pattern1, False())
        equivalance2 = ArbitraryPatternBasedRewriter(pattern2, False())
        equivalance3 = ArbitraryPatternBasedRewriter(pattern3, False())
        equivalance4 = ArbitraryPatternBasedRewriter(pattern4, False())
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testFormula1() {
        assertEquals(2,equivalance1.matches(formula1));
        assertEquals(2,equivalance2.matches(formula1));
        assertEquals(1,equivalance3.matches(formula1));
        assertEquals(0,equivalance4.matches(formula1));
    }

    @Test
    fun testFormula2() {
        assertEquals(1,equivalance1.matches(formula2))
        assertEquals(0,equivalance2.matches(formula2))
        assertEquals(1,equivalance3.matches(formula2))
        assertEquals(1,equivalance4.matches(formula2))
    }

    @Test
    fun testFormula3() {
        assertEquals(3,equivalance1.matches(formula3))
        assertEquals(2,equivalance2.matches(formula3))
        assertEquals(2,equivalance3.matches(formula3))
        assertEquals(2,equivalance4.matches(formula3))
    }
}

class MultipleSamePatternsTest{
    val formula1: FOLFormula = And(True(), True())
    val formula2: FOLFormula = And(And(True(), True()), And(True(), True()))
    val formula3 : FOLFormula = And(And(True(), True()), And(False(), True()))
    val formula4: FOLFormula = And(True(), False())
    lateinit var pattern1 : FOLPattern
    lateinit var arbitraryEquivalence1: ArbitraryPatternBasedRewriter
    @Before
    fun setUp(){
        val patternAtom = AllowAllVars()
        pattern1 = And(patternAtom, patternAtom)
        arbitraryEquivalence1 = ArbitraryPatternBasedRewriter(pattern1, pattern1);
    }

    @Test
    fun testFormula1() {
        assertEquals(1,arbitraryEquivalence1.matches(formula1));

    }

    @Test
    fun testFormula2() {
        assertEquals(3,arbitraryEquivalence1.matches(formula2))

    }

    @Test
    fun testFormula3() {
        assertEquals(1,arbitraryEquivalence1.matches(formula3))

    }

    @Test
    fun testFormula4() {
        assertEquals(0,arbitraryEquivalence1.matches(formula4))
    }
}

class FOLEquivalenceTest{
    val alwaysFalsePredicate = Predicate.newUnEvaluatableRelation()
    lateinit var formula1: FOLFormula;
    lateinit var formula2: FOLFormula;
    lateinit var formula3: FOLFormula;
    lateinit var formula4: FOLFormula;
    lateinit var arbitraryEquivalence1: ArbitraryPatternBasedRewriter
    lateinit var arbitraryEquivalence2: ArbitraryPatternBasedRewriter

    @Before
    fun setUp() {
        setUpFormula1()
        setUpFormula2()
        setUpFormula3()
        setUpFormula4()
        setUpPattern1()
//        setUpPattern2()

    }

//    private fun setUpPattern2() {
//        //search for forall and exists one after the other, with both vars potentially used
//        val forAllVar = VariableName()
//        val existsVar = VariableName()
//        val potentiallyBothVars = EquivalencePattern(arrayOf(forAllVar, existsVar))
//        val pattern = ForAll(Exists(potentiallyBothVars, existsVar), forAllVar)
//        arbitraryEquivalence2 = ArbitraryPatternBasedRewriter(pattern, pattern)
//    }

    private fun setUpPattern1() {
        val multiUseEquivalencePattern = AllowAllVars()
        val pattern1 = ForAll(multiUseEquivalencePattern)
        arbitraryEquivalence1 = ArbitraryPatternBasedRewriter(pattern1, pattern1)
    }

    private fun setUpFormula4() {
        val forAllVar1 = VariableName()

        val predicateAtom2 = PredicateAtom(alwaysFalsePredicate, arrayOf(forAllVar1))
        formula4 = And(ForAll(Exists(predicateAtom2), forAllVar1), False())
    }

    private fun setUpFormula3() {
        formula3 = ForAll(And(formula1, formula2))
    }

    private fun setUpFormula2() {
        val forAllVar1 = VariableName()
        val forAllVar2 = VariableName()
        val existsVar3 = VariableName()

        val predicateAtom1 = PredicateAtom(alwaysFalsePredicate, arrayOf(existsVar3, forAllVar2))
        val predicateAtom2 = PredicateAtom(alwaysFalsePredicate, arrayOf(forAllVar1))
        formula2 = And(ForAll(Exists(predicateAtom1, existsVar3), forAllVar2), ForAll(predicateAtom2, forAllVar1))
    }

    private fun setUpFormula1() {
        formula1 = ForAll(Exists(True()))
    }

    @Test
    fun testFormula1() {
        assertEquals(1,arbitraryEquivalence1.matches(formula1))
//        assertEquals(1,arbitraryEquivalence2.matches(formula1))
    }

    @Test
    fun testFormula2() {
        assertEquals(2,arbitraryEquivalence1.matches(formula2))
//        assertEquals(1,arbitraryEquivalence2.matches(formula2))
    }

    @Test
    fun testFormula3() {
        assertEquals(4,arbitraryEquivalence1.matches(formula3))
//        assertEquals(2,arbitraryEquivalence2.matches(formula3))
    }

    @Test
    fun testFormula4() {
        assertEquals(1,arbitraryEquivalence1.matches(formula4))
//        assertEquals(1,arbitraryEquivalence2.matches(formula4))
    }
}
