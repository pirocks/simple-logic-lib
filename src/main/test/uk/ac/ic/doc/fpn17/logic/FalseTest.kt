package uk.ac.ic.doc.fpn17.logic

import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class FalseTest {

    val `true`: True = True()
    val `false`: False = False()
    lateinit var complex : And

    @Before
    fun setUp() {
        val or = Or(False(),False())
        complex = And(or,or)
    }



    @Test
    fun identity(){
        assertTrue(equalsAndSameAs(`false`,`false`))
    }

    @Test
    fun trueIsNotFalse(){
        assertFalse(`true`.equals(`false`))
        assertFalse(`true`.sameAs(`false`))
    }

    @Test
    fun nestedIdentity(){
        assertTrue(equalsAndSameAs(complex,complex))
    }
}