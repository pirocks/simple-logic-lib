package uk.ac.ic.doc.fpn17.logic

abstract class RewritingVisitor() {

    open fun rewrite(original: FOLFormula): FOLFormula {
        return when (original) {
            is PredicateAtom -> rewritePredicateAtom(original)
            is True -> True()
            is False -> False()
            is And -> rewriteAnd(original)
            is Or -> rewriteOr(original)
            is Negation -> rewriteNegation(original)
            is Implies -> rewriteImplies(original)
            is IFF -> rewriteIFF(original)
            is ForAll -> rewriteForAll(original)
            is Exists -> rewriteExists(original)
        }
    }

    open fun rewritePredicateAtom(toRewrite: PredicateAtom): PredicateAtom {
        return toRewrite.copy()
    }

    open fun rewriteAnd(toRewrite: And): And {
        return And(rewrite(toRewrite.left), rewrite(toRewrite.right))
    }

    open fun rewriteOr(toRewrite: Or): Or {
        return Or(rewrite(toRewrite.left), rewrite(toRewrite.right))
    }

    open fun rewriteNegation(toRewrite: Negation): Negation {
        return Negation(rewrite(toRewrite.child))
    }

    open fun rewriteImplies(toRewrite: Implies): Implies {
        return Implies(rewrite(toRewrite.given), rewrite(toRewrite.result))
    }

    open fun rewriteIFF(toRewrite: IFF): IFF {
        return IFF(rewrite(toRewrite.one), rewrite(toRewrite.two))
    }

    open fun rewriteForAll(toRewrite: ForAll): ForAll {
        return ForAll(rewrite(toRewrite.child), toRewrite.varName)
    }

    open fun rewriteExists(toRewrite: Exists): Exists {
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