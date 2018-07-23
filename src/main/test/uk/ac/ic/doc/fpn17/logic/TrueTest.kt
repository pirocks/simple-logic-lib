package uk.ac.ic.doc.fpn17.logic

import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class TrueTest {

    val `true`: True = True()
    val `false`: False = False()
    lateinit var complex : And

    @Before
    fun setUp() {
        val or = Or(True(),True())
        complex = And(or,or)
    }



    @Test
    fun identity(){
        assertTrue(equalsAndSameAs(`true`,`true`))
    }

    @Test
    fun trueIsNotFalse(){
        assertFalse(`false`.equals(`true`))
        assertFalse(`false`.sameAs(`true`))
    }

    @Test
    fun nestedIdentity(){
        assertTrue(equalsAndSameAs(complex,complex))
    }

    @After
    fun tearDown() {
    }
}