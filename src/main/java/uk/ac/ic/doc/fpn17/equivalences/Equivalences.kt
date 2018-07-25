package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import java.lang.IllegalStateException


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, index:Int):FOLFormula
}

class EquivalencePattern(val allowedVars: Array<VariableName>,val allowsEveryVar:Boolean = false) : PredicateAtom(Predicate({throw IllegalStateException("Tried to evaluate an equivalence pattern") }),allowedVars)

//there will be one equivalence for left direction, and one for right
/**
 * Patterns should not include free variables. Use equivalence pattern instead.
 */
abstract class EquivalenceImpl : Equivalence{
    abstract val patternFrom:FOLFormula;
    abstract val patternTo:FOLFormula;

    override fun matches(formula: FOLFormula):Int{
        return matchesImpl(formula,patternFrom);
    }

    private fun matchesImpl(formula: FOLFormula,pattern:FOLFormula): Int {
        var res: Int = 0;
        class MatchSubstitutions{
            val matchedPatterns: MutableMap<EquivalencePattern,FOLFormula> = mutableMapOf()
            // from formula variable names to pattern variable names
            val variableSubstitutions: MutableMap<VariableName,VariableName> = mutableMapOf()
        }

        object : RewritingVisitor(){

            private fun recursivelyCheckMatch(subFormula:FOLFormula,subPattern: FOLFormula,matchSubstitutions: MatchSubstitutions):Boolean{

                if(subFormula.javaClass != subPattern.javaClass && subFormula !is EquivalencePattern){
                    assert(subFormula !is EquivalencePattern)
                    return handlePatternMatch(subPattern, matchSubstitutions, subFormula)
                }
                checkQuantifierVariableAdd(subFormula, subPattern, matchSubstitutions)

                try {
                    for (i in 0 until subFormula.subFormulas.size){
                        if(!recursivelyCheckMatch(subFormula.subFormulas[i], subPattern.subFormulas[i], matchSubstitutions)){
                            return false
                        }
                    }
                    return true
                } finally {
                    checkQuantifierVariableRemove(subFormula, subPattern, matchSubstitutions)
                }
            }

            private fun checkQuantifierVariableRemove(subFormula: FOLFormula, subPattern: FOLFormula, matchSubstitutions: MatchSubstitutions) {
                if (subFormula is Quantifier) {
                    assert(subPattern is Quantifier)
                    if (subPattern is Quantifier) {
                        matchSubstitutions.variableSubstitutions.remove(subFormula.varName)
                    }
                }
            }

            private fun checkQuantifierVariableAdd(subFormula: FOLFormula, subPattern: FOLFormula, matchSubstitutions: MatchSubstitutions) {
                if (subFormula is Quantifier) {
                    assert(subPattern is Quantifier)
                    if (subPattern is Quantifier) {
                        matchSubstitutions.variableSubstitutions[subFormula.varName] = subPattern.varName
                    }
                }
            }

            private fun handlePatternMatch(subPattern: FOLFormula, matchSubstitutions: MatchSubstitutions, subFormula: FOLFormula): Boolean {
                if (subPattern is EquivalencePattern) {
                    if (subPattern.allowsEveryVar) {
                        if (subPattern in matchSubstitutions.matchedPatterns) {
                            //we already found this pattern elsewhere
                            //need to check if same as elsewhere
                            val expectedFormula = matchSubstitutions.matchedPatterns[subPattern]!!
                            val actualFormula = subFormula
                            //todo check that the order of parameters does not need reversing
                            //todo this could still encounter vars from higher up the rewriting visitor
                            //todo need better sameAsImpl
                            return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
                        } else {
                            //todo check variables
                            matchSubstitutions.matchedPatterns[subPattern] = subFormula;
                            return true
                        }
                    } else {
                        TODO()
                    }
                }
                return false
            }

            override fun rewrite(original: FOLFormula): FOLFormula {
                if(original.javaClass == pattern.javaClass){
                    if(recursivelyCheckMatch(original,pattern,MatchSubstitutions())){
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