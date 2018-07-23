package uk.ac.ic.doc.fpn17.logic

import java.util.*

class RewriteRules(val rewritePredicateAtom: (PredicateAtom, RewriteRules) -> PredicateAtom = ::recursiveRewritePredicateAtom,
                   val rewriteAnd: (And, RewriteRules) -> And = ::recursiveRewriteAnd,
                   val rewriteOr: (Or, RewriteRules) -> Or = ::recursiveRewriteOr,
                   val rewriteNegation: (Negation, RewriteRules) -> Negation = ::recursiveRewriteNegation,
                   val rewriteImplies: (Implies, RewriteRules) -> Implies = ::recursiveRewriteImplies,
                   val rewriteIFF: (IFF, RewriteRules) -> IFF = ::recursiveRewriteIFF,
                   val rewriteForAll: (ForAll, RewriteRules) -> ForAll = ::recursiveRewriteForAll,
                   val rewriteExists: (Exists, RewriteRules) -> Exists = ::recursiveRewriteExists)

fun recursiveRewrite(original: FOLFormula, rewrite: RewriteRules): FOLFormula {
    //todo make this way more object oriented
    if (original is PredicateAtom) {
        return rewrite.rewritePredicateAtom(original, rewrite)
    }
    if (original is And) {
        return rewrite.rewriteAnd(original, rewrite)
    }
    if (original is Or) {
        return rewrite.rewriteOr(original, rewrite)
    }
    if (original is Negation) {
        return rewrite.rewriteNegation(original, rewrite)
    }
    if (original is Implies) {
        return rewrite.rewriteImplies(original, rewrite)
    }
    if (original is IFF) {
        return rewrite.rewriteIFF(original, rewrite)
    }
    if (original is ForAll) {
        return rewrite.rewriteForAll(original, rewrite)
    }
    if (original is Exists) {
        return rewrite.rewriteExists(original, rewrite)
    }
    if (original is False) {
        return False()
    }
    if (original is True) {
        return True()
    }
    assert(false)
    throw IllegalStateException()
}

fun recursiveRewritePredicateAtom(toRewrite: PredicateAtom, rewrite: RewriteRules): PredicateAtom {
    return toRewrite.copy()
}

fun recursiveRewriteAnd(toRewrite: And, rewrite: RewriteRules): And {
    return And(recursiveRewrite(toRewrite.left, rewrite), recursiveRewrite(toRewrite.right, rewrite))
}

fun recursiveRewriteOr(toRewrite: Or, rewrite: RewriteRules): Or {
    return Or(recursiveRewrite(toRewrite.left, rewrite), recursiveRewrite(toRewrite.right, rewrite))
}

fun recursiveRewriteNegation(toRewrite: Negation, rewrite: RewriteRules): Negation {
    return Negation(recursiveRewrite(toRewrite.child, rewrite))
}

fun recursiveRewriteImplies(toRewrite: Implies, rewrite: RewriteRules): Implies {
    return Implies(recursiveRewrite(toRewrite.given, rewrite), recursiveRewrite(toRewrite.result, rewrite))
}

fun recursiveRewriteIFF(toRewrite: IFF, rewrite: RewriteRules): IFF {
    return IFF(recursiveRewrite(toRewrite.one, rewrite), recursiveRewrite(toRewrite.two, rewrite))
}

fun recursiveRewriteForAll(toRewrite: ForAll, rewrite: RewriteRules): ForAll {
    return ForAll(recursiveRewrite(toRewrite.child, rewrite), toRewrite.varUUID)
}

fun recursiveRewriteExists(toRewrite: Exists, rewrite: RewriteRules): Exists {
    return Exists(recursiveRewrite(toRewrite.child, rewrite), toRewrite.varUUID)
}

fun renameVar(formula: FOLFormula, fromUUID: UUID, toUUID: UUID): FOLFormula {
    val renameVarsPredicate = { predicateAtom: PredicateAtom, rewriteRules: RewriteRules ->
        PredicateAtom(predicateAtom.predicate, predicateAtom.expectedArgs.copyOf().map {
            val newVarName =
                    if (it == fromUUID)
                        toUUID
                    else it
            newVarName
        }.toTypedArray())
    }
    //maybe this could be more legible and more object oriented todo. also abstraction for quantifier would be nice
    val renameVarsForAll = { previousForAll: ForAll, rewriteRules: RewriteRules ->
        ForAll(recursiveRewrite(previousForAll.child, rewriteRules), if (previousForAll.varUUID == fromUUID) toUUID else previousForAll.varUUID)
    }
    val renameVarsExist = { previousExist: Exists, rewriteRules: RewriteRules ->
        Exists(recursiveRewrite(previousExist.child, rewriteRules), if (previousExist.varUUID == fromUUID) toUUID else previousExist.varUUID)
    }
    return recursiveRewrite(formula, RewriteRules(rewritePredicateAtom = renameVarsPredicate, rewriteForAll = renameVarsForAll, rewriteExists = renameVarsExist))
}