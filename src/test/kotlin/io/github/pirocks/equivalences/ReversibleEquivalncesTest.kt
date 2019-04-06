package io.github.pirocks.equivalences

import io.github.pirocks.logic.*
import org.junit.Test

abstract class ReverseEquivalenceTest {
    abstract val forward: Equivalence
    abstract val backwards: Equivalence
    abstract val expression: FOLFormula

    @Test
    fun doTest(){
        val forwardsApplied = forward.apply(expression, 0)
        assert(!forwardsApplied.sameAs(expression))
        assert(!expression.sameAs(forwardsApplied))
        val original = backwards.apply(forwardsApplied,0)
        assert(original.sameAs(expression))
        assert(expression.sameAs(original))
    }
}

private val aPredicate = RelationAtom(Relation.newUnEvaluatableRelation(), arrayOf())
private val bPredicate = RelationAtom(Relation.newUnEvaluatableRelation(), arrayOf())
private val cPredicate = RelationAtom(Relation.newUnEvaluatableRelation(), arrayOf())
private val falseAtom = False()
private val trueAtom = True()

class OrAssociativityReverseEquivalenceTest : ReverseEquivalenceTest() {
    override val forward: Equivalence
        get() = OrAssociativity()
    override val backwards: Equivalence
        get() = OrAssociativityReverse()
    override val expression: FOLFormula
        get() = Or(aPredicate, Or(bPredicate, And(cPredicate, Implies(falseAtom, trueAtom))))
}

class OrIntroductionFalseVariantReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = OrIntroductionFalseVariant()
    override val backwards: Equivalence
        get() = OrIntroductionFalseVariantReverse()
    override val expression: FOLFormula
        get() = And(falseAtom, Implies(trueAtom, Or(aPredicate, bPredicate)))
}
class AOrAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = AOrA()
    override val backwards: Equivalence
        get() = AOrAReverse()
    override val expression: FOLFormula
        get() = Or(Or(aPredicate, And(bPredicate, Implies(falseAtom, trueAtom))), Or(aPredicate, And(bPredicate, Implies(falseAtom, trueAtom))))
}
class AndAssociativityReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = AndAssociativity()
    override val backwards: Equivalence
        get() = AndAssociativityReverse()
    override val expression: FOLFormula
        get() = And(aPredicate, And(bPredicate, And(cPredicate, Implies(falseAtom, trueAtom))))
}
class AndTrueReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = AndTrue()
    override val backwards: Equivalence
        get() = AndTrueReverse()
    override val expression: FOLFormula
        get() = And(Or(Implies(aPredicate, bPredicate), falseAtom), trueAtom)
}
class AAndAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = AAndA()
    override val backwards: Equivalence
        get() = AAndAReverse()
    override val expression: FOLFormula
        get() = And(Or(aPredicate, And(bPredicate, Implies(falseAtom, trueAtom))), Or(aPredicate, And(bPredicate, Implies(falseAtom, trueAtom))))
}
class IFFToDoubleImpliesReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = IFFToDoubleImplies()
    override val backwards: Equivalence
        get() = IFFToDoubleImpliesReverse()
    override val expression: FOLFormula
        get() = IFF(And(aPredicate, bPredicate), And(aPredicate, bPredicate))
}
class IFFToDoubleNotIFFReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = IFFToDoubleNotIFF()
    override val backwards: Equivalence
        get() = IFFToDoubleNotIFFReverse()
    override val expression: FOLFormula
        get() = IFF(And(aPredicate, falseAtom), And(bPredicate, trueAtom))
}
class TrueImpliesAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = TrueImpliesA()
    override val backwards: Equivalence
        get() = TrueImpliesAReverse()
    override val expression: FOLFormula
        get() = Implies(trueAtom, Implies(aPredicate, bPredicate))
}
class AImpliesFalseReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = AImpliesFalse()
    override val backwards: Equivalence
        get() = AImpliesFalseReverse()
    override val expression: FOLFormula
        get() = And(aPredicate, Implies(bPredicate, falseAtom))
}
class ImpliesAsOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ImpliesAsOr()
    override val backwards: Equivalence
        get() = ImpliesAsOrReverse()
    override val expression: FOLFormula
        get() = And(aPredicate, Implies(trueAtom, bPredicate))
}
class ModusPonensReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ModusPonens()
    override val backwards: Equivalence
        get() = ModusPonensReverse()
    override val expression: FOLFormula
        get() = IFF(aPredicate, And(bPredicate, Implies(bPredicate, trueAtom)))
}
class NotFalseReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = NotFalse()
    override val backwards: Equivalence
        get() = NotFalseReverse()
    override val expression: FOLFormula
        get() = And(aPredicate, Not(falseAtom))
}
class NotTrueReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = NotTrue()
    override val backwards: Equivalence
        get() = NotTrueReverse()
    override val expression: FOLFormula
        get() = Implies(Not(aPredicate), Not(Not(trueAtom)))
}
class DeMorganLawOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = DeMorganLawOr()
    override val backwards: Equivalence
        get() = DeMorganLawOrReverse()
    override val expression: FOLFormula
        get() = Not(Or(IFF(aPredicate, falseAtom), IFF(bPredicate, trueAtom)))
}
class DeMorganLawAndReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = DeMorganLawAnd()
    override val backwards: Equivalence
        get() = DeMorganLawAndReverse()
    override val expression: FOLFormula
        get() = Not(And(IFF(aPredicate, falseAtom), IFF(bPredicate, trueAtom)))
}
class DistributeOrOverAndReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = DistributeOrOverAnd()
    override val backwards: Equivalence
        get() = DistributeOrOverAndReverse()
    override val expression: FOLFormula
        get() = Or(aPredicate, And(bPredicate, cPredicate))
}
class DistributeAndOverOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = DistributeAndOverOr()
    override val backwards: Equivalence
        get() = DistributeAndOverOrReverse()
    override val expression: FOLFormula
        get() = And(aPredicate, Or(bPredicate, cPredicate))
}

private val v = VariableName()
private val p = RelationAtom(Relation.newUnEvaluatableRelation(), arrayOf(v))
private val p2 = RelationAtom(Relation.newUnEvaluatableRelation(), arrayOf())

class ForAllAToAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ForAllAToA()
    override val backwards: Equivalence
        get() = ForAllAToAReverse()
    override val expression: FOLFormula
        get() = ForAll(p2, v)

}
class ExistOverAndReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ExistOverAnd()
    override val backwards: Equivalence
        get() = ExistOverAndReverse()
    override val expression: FOLFormula
        get() = Exists(And(Or(p2, True()), p), v)

}
class ForAllOverOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ForAllOverOr()
    override val backwards: Equivalence
        get() = ForAllOverOrReverse()
    override val expression: FOLFormula
        get() = ForAll(Or(p2, p), v)

}
class ForAllOverImpliesReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ForAllOverImplies()
    override val backwards: Equivalence
        get() = ForAllOverImpliesReverse()
    override val expression: FOLFormula
        get() = ForAll(Implies(p2, p), v)

}
class ExistsOverImpliesReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: Equivalence
        get() = ExistsOverImplies()
    override val backwards: Equivalence
        get() = ExistsOverImpliesReverse()
    override val expression: FOLFormula
        get() = Exists(Implies(p2, p), v)

}