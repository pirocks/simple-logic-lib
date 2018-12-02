package io.github.pirocks.logic

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class FOLSearchingKtTest {

    val var1 = VariableName()
    val var2 = VariableName()
    val var3 = VariableName()
    val var4 = VariableName()
    val var5 = VariableName()
    val rel1 = RelationAtom(Relation({ false }), arrayOf(var1))
    val rel2 = RelationAtom(Relation({ false }), arrayOf(var2))
    val rel3 = RelationAtom(Relation({ false }), arrayOf(var3,var5))
    val expr = And(Or(rel1,False()),IFF(rel2, rel3))

    @Before
    fun setUp() {
    }

    @Test
    fun containsVarTest() {
        assertTrue(containsVar(expr,var1))
        assertTrue(containsVar(expr,var2))
        assertTrue(containsVar(expr,var3))
        assertTrue(containsVar(expr,var5))
        assertFalse(containsVar(expr,var4))
    }

    @Test
    fun containsVarsOtherThanTest() {
        assertTrue(containsVarsOtherThan(expr, arrayOf(var4)))
        assertTrue(containsVarsOtherThan(expr, arrayOf(var1)))
        assertTrue(containsVarsOtherThan(expr, arrayOf(var3,var5)))
        assertTrue(containsVarsOtherThan(expr, arrayOf(var3,var5,var2)))
        assertFalse(containsVarsOtherThan(expr, arrayOf(var1,var2,var3,var5)))
    }
}