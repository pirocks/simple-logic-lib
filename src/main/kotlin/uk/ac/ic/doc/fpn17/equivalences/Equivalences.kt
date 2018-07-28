package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import java.lang.IllegalStateException


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, index:Int):FOLFormula
}

class MatchSubstitutions{
    val matchedPatterns: MutableMap<EquivalencePattern,FOLFormula> = mutableMapOf()
    // from formula variable names to pattern variable names
    val variableSubstitutions: MutableMap<VariableName,VariableName> = mutableMapOf()
}

class EquivalencePattern(val allowedVars: Array<VariableName>,val allowsEveryVar:Boolean = false) : PredicateAtom(Predicate({throw IllegalStateException("Tried to evaluate an equivalence pattern") }),allowedVars){
    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        val actualFormula = formula
        if (allowsEveryVar) {
            if (this in matchSubstitutions.matchedPatterns) {
                //we already found this pattern elsewhere
                //need to check if same as elsewhere
                val expectedFormula = matchSubstitutions.matchedPatterns[this]!!
                //todo check that the order of parameters does not need reversing
                //todo this could still encounter vars from higher up the rewriting visitor
                return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
            } else {
                //todo check variables
                matchSubstitutions.matchedPatterns[this] = formula;
                return true
            }
        } else {
            if(this in matchSubstitutions.matchedPatterns){
                val expectedFormula = matchSubstitutions.matchedPatterns[this]!!
                return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
            }else{
                if(containsVarsOtherThan(formula,allowedVars.map { matchSubstitutions.variableSubstitutions[it]!! }.toTypedArray()))
                    return false
                matchSubstitutions.matchedPatterns[this] = formula;
                return true
            }
        }
    }


}

//there will be one equivalence for left direction, and one for right
/**
 * Patterns should not include free variables. Use equivalence pattern instead.
 */
abstract class EquivalenceImpl : Equivalence{
    abstract val patternFrom:FOLPattern;
    abstract val patternTo:FOLPattern;

    override fun matches(formula: FOLFormula):Int{
        return matchesImpl(formula,patternFrom);
    }

    private fun matchesImpl(formula: FOLFormula,pattern:FOLPattern): Int {
        var res: Int = 0;

        object : RewritingVisitor(){

            override fun rewrite(original: FOLFormula): FOLFormula {
                if(original.javaClass == pattern.javaClass){
                    if(pattern.matches(original,MatchSubstitutions())){
                        res++;
                    }
                }
                return super.rewrite(original)
            }
        }.rewrite(formula)
        return res
    }
}


class ArbritraryEquivalance(override val patternFrom: FOLFormula, override val patternTo: FOLFormula) : EquivalenceImpl() {
    override fun apply(formula: FOLFormula, index: Int): FOLFormula {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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