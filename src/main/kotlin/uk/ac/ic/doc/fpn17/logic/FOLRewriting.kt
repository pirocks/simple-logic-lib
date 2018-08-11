package uk.ac.ic.doc.fpn17.logic

abstract class RewritingVisitor() {

    open fun rewrite(original: FOLFormula): FOLFormula {
        return when (original) {
            is PredicateAtom -> rewritePredicateAtom(original)
            is True -> rewriteTrue(original)
            is False -> rewriteFalse(original)
            is Negation -> rewriteNegation(original)
            is BinaryRelation -> rewriteBinaryRelation(original)
            is Quantifier -> rewriteQuantifier(original)
            is PatternMember -> rewritePatternMember(original)
        }
    }

    open fun rewritePatternMember(original: PatternMember): FOLFormula{
        return when(original) {
            is AllowAllVars -> rewriteAllowAllVars(original)
            is AllowOnlyCertainVars -> rewriteAllowOnlyCertainVars(original)
            is ForbidCertainVars -> rewriteForbidCertainVars(original)
        }
    }

    open fun rewriteForbidCertainVars(original: ForbidCertainVars): FOLFormula {
        return ForbidCertainVars(original.vars.clone())
    }

    open fun rewriteAllowOnlyCertainVars(original: AllowOnlyCertainVars): FOLFormula {
        return AllowOnlyCertainVars(original.vars.clone())
    }

    open fun rewriteAllowAllVars(original: AllowAllVars): FOLFormula {
        return AllowAllVars()
    }

    /**
     * parameters only included for consistency. Shouldn't actually be necessary
     */
    open fun rewriteTrue(toRewrite:True):FOLFormula = True()
    open fun rewriteFalse(toRewrite:False):FOLFormula = False()

    open fun rewriteBinaryRelation(toRewrite: BinaryRelation):FOLFormula{
        return when(toRewrite){
            is And -> rewriteAnd(toRewrite)
            is Or -> rewriteOr(toRewrite)
            is Implies -> rewriteImplies(toRewrite)
            is IFF -> rewriteIFF(toRewrite)
        }
    }

    open fun rewriteQuantifier(toRewrite:Quantifier):FOLFormula{
        return when(toRewrite){
            is ForAll -> rewriteForAll(toRewrite)
            is Exists -> rewriteExists(toRewrite)
        }
    }

    open fun rewritePredicateAtom(toRewrite: PredicateAtom): FOLFormula {
        return toRewrite
    }

    open fun rewriteAnd(toRewrite: And): FOLFormula {
        return And(rewrite(toRewrite.left), rewrite(toRewrite.right))
    }

    open fun rewriteOr(toRewrite: Or): FOLFormula {
        return Or(rewrite(toRewrite.left), rewrite(toRewrite.right))
    }

    open fun rewriteNegation(toRewrite: Negation): FOLFormula {
        return Negation(rewrite(toRewrite.child))
    }

    open fun rewriteImplies(toRewrite: Implies): FOLFormula {
        return Implies(rewrite(toRewrite.given), rewrite(toRewrite.result))
    }

    open fun rewriteIFF(toRewrite: IFF): FOLFormula {
        return IFF(rewrite(toRewrite.one), rewrite(toRewrite.two))
    }

    open fun rewriteForAll(toRewrite: ForAll): FOLFormula {
        return ForAll(rewrite(toRewrite.child), toRewrite.varName)
    }

    open fun rewriteExists(toRewrite: Exists): FOLFormula {
        return Exists(rewrite(toRewrite.child), toRewrite.varName)
    }

}


public fun renameVar(formula: FOLFormula, from: VariableName, to: VariableName): FOLFormula {
    return object : RewritingVisitor() {
        override fun rewriteForAll(toRewrite: ForAll): ForAll {
            return ForAll(rewrite(toRewrite.child), if (from == toRewrite.varName) to else toRewrite.varName)
        }

        override fun rewriteExists(toRewrite: Exists): Exists {
            return Exists(rewrite(toRewrite.child), if (from == toRewrite.varName) to else toRewrite.varName)
        }

        override fun rewritePredicateAtom(toRewrite: PredicateAtom): PredicateAtom {
            return PredicateAtom(toRewrite.predicate, toRewrite.expectedArgs.map {
                if (it == from) to else it
            }.toTypedArray())
        }
    }.rewrite(formula)
}