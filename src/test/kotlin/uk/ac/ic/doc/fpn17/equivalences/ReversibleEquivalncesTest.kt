package uk.ac.ic.doc.fpn17.equivalences

import org.junit.Test
import uk.ac.ic.doc.fpn17.logic.*

abstract class ReverseEquivalenceTest {
    abstract val forward: EquivalenceImpl
    abstract val backwards: EquivalenceImpl
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

private val aPredicate = PredicateAtom(Predicate({false}), arrayOf())
private val bPredicate = PredicateAtom(Predicate({false}), arrayOf())
private val cPredicate = PredicateAtom(Predicate({false}), arrayOf())
private val falseAtom = False()
private val trueAtom = True()

class OrAssociativityReverseEquivalenceTest : ReverseEquivalenceTest() {
    override val forward: EquivalenceImpl
        get() = OrAssociativity()
    override val backwards: EquivalenceImpl
        get() = OrAssociativityReverse()
    override val expression: FOLFormula
        get() = Or(aPredicate,Or(bPredicate,And(cPredicate,Implies(falseAtom,trueAtom))))
}

class OrIntroductionFalseVariantReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = OrIntroductionFalseVariant()
    override val backwards: EquivalenceImpl
        get() = OrIntroductionFalseVariantReverse()
    override val expression: FOLFormula
        get() = And(falseAtom,Implies(trueAtom,Or(aPredicate,bPredicate)))
}
class AOrAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = AOrA()
    override val backwards: EquivalenceImpl
        get() = AOrAReverse()
    override val expression: FOLFormula
        get() = Or(Or(aPredicate,And(bPredicate,Implies(falseAtom,trueAtom))),Or(aPredicate,And(bPredicate,Implies(falseAtom,trueAtom))))
}
class AndAssociativityReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = AndAssociativity()
    override val backwards: EquivalenceImpl
        get() = AndAssociativityReverse()
    override val expression: FOLFormula
        get() = And(aPredicate,And(bPredicate,And(cPredicate,Implies(falseAtom,trueAtom))))
}
class AndTrueReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = AndTrue()
    override val backwards: EquivalenceImpl
        get() = AndTrueReverse()
    override val expression: FOLFormula
        get() = And(Or(Implies(aPredicate,bPredicate),falseAtom),trueAtom)
}
class AAndAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = AAndA()
    override val backwards: EquivalenceImpl
        get() = AAndAReverse()
    override val expression: FOLFormula
        get() = And(Or(aPredicate,And(bPredicate,Implies(falseAtom,trueAtom))),Or(aPredicate,And(bPredicate,Implies(falseAtom,trueAtom))))
}
class IFFToDoubleImpliesReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = IFFToDoubleImplies()
    override val backwards: EquivalenceImpl
        get() = IFFToDoubleImpliesReverse()
    override val expression: FOLFormula
        get() = IFF(And(aPredicate,bPredicate),And(aPredicate,bPredicate))
}
class IFFToDoubleNotIFFReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = IFFToDoubleNotIFF()
    override val backwards: EquivalenceImpl
        get() = IFFToDoubleNotIFFReverse()
    override val expression: FOLFormula
        get() = IFF(And(aPredicate,falseAtom),And(bPredicate,trueAtom))
}
class TrueImpliesAReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = TrueImpliesA()
    override val backwards: EquivalenceImpl
        get() = TrueImpliesAReverse()
    override val expression: FOLFormula
        get() = Implies(trueAtom,Implies(aPredicate, bPredicate))
}
class AImpliesFalseReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = AImpliesFalse()
    override val backwards: EquivalenceImpl
        get() = AImpliesFalseReverse()
    override val expression: FOLFormula
        get() = And(aPredicate,Implies(bPredicate, falseAtom))
}
class ImpliesAsOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = ImpliesAsOr()
    override val backwards: EquivalenceImpl
        get() = ImpliesAsOrReverse()
    override val expression: FOLFormula
        get() = And(aPredicate,Implies(trueAtom, bPredicate))
}
class ModusPonensReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = ModusPonens()
    override val backwards: EquivalenceImpl
        get() = ModusPonensReverse()
    override val expression: FOLFormula
        get() = IFF(aPredicate,And(bPredicate,Implies(bPredicate, trueAtom)))
}
class NotFalseReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = NotFalse()
    override val backwards: EquivalenceImpl
        get() = NotFalseReverse()
    override val expression: FOLFormula
        get() = And(aPredicate,Not(falseAtom))
}
class NotTrueReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = NotTrue()
    override val backwards: EquivalenceImpl
        get() = NotTrueReverse()
    override val expression: FOLFormula
        get() = Implies(Not(aPredicate),Not(Not(trueAtom)))
}
class DeMorganLawOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = DeMorganLawOr()
    override val backwards: EquivalenceImpl
        get() = DeMorganLawOrReverse()
    override val expression: FOLFormula
        get() = Not(Or(IFF(aPredicate, falseAtom),IFF(bPredicate, trueAtom)))
}
class DeMorganLawAndReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = DeMorganLawAnd()
    override val backwards: EquivalenceImpl
        get() = DeMorganLawAndReverse()
    override val expression: FOLFormula
        get() = Not(And(IFF(aPredicate, falseAtom),IFF(bPredicate, trueAtom)))
}
class DistributeOrOverAndReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = DistributeOrOverAnd()
    override val backwards: EquivalenceImpl
        get() = DistributeOrOverAndReverse()
    override val expression: FOLFormula
        get() = Or(aPredicate,And(bPredicate, cPredicate))
}
class DistributeAndOverOrReverseEquivalenceTest : ReverseEquivalenceTest(){
    override val forward: EquivalenceImpl
        get() = DistributeAndOverOr()
    override val backwards: EquivalenceImpl
        get() = DistributeAndOverOrReverse()
    override val expression: FOLFormula
        get() = And(aPredicate,Or(bPredicate, cPredicate))
}