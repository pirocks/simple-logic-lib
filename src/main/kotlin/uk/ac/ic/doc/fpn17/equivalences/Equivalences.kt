package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*


val availableEquivalences = arrayOf(OrAssociativity(), OrAssociativityReverse(), CommutativityOr(), OrIntroductionFalseVariant(), OrIntroductionFalseVariantReverse(), OrIntroductionTrueVariant(), AOrA(), AOrAReverse(), AOrNotA(), AndAssociativity(), AndAssociativityReverse(), CommutativityAnd(), AAndNotA(), AndFalse(), AndTrue(), AndTrueReverse(), AAndA(), AAndAReverse(), CommutativityIFF(), IFFToDoubleImplies(), IFFToDoubleImpliesReverse(), NotIFF(), IFFToDoubleNotIFF(), IFFToDoubleNotIFFReverse(), AImpliesA(), TrueImpliesA(), TrueImpliesAReverse(), AImpliesTrue(), FalseImpliesA(), AImpliesFalse(), AImpliesFalseReverse(), ImpliesAsOr(), ImpliesAsOrReverse(), ModusPonens(), ModusPonensReverse(), DoubleNotElimination(), DoubleNotReverse(), NotFalse(), NotFalseReverse(), NotTrue(), NotTrueReverse(), DeMorganLawOr(), DeMorganLawOrReverse(), DeMorganLawAnd(), DeMorganLawAndReverse(), DistributeOrOverAnd(), DistributeOrOverAndReverse(), DistributeAndOverOr(), DistributeAndOverOrReverse())

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
                //todo rewrite not all vars needs to be supported
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


class ArbitraryEquivalence(override val patternFrom: FOLPattern, override val patternTo: FOLPattern) : EquivalenceImpl()
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

    override val patternFrom: FOLPattern get() = toReverse.patternTo
    override val patternTo: FOLPattern get() = toReverse.patternFrom
}

class OrAssociativity : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, Or(b, c))
    override val patternTo: FOLPattern = Or(Or(a, b), c)
}

class OrAssociativityReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = OrAssociativity()
}

class CommutativityOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = Or(a, b)
    override val patternTo: FOLPattern = Or(b, a)
}

class OrIntroductionFalseVariant : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = a
    override val patternTo: FOLPattern = Or(a, False())

}

class OrIntroductionFalseVariantReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = OrIntroductionFalseVariant()

}

class OrIntroductionTrueVariant : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = a
    override val patternTo: FOLPattern = Or(a, True())
}

class AOrA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, a)
    override val patternTo: FOLPattern = a
}

class AOrAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = AOrA()

}

class AOrNotA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, Not(a))
    override val patternTo: FOLPattern = True()
}


/**
 * -----------------------------BEGIN AND RELATED EQUIVALENCES---------------------
 */

class AndAssociativity : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, And(b, c))
    override val patternTo: FOLPattern = And(And(a, b), c)

}

class AndAssociativityReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = AndAssociativity()
}

class CommutativityAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = And(a, b)
    override val patternTo: FOLPattern = And(b, a)
}

/**
 * NOTE: There is no and contradiction reverse, because this would have infinite number of possible outputs.
 */
class AAndNotA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Negation(a))
    override val patternTo: FOLPattern = False()

}

/**
 * NOTE: There is no and false reverse, because this would have infinite number of possible outputs.
 */
class AndFalse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, False())
    override val patternTo: FOLPattern = False()
}

class AndTrue : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, True())
    override val patternTo: FOLPattern = a
}

class AndTrueReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = AndTrue()
}

class AAndA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, a)
    override val patternTo: FOLPattern = a
}

class AAndAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = AAndA()
}

/**
 * -----------------------------BEGIN IFF RELATED EQUIVALENCES---------------------
 */

class CommutativityIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = IFF(b, a)
}

class IFFToDoubleImplies : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = And(Implies(a, b), Implies(b, a))
}

class IFFToDoubleImpliesReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = IFFToDoubleImplies()

}

class NotIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(IFF(a, b))
    override val patternTo: FOLPattern = IFF(Not(a), b)
}

class IFFToDoubleNotIFF : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = IFF(Not(a), Not(b))
}

class IFFToDoubleNotIFFReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = IFFToDoubleNotIFF()
}


/**
 * -----------------------------BEGIN IMPLIES RELATED EQUIVALENCES-----------------
 */

class AImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, a)
    override val patternTo: FOLPattern = True()
}

class TrueImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(True(), a)
    override val patternTo: FOLPattern = a
}

class TrueImpliesAReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = TrueImpliesA()
}

class AImpliesTrue : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, True())
    override val patternTo: FOLPattern = True()
}

class FalseImpliesA : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(False(), a)
    override val patternTo: FOLPattern = True()
}

class AImpliesFalse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, False())
    override val patternTo: FOLPattern = Not(a)
}

class AImpliesFalseReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = AImpliesFalse()
}

class ImpliesAsOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, b)
    override val patternTo: FOLPattern = Or(Not(a), b)
}

class ImpliesAsOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = ImpliesAsOr()

}

class ModusPonens : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Implies(a, b))
    override val patternTo: FOLPattern = And(a, b)
}

class ModusPonensReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = ModusPonens()
}


/**
 * -----------------------------BEGIN NOT RELATED EQUIVALENCES---------------------
 */

class DoubleNotElimination : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Not(a))
    override val patternTo: FOLPattern = a
}

class DoubleNotReverse : EquivalenceImpl() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Not(a))
    override val patternTo: FOLPattern = a
}

class NotFalse : EquivalenceImpl() {
    override val patternFrom: FOLPattern = Not(False())
    override val patternTo: FOLPattern = True()
}

class NotFalseReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = NotFalse()
}


class NotTrue : EquivalenceImpl() {
    override val patternFrom: FOLPattern = Not(True())
    override val patternTo: FOLPattern = False()
}

class NotTrueReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = NotTrue()

}

/**
 * -----------------------------BEGIN DE MORGAN LAWS ------------------------------
 */

class DeMorganLawOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Or(a, b))
    override val patternTo: FOLPattern = And(Not(a), Not(b))
}

class DeMorganLawOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = DeMorganLawOr()
}

class DeMorganLawAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(And(a, b))
    override val patternTo: FOLPattern = Or(Not(a), Not(b))
}

class DeMorganLawAndReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = DeMorganLawOr()
}

/**
 * -----------------------------DISTRIBUTIVITY ------------------------------------
 */

class DistributeOrOverAnd : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Or(b, c))
    override val patternTo: FOLPattern = Or(And(a, b), And(a, c))
}

class DistributeOrOverAndReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = DistributeOrOverAnd()
}

class DistributeAndOverOr : EquivalenceImpl() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, And(b, c))
    override val patternTo: FOLPattern = And(Or(a, b), Or(a, c))
}

class DistributeAndOverOrReverse : ReverseEquivalence() {
    override val toReverse: EquivalenceImpl = DistributeAndOverOr()
}