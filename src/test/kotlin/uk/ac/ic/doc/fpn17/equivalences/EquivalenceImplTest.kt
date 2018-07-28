package uk.ac.ic.doc.fpn17.equivalences

import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.ac.ic.doc.fpn17.logic.*
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
    lateinit var predicate1:PredicateAtom
    lateinit var predicate2:PredicateAtom
    lateinit var equivalance1: ArbritraryEquivalance
    lateinit var equivalance2: ArbritraryEquivalance
    lateinit var equivalance3: ArbritraryEquivalance
    lateinit var equivalance4: ArbritraryEquivalance


    @Before
    fun setUp() {
        predicate1 = PredicateAtom(Predicate({false}), arrayOf())
        predicate2 = PredicateAtom(Predicate({false}), arrayOf())
        formula1 = And(Or(And(True(),predicate1),False()),Or(And(True(),predicate1),True()))
        formula2 = Implies(And(False(),predicate2),And(True(),Or(predicate2,False())))
        formula3 = Implies(formula1,formula2)
        pattern1 = And(True(),EquivalencePattern(arrayOf(),true))
        pattern2 = Or(And(True(),EquivalencePattern(arrayOf(),true)),EquivalencePattern(arrayOf(),true))
        pattern3 = Or(EquivalencePattern(arrayOf(),true),False())
        pattern4 = Implies(EquivalencePattern(arrayOf(),true),EquivalencePattern(arrayOf(),true))
        equivalance1 = ArbritraryEquivalance(pattern1, False())
        equivalance2 = ArbritraryEquivalance(pattern2, False())
        equivalance3 = ArbritraryEquivalance(pattern3, False())
        equivalance4 = ArbritraryEquivalance(pattern4, False())
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
    val formula1: FOLFormula = And(True(),True())
    val formula2: FOLFormula = And(And(True(),True()),And(True(),True()))
    val formula3 : FOLFormula = And(And(True(),True()),And(False(),True()))
    val formula4: FOLFormula = And(True(),False())
    lateinit var pattern1 : FOLPattern
    lateinit var arbritraryEquivalance1: ArbritraryEquivalance
    @Before
    fun setUp(){
        val patternAtom = EquivalencePattern(arrayOf(),true)
        pattern1 = And(patternAtom,patternAtom)
        arbritraryEquivalance1 = ArbritraryEquivalance(pattern1,pattern1);
    }

    @Test
    fun testFormula1() {
        assertEquals(1,arbritraryEquivalance1.matches(formula1));

    }

    @Test
    fun testFormula2() {
        assertEquals(3,arbritraryEquivalance1.matches(formula2))

    }

    @Test
    fun testFormula3() {
        assertEquals(1,arbritraryEquivalance1.matches(formula3))

    }

    @Test
    fun testFormula4() {
        assertEquals(0,arbritraryEquivalance1.matches(formula4))
    }
}

//todo first order test