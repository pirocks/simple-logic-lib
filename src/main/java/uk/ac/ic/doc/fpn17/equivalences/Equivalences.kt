package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.EqualityContext
import uk.ac.ic.doc.fpn17.logic.FOLFormula
import uk.ac.ic.doc.fpn17.logic.PredicateAtom
import java.util.*


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, index:Int):FOLFormula
}

//there will be one equivalence for left direction, and one for right
abstract class EquivalenceImpl : Equivalence{
    class MatchContext{
        val currentMatches : MutableMap<UUID,FOLFormula> = mutableMapOf();
        val completedMatches : MutableList<Map<UUID,FOLFormula>> = mutableListOf();
        val subFormulasToCheckLater : Stack<FOLFormula> = Stack()
        val subFormulasChecked : Set<FOLFormula> = hashSetOf()//in theory formulas could get repeatedly added to subformulas to check, this will prevent double checking
    }
    abstract val patternFrom:FOLFormula;
    abstract val patternTo:FOLFormula;

    private fun matchesImpl(subFormula: FOLFormula,subPattern:FOLFormula, matchContext: MatchContext): Boolean {
        //regardless of whether or not we match, check this subformula as well.
        matchContext.subFormulasToCheckLater.push(subFormula)
        if(subPattern is PredicateAtom){
            val predicateUUID = subPattern.predicate.uuid;
            if(predicateUUID in matchContext.currentMatches){
                val expectedValue = matchContext.currentMatches[predicateUUID]!!
                if(expectedValue != subFormula){
                    //no match
                    return false;
                }

                //match, should recurse back up and check.
                return true;
            }
        }
        if(subFormula.javaClass != subPattern.javaClass){
            //special behavior for quantifiers needed
            //recurse into subformulas with original pattern
        }else{
            //special behavior for quantifiers needed
            //match
            //recurse check

        }
        TODO()
    }
}

/*
equivalences to implement:
and:
(a & b,b & a)
(a & a,a)
(a & True,a)
(a & False,a)
(a & ~a,False)
(a & (b & c),(a & b) & c)
or:
(a | b, b | a)
iff:

 */