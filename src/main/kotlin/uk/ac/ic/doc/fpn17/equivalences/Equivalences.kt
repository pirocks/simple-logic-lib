package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import kotlin.concurrent.fixedRateTimer


val availablePropositionalEquivalences = arrayOf(
        OrAssociativityReverse(), CommutativityOr(),
        OrIntroductionFalseVariant(), OrIntroductionFalseVariantReverse(),
        AOrA(), AOrAReverse(),
        AOrNotA(), AndAssociativity(),
        AndAssociativityReverse(), CommutativityAnd(),
        AAndNotA(), AndFalse(),
        AndTrue(), AndTrueReverse(),
        AAndA(), AAndAReverse(),
        CommutativityIFF(), IFFToDoubleImplies(),
        IFFToDoubleImpliesReverse(), NotIFF(),
        IFFToDoubleNotIFF(), IFFToDoubleNotIFFReverse(),
        AImpliesA(), TrueImpliesA(),
        TrueImpliesAReverse(), AImpliesTrue(),
        FalseImpliesA(), AImpliesFalse(),
        AImpliesFalseReverse(), ImpliesAsOr(),
        ImpliesAsOrReverse(), ModusPonens(),
        ModusPonensReverse(), DoubleNotElimination(),
        DoubleNotReverse(), NotFalse(),
        NotFalseReverse(), NotTrue(),
        NotTrueReverse(), DeMorganLawOr(),
        DeMorganLawOrReverse(), DeMorganLawAnd(),
        DeMorganLawAndReverse(), DistributeOrOverAnd(),
        DistributeOrOverAndReverse(), DistributeAndOverOr(),
        DistributeAndOverOrReverse(), OrAssociativity())
val availableFirstOrderEquivalences = arrayOf(
        SwapForAll(), SwapExists(),
        PushNegationThroughForAll(), PushNegationThroughForAllReverse(),
        PushNegationThroughExist(), PushNegationThroughExistReverse(),
        DistributeForAllOverAnd(), DistributeForAllOverAndReverse(),
        DistributeExistOverOr(), DistributeExistOverOrReverse(),
        ExistAToA(), ExistToAReverse(),
        ForAllAToA(), ForAllAToAReverse(),
        ExistOverAnd(), ExistOverAndReverse(),
        ForAllOverOr(), ForAllOverOrReverse(),
        ForAllOverImplies(), ForAllOverImpliesReverse(),
        ExistsOverImplies(), ExistsOverImpliesReverse()
)
val availableEquivalences = availableFirstOrderEquivalences + availablePropositionalEquivalences

interface PatternBasedRewriter {
    fun matches(formula: FOLFormula): Int

    fun apply(formula: FOLFormula,
              targetIndex: Int): FOLFormula
}

class MatchSubstitutions {
    val matchedPatterns: MutableMap<PatternMember, FOLFormula> = mutableMapOf()
    // from formula variable names to pattern variable names
    val variableSubstitutions: MutableMap<VariableName, VariableName> = mutableMapOf()
}

/**
 * Patterns should not include free variables
 */
sealed class Equivalence : PatternBasedRewriter {
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

        /**
         * function applies pattern member rewrite rules.
         */
        fun applySubstitutions(patternTo: FOLPattern, matchSubstitutions: MatchSubstitutions): FOLFormula {
            return object : RewritingVisitor() {
                override fun rewritePatternMember(original: PatternMember): FOLFormula {
                    var substitution = matchSubstitutions.matchedPatterns[original]!!
                    //need to change to new variable names for substitution
                    //also need to correctrly handle case when swapping variable names, which requires temporary var names
                    val tempToFinal = mutableMapOf<VariableName, VariableName>()
                    matchSubstitutions.variableSubstitutions.forEach {
                        val originalVarName = it.value//todo this is confusing. change
                        val finalVarName = it.key
                        val tempVarName = VariableName()
                        substitution = renameVar(substitution, originalVarName, tempVarName)
                        tempToFinal[tempVarName] = finalVarName
                    }
                    tempToFinal.forEach {
                        val temp = it.key
                        val final = it.value
                        substitution = renameVar(substitution, temp, final)
                    }
                    return substitution
                }
            }.rewrite(patternTo as FOLFormula)
        }

        var index: Int = 0;
        var patternFound = false;
        val rewritten = object : RewritingVisitor() {
            override fun rewrite(original: FOLFormula): FOLFormula {
                val matchSubstitutions = MatchSubstitutions()
                if (patternFrom.matches(original, matchSubstitutions)) {
                    try {
                        if (index == targetIndex) {
                            patternFound = true
                            return applySubstitutions(patternTo, matchSubstitutions)
                        }
                    } finally {
                        index++;
                    }
                }

                return super.rewrite(original)
            }
        }.rewrite(formula)
        assert(patternFound)
        return renameAllVars(rewritten)

    }
}


class ArbitraryPatternBasedRewriter(override val patternFrom: FOLPattern, override val patternTo: FOLPattern) : Equivalence()
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
abstract class ReverseEquivalence : Equivalence() {
    abstract val toReverse: Equivalence;

    override val patternFrom: FOLPattern get() = toReverse.patternTo
    override val patternTo: FOLPattern get() = toReverse.patternFrom
}

class OrAssociativity : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, Or(b, c))
    override val patternTo: FOLPattern = Or(Or(a, b), c)
}

class OrAssociativityReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = OrAssociativity()
}

class CommutativityOr : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = Or(a, b)
    override val patternTo: FOLPattern = Or(b, a)
}

class OrIntroductionFalseVariant : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = a
    override val patternTo: FOLPattern = Or(a, False())

}

class OrIntroductionFalseVariantReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = OrIntroductionFalseVariant()

}


class AOrA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, a)
    override val patternTo: FOLPattern = a
}

class AOrAReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = AOrA()

}

class AOrNotA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, Not(a))
    override val patternTo: FOLPattern = True()
}


/**
 * -----------------------------BEGIN AND RELATED EQUIVALENCES---------------------
 */

class AndAssociativity : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, And(b, c))
    override val patternTo: FOLPattern = And(And(a, b), c)

}

class AndAssociativityReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = AndAssociativity()
}

class CommutativityAnd : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = And(a, b)
    override val patternTo: FOLPattern = And(b, a)
}

/**
 * NOTE: There is no and contradiction reverse, because this would have infinite number of possible outputs.
 */
class AAndNotA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Negation(a))
    override val patternTo: FOLPattern = False()

}

/**
 * NOTE: There is no and false reverse, because this would have infinite number of possible outputs.
 */
class AndFalse : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, False())
    override val patternTo: FOLPattern = False()
}

class AndTrue : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, True())
    override val patternTo: FOLPattern = a
}

class AndTrueReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = AndTrue()
}

class AAndA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, a)
    override val patternTo: FOLPattern = a
}

class AAndAReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = AAndA()
}

/**
 * -----------------------------BEGIN IFF RELATED EQUIVALENCES---------------------
 */

class CommutativityIFF : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = IFF(b, a)
}

class IFFToDoubleImplies : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()

    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = And(Implies(a, b), Implies(b, a))
}

class IFFToDoubleImpliesReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = IFFToDoubleImplies()

}

class NotIFF : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(IFF(a, b))
    override val patternTo: FOLPattern = IFF(Not(a), b)
}

class IFFToDoubleNotIFF : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = IFF(a, b)
    override val patternTo: FOLPattern = IFF(Not(a), Not(b))
}

class IFFToDoubleNotIFFReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = IFFToDoubleNotIFF()
}


/**
 * -----------------------------BEGIN IMPLIES RELATED EQUIVALENCES-----------------
 */

class AImpliesA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, a)
    override val patternTo: FOLPattern = True()
}

class TrueImpliesA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(True(), a)
    override val patternTo: FOLPattern = a
}

class TrueImpliesAReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = TrueImpliesA()
}

class AImpliesTrue : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, True())
    override val patternTo: FOLPattern = True()
}

class FalseImpliesA : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(False(), a)
    override val patternTo: FOLPattern = True()
}

class AImpliesFalse : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, False())
    override val patternTo: FOLPattern = Not(a)
}

class AImpliesFalseReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = AImpliesFalse()
}

class ImpliesAsOr : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Implies(a, b)
    override val patternTo: FOLPattern = Or(Not(a), b)
}

class ImpliesAsOrReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ImpliesAsOr()

}

class ModusPonens : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Implies(a, b))
    override val patternTo: FOLPattern = And(a, b)
}

class ModusPonensReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ModusPonens()
}


/**
 * -----------------------------BEGIN NOT RELATED EQUIVALENCES---------------------
 */

class DoubleNotElimination : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Not(a))
    override val patternTo: FOLPattern = a
}

class DoubleNotReverse : Equivalence() {
    private val a = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Not(a))
    override val patternTo: FOLPattern = a
}

class NotFalse : Equivalence() {
    override val patternFrom: FOLPattern = Not(False())
    override val patternTo: FOLPattern = True()
}

class NotFalseReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = NotFalse()
}


class NotTrue : Equivalence() {
    override val patternFrom: FOLPattern = Not(True())
    override val patternTo: FOLPattern = False()
}

class NotTrueReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = NotTrue()

}

/**
 * -----------------------------BEGIN DE MORGAN LAWS ------------------------------
 */

class DeMorganLawOr : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(Or(a, b))
    override val patternTo: FOLPattern = And(Not(a), Not(b))
}

class DeMorganLawOrReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DeMorganLawOr()
}

class DeMorganLawAnd : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Not(And(a, b))
    override val patternTo: FOLPattern = Or(Not(a), Not(b))
}

class DeMorganLawAndReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DeMorganLawAnd()
}

/**
 * -----------------------------DISTRIBUTIVITY ------------------------------------
 */

class DistributeOrOverAnd : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = Or(a, And(b, c))
    override val patternTo: FOLPattern = And(Or(a, b), Or(a, c))
}

class DistributeOrOverAndReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DistributeOrOverAnd()
}

class DistributeAndOverOr : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val c = AllowAllVars()
    override val patternFrom: FOLPattern = And(a, Or(b, c))
    override val patternTo: FOLPattern = Or(And(a, b), And(a, c))
}

class DistributeAndOverOrReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DistributeAndOverOr()
}

/**
 * -----------------------------PREDICATE EQUIVALENCES ----------------------------
 */

class SwapForAll : Equivalence() {
    private val a = AllowAllVars()
    private val v1 = VariableName()
    private val v2 = VariableName()
    override val patternFrom: FOLPattern = ForAll(ForAll(a, v1), v2)
    override val patternTo: FOLPattern = ForAll(ForAll(a, v2), v1)
}

class SwapExists : Equivalence() {
    private val a = AllowAllVars()
    private val v1 = VariableName()
    private val v2 = VariableName()
    override val patternFrom: FOLPattern = Exists(Exists(a, v1), v2)
    override val patternTo: FOLPattern = Exists(Exists(a, v2), v1)
}

class PushNegationThroughForAll : Equivalence() {
    private val a = AllowAllVars()
    val v1 = VariableName()
    override val patternFrom: FOLPattern = Not(ForAll(a, v1))
    override val patternTo: FOLPattern = Exists(Not(a), v1)
}

class PushNegationThroughForAllReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = PushNegationThroughForAll()
}

class PushNegationThroughExist : Equivalence() {
    private val a = AllowAllVars()
    private val v1 = VariableName()
    override val patternFrom: FOLPattern = Not(Exists(a, v1))
    override val patternTo: FOLPattern = ForAll(Not(a), v1)
}


class PushNegationThroughExistReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = PushNegationThroughExist()
}

class DistributeForAllOverAnd : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val v1 = VariableName()
    override val patternFrom: FOLPattern = ForAll(And(a, b), v1)
    override val patternTo: FOLPattern = And(ForAll(a, v1), ForAll(b, v1))
}

class DistributeForAllOverAndReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DistributeForAllOverAnd()
}

class DistributeExistOverOr : Equivalence() {
    private val a = AllowAllVars()
    private val b = AllowAllVars()
    private val v1 = VariableName()
    override val patternFrom: FOLPattern = Exists(Or(a, b), v1)
    override val patternTo: FOLPattern = Or(Exists(a, v1), Exists(b, v1))
}

class DistributeExistOverOrReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = DistributeExistOverOr()
}

/**
 * --------------------PREDICATE EQUIVALENCES SPECIAL CASES------------------------
 */

class ExistAToA : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    override val patternFrom: FOLPattern = Exists(a, v)
    override val patternTo: FOLPattern = a
}

class ExistToAReverse : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: FOLPattern = a
    override val patternTo: FOLPattern = Exists(a)
}

class ForAllAToA : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    override val patternFrom: FOLPattern = ForAll(a, v)
    override val patternTo: FOLPattern = a
}

class ForAllAToAReverse : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: FOLPattern = a
    override val patternTo: FOLPattern = ForAll(a)
}


class ExistOverAnd : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Exists(And(a, b), v)
    override val patternTo: FOLPattern = And(a, Exists(b, v))
}

class ExistOverAndReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ExistOverAnd()
}

class ForAllOverOr : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = ForAll(Or(a, b), v)
    override val patternTo: FOLPattern = Or(a, ForAll(b, v))
}

class ForAllOverOrReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ForAllOverOr()
}

class ForAllOverImplies : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = ForAll(Implies(a, b), v)
    override val patternTo: FOLPattern = Implies(a, ForAll(b, v))
}

class ForAllOverImpliesReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ForAllOverImplies()
}

class ExistsOverImplies : Equivalence() {
    private val v = VariableName()
    private val a = ForbidCertainVars(arrayOf(v))
    private val b = AllowAllVars()
    override val patternFrom: FOLPattern = Exists(Implies(a, b), v)
    override val patternTo: FOLPattern = Implies(a, Exists(b, v))
}

class ExistsOverImpliesReverse : ReverseEquivalence() {
    override val toReverse: Equivalence = ExistsOverImplies()
}

