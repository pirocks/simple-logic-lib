package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*


val availableEquivalences = arrayOf(OrAssociativity(), OrAssociativityReverse(), CommutativityOr(), OrIntroductionFalseVariant(), OrIntroductionFalseVariantReverse(), OrIntroductionTrueVariant(), AOrA(), AOrAReverse(), AOrNotA(), AndAssociativity(), AndAssociativityReverse(), CommutativityAnd(), AAndNotA(), AndFalse(), AndTrue(), AndTrueReverse(), AAndA(), AAndAReverse(), CommutativityIFF(), IFFToDoubleImplies(), IFFToDoubleImpliesReverse(), NotIFF(), IFFToDoubleNotIFF(), IFFToDoubleNotIFFReverse(), AImpliesA(), AImpliesAReverse(), TrueImpliesA(), TrueImpliesAReverse(), AImpliesTrue(), FalseImpliesA(), AImpliesFalse(), AImpliesFalseReverse(), ImpliesAsOr(), ImpliesAsOrReverse(), ModusPonens(), ModusPonensReverse(), DoubleNotElimination(), DoubleNotReverse(), NotFalse(), NotFalseReverse(), NotTrue(), NotTrueReverse(), DeMorganLawOr(), DeMorganLawOrReverse(), DeMorganLawAnd(), DeMorganLawAndReverse(), DistributeOrOverAnd(), DistributeOrOverAndReverse(), DistributeAndOverOr(), DistributeAndOverOrReverse())

interface Equivalence {
    fun matches(formula: FOLFormula): Int

    fun apply(formula: FOLFormula, targetIndex: Int): FOLFormula
}

class MatchSubstitutions {
    val matchedPatterns: MutableMap<PatternMatchers, FOLFormula> = mutableMapOf()
    // from formula variable names to pattern variable names
    val variableSubstitutions: MutableMap<VariableName, VariableName> = mutableMapOf()
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
sealed class EquivalenceImpl : Equivalence {
    abstract val patternFrom: FOLPattern;
    abstract val patternTo: FOLPattern;

    override fun matches(formula: FOLFormula): Int {
        return matchesImpl(formula, patternFrom);
    }

    private fun matchesImpl(formula: FOLFormula, pattern: FOLPattern): Int {
        var res: Int = 0;

        val rewritten = object : RewritingVisitor() {

            override fun rewrite(original: FOLFormula): FOLFormula {
                if (pattern.matches(original, MatchSubstitutions())) {
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

        var index: Int = 0;

        val rewritten = object : RewritingVisitor() {
            override fun rewrite(original: FOLFormula): FOLFormula {
                val matchSubstitutions = MatchSubstitutions()
                if (patternFrom.matches(original, matchSubstitutions)) {
                    try {
                        if (index == targetIndex) {
                            return applySubstitutions(patternTo, matchSubstitutions)
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


class ArbritraryEquivalance(override val patternFrom: FOLPattern, override val patternTo: FOLPattern) : EquivalenceImpl()
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

/**
 * -----------------------------BEGIN OR RELATED EQUIVALENCES----------------------
 */
abstract class ReverseEquivalence : EquivalenceImpl() {
    abstract val toReverse: EquivalenceImpl;

    override val patternFrom: FOLPattern
        get() = toReverse.patternTo
    override val patternTo: FOLPattern
        get() = toReverse.patternFrom
}

class OrAssociativity : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Or(a, Or(b, c))
    override val patternTo: FOLPattern
        get() = Or(Or(a, b), c)
}

class OrAssociativityReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = OrAssociativity()
}

class CommutativityOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = Or(a, b)
    override val patternTo: FOLPattern
        get() = Or(b, a)
}

class OrIntroductionFalseVariant : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = a
    override val patternTo: FOLPattern
        get() = Or(a, False())

}

class OrIntroductionFalseVariantReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = OrIntroductionFalseVariant()

}

class OrIntroductionTrueVariant : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = a
    override val patternTo: FOLPattern
        get() = Or(a, True())
}

class AOrA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Or(a, a)
    override val patternTo: FOLPattern
        get() = a
}

class AOrAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AOrA()

}

class AOrNotA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Or(a, Not(a))
    override val patternTo: FOLPattern
        get() = True()
}


/**
 * -----------------------------BEGIN AND RELATED EQUIVALENCES---------------------
 */

class AndAssociativity : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, And(b, c))
    override val patternTo: FOLPattern
        get() = And(And(a, b), c)

}

class AndAssociativityReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AndAssociativity()
}

class CommutativityAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = And(a, b)
    override val patternTo: FOLPattern
        get() = And(b, a)
}

/**
 * NOTE: There is no and contradiction reverse, because this would have infinite number of possible outputs.
 */
class AAndNotA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, Negation(a))
    override val patternTo: FOLPattern
        get() = False()

}

/**
 * NOTE: There is no and false reverse, because this would have infinite number of possible outputs.
 */
class AndFalse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, False())
    override val patternTo: FOLPattern
        get() = False()
}

class AndTrue : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, True())
    override val patternTo: FOLPattern
        get() = a
}

class AndTrueReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AndTrue()
}

class AAndA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, a)
    override val patternTo: FOLPattern
        get() = a
}

class AAndAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AAndA()
}

/**
 * -----------------------------BEGIN IFF RELATED EQUIVALENCES---------------------
 */

class CommutativityIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = IFF(a, b)
    override val patternTo: FOLPattern
        get() = IFF(b, a)
}

class IFFToDoubleImplies : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern
        get() = IFF(a, b)
    override val patternTo: FOLPattern
        get() = And(Implies(a, b), Implies(b, a))
}

class IFFToDoubleImpliesReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = IFFToDoubleImplies()

}

class NotIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Not(IFF(a, b))
    override val patternTo: FOLPattern
        get() = IFF(Not(a), b)
}

class IFFToDoubleNotIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = IFF(a, b)
    override val patternTo: FOLPattern
        get() = IFF(Not(a), Not(b))
}

class IFFToDoubleNotIFFReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = IFFToDoubleNotIFF()
}


/**
 * -----------------------------BEGIN IMPLIES RELATED EQUIVALENCES-----------------
 */

class AImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(a, a)
    override val patternTo: FOLPattern
        get() = True()
}

class AImpliesAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AImpliesA()

}

class TrueImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(True(), a)
    override val patternTo: FOLPattern
        get() = a
}

class TrueImpliesAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = TrueImpliesA()
}

class AImpliesTrue : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(a, True())
    override val patternTo: FOLPattern
        get() = True()
}

class FalseImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(False(), a)
    override val patternTo: FOLPattern
        get() = True()
}

class AImpliesFalse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(a, False())
    override val patternTo: FOLPattern
        get() = Not(a)
}

class AImpliesFalseReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = AImpliesFalse()
}

class ImpliesAsOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Implies(a, b)
    override val patternTo: FOLPattern
        get() = Or(Not(a), b)
}

class ImpliesAsOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = ImpliesAsOr()

}

class ModusPonens : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, Implies(a, b))
    override val patternTo: FOLPattern
        get() = And(a, b)
}

class ModusPonensReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = ModusPonens()
}


/**
 * -----------------------------BEGIN NOT RELATED EQUIVALENCES---------------------
 */

class DoubleNotElimination : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Not(Not(a))
    override val patternTo: FOLPattern
        get() = a
}

class DoubleNotReverse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Not(Not(a))
    override val patternTo: FOLPattern
        get() = a
}

class NotFalse : EquivalenceImpl() {
    override val patternFrom: FOLPattern
        get() = Not(False())
    override val patternTo: FOLPattern
        get() = True()
}

class NotFalseReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = NotFalse()
}


class NotTrue : EquivalenceImpl() {
    override val patternFrom: FOLPattern
        get() = Not(True())
    override val patternTo: FOLPattern
        get() = False()
}

class NotTrueReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = NotTrue()

}

/**
 * -----------------------------BEGIN DE MORGAN LAWS ------------------------------
 */

class DeMorganLawOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Not(Or(a, b))
    override val patternTo: FOLPattern
        get() = And(Not(a), Not(b))
}

class DeMorganLawOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = DeMorganLawOr()
}

class DeMorganLawAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Not(And(a, b))
    override val patternTo: FOLPattern
        get() = Or(Not(a), Not(b))
}

class DeMorganLawAndReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = DeMorganLawOr()
}

/**
 * -----------------------------DISTRIBUTIVITY ------------------------------------
 */

class DistributeOrOverAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = And(a, Or(b, c))
    override val patternTo: FOLPattern
        get() = Or(And(a, b), And(a, c))
}

class DistributeOrOverAndReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = DistributeOrOverAnd()
}

class DistributeAndOverOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern
        get() = Or(a, And(b, c))
    override val patternTo: FOLPattern
        get() = And(Or(a, b), Or(a, c))
}

class DistributeAndOverOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl
        get() = DistributeAndOverOr()
}