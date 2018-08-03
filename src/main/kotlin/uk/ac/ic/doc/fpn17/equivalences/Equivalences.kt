package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import java.lang.IllegalStateException


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, targetIndex:Int):FOLFormula
}

class MatchSubstitutions{
    val matchedPatterns: MutableMap<PatternMatchers,FOLFormula> = mutableMapOf()
    // from formula variable names to pattern variable names
    val variableSubstitutions: MutableMap<VariableName,VariableName> = mutableMapOf()
}

//class EquivalencePattern(val allowedVars: Array<VariableName>,val allowsEveryVar:Boolean = false) : PredicateAtom(Predicate({throw IllegalStateException("Tried to evaluate an equivalence pattern") }),allowedVars){
//    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
//        val actualFormula = formula
//        if (allowsEveryVar) {
//            if (this in matchSubstitutions.matchedPatterns) {
//                //we already found this pattern elsewhere
//                //need to check if same as elsewhere
//                val expectedFormula = matchSubstitutions.matchedPatterns[this]!!
//                //todo check that the order of parameters does not need reversing
//                //todo this could still encounter vars from higher up the rewriting visitor
//                return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
//            } else {
//                //todo check variables
//                matchSubstitutions.matchedPatterns[this] = formula;
//                return true
//            }
//        } else {
//            if(this in matchSubstitutions.matchedPatterns){
//                val expectedFormula = matchSubstitutions.matchedPatterns[this]!!
//                return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
//            }else{
//                if(containsVarsOtherThan(formula,allowedVars.map {
//                            matchSubstitutions.variableSubstitutions[it]!!
//                        }.toTypedArray()))
//                    return false
//                matchSubstitutions.matchedPatterns[this] = formula;
//                return true
//            }
//        }
//    }
//}



//there will be one equivalence for left direction, and one for right
/**
 * Patterns should not include free variables. Use equivalence pattern instead.
 */
sealed class EquivalenceImpl : Equivalence{
    abstract val patternFrom:FOLPattern;
    abstract val patternTo:FOLPattern;

    override fun matches(formula: FOLFormula):Int{
        return matchesImpl(formula,patternFrom);
    }

    private fun matchesImpl(formula: FOLFormula,pattern:FOLPattern): Int {
        var res: Int = 0;

        val rewritten = object : RewritingVisitor(){

            override fun rewrite(original: FOLFormula): FOLFormula {
                if(pattern.matches(original,MatchSubstitutions())){
                    res++;
                }
                return super.rewrite(original)
            }
        }.rewrite(formula)
        assert(rewritten.sameAs(formula))
        return res
    }

    override fun apply(formula: FOLFormula, targetIndex: Int): FOLFormula {

        fun applySubstitutions(patternTo: FOLPattern, matchSubstitutions: MatchSubstitutions): FOLFormula {
            return object : RewritingVisitor() {
                override fun rewriteAllowAllVars(original: AllowAllVars): FOLFormula {
                    return matchSubstitutions.matchedPatterns[original]!!
                }
            }.rewrite(patternTo as FOLFormula)
        }
        var index : Int= 0;

        val rewritten = object : RewritingVisitor(){
            override fun rewrite(original: FOLFormula): FOLFormula {
                val matchSubstitutions = MatchSubstitutions()
                if(patternFrom.matches(original, matchSubstitutions)){
                    try {
                        if(index == targetIndex){
                            return applySubstitutions(patternTo,matchSubstitutions)
                        }
                    } finally {
                        index++;
                    }
                }

                return super.rewrite(original)
            }
        }.rewrite(formula)
        return rewritten

    }
}


class ArbritraryEquivalance(override val patternFrom: FOLPattern, override val patternTo: FOLPattern) : EquivalenceImpl() {
    override fun apply(formula: FOLFormula, targetIndex: Int): FOLFormula {
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

class OrAssociativityReverse : EquivalenceImpl(){
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternTo: FOLPattern
        get() = Or(a,Or(b,c))
    override val patternFrom: FOLPattern
        get() = Or(Or(a,b),c)

}

class OrAssociativity : EquivalenceImpl(){
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Or(a,Or(b,c))
    override val patternTo: FOLPattern
        get() = Or(Or(a,b),c)

}

class OrIntroductionFalseVariant : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = a
    override val patternTo: FOLPattern
        get() = Or(a,False())

}

class OrIntroductionTrueVariant1 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = a
    override val patternTo: FOLPattern
        get() = Or(a,True())

}


/**
 * todo clean up reverse duplication
 */
class AndAssociativityReverse : EquivalenceImpl(){
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternTo: FOLPattern
        get() = And(a,And(b,c))
    override val patternFrom: FOLPattern
        get() = And(And(a,b),c)

}

class AndAssociativity : EquivalenceImpl(){
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a,And(b,c))
    override val patternTo: FOLPattern
        get() = And(And(a,b),c)

}

class AndContradiction : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a,Negation(a))
    override val patternTo: FOLPattern
        get() = False()

}

class AndFalse1 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a,False())
    override val patternTo: FOLPattern
        get() = False()
}

class AndFalse2 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(False(),a)
    override val patternTo: FOLPattern
        get() = False()

}


class ReverseAndTrue1 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternTo: FOLPattern
        get() = And(a,True())
    override val patternFrom: FOLPattern
        get() = a
}

class ReverseAndTrue2 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternTo: FOLPattern
        get() = And(True(),a)
    override val patternFrom: FOLPattern
        get() = a

}

class ReverseAAndA : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternTo: FOLPattern
        get() = And(a,a)
    override val patternFrom: FOLPattern
        get() = a
}

/**
 * todo clean up duplication with and a.
 */
class AndTrue1 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a,True())
    override val patternTo: FOLPattern
        get() = a
}

class AndTrue2 : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(True(),a)
    override val patternTo: FOLPattern
        get() = a

}

class AAndA : EquivalenceImpl(){
    val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a,a)
    override val patternTo: FOLPattern
        get() = a
}

class CommutativityAnd : EquivalenceImpl() {
    val a = AllowAllVars()
    val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = And(a,b)
    override val patternTo: FOLPattern
        get() = And(b,a)
}

class CommutativityOr : EquivalenceImpl() {
    val a = AllowAllVars()
    val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = Or(a,b)
    override val patternTo: FOLPattern
        get() = Or(b,a)
}


class CommutativityIFF : EquivalenceImpl() {
    val a = AllowAllVars()
    val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = IFF(a,b)
    override val patternTo: FOLPattern
        get() = IFF(b,a)
}