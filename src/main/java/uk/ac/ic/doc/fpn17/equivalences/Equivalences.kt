package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import java.util.*


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, index:Int):FOLFormula
}

//there will be one equivalence for left direction, and one for right
abstract class EquivalenceImpl : Equivalence{
    abstract val patternFrom:FOLFormula;
    abstract val patternTo:FOLFormula;

    private fun matchesImpl(formula: FOLFormula,pattern:FOLFormula): Boolean {
        var res: Int = 0;
        object : RewritingVisitor(){
            override fun rewriteAnd(toRewrite: And): And {
                return super.rewriteAnd(toRewrite)
            }

            override fun rewriteExists(toRewrite: Exists): Exists {
                return super.rewriteExists(toRewrite)
            }

            override fun rewriteForAll(toRewrite: ForAll): ForAll {
                return super.rewriteForAll(toRewrite)
            }

            override fun rewriteIFF(toRewrite: IFF): IFF {
                return super.rewriteIFF(toRewrite)
            }

            override fun rewriteImplies(toRewrite: Implies): Implies {
                return super.rewriteImplies(toRewrite)
            }

            override fun rewriteNegation(toRewrite: Negation): Negation {
                return super.rewriteNegation(toRewrite)
            }

            override fun rewriteOr(toRewrite: Or): Or {
                return super.rewriteOr(toRewrite)
            }

            override fun rewritePredicateAtom(toRewrite: PredicateAtom): PredicateAtom {
                return super.rewritePredicateAtom(toRewrite)
            }
        }
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