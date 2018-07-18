package uk.ac.ic.doc.fpn17.logic

import uk.ac.ic.doc.fpn17.util.UUIDUtil
import java.util.*

data class SignatureElement(val uuid: UUID)
data class Variable(val uuid: UUID, val value: SignatureElement)
class Signature(val elements: Set<SignatureElement>/*, val predicates: Set<(SignatureElement) -> Boolean>*/)
class EvalContext(val signature: Signature, val variables: MutableMap<UUID, Variable>)

interface FOLFormula {
    fun evaluate(ev: EvalContext): Boolean
}

class True : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = true
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

class False : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = true
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

data class PredicateAtom(val predicate: (Array<Variable>) -> Boolean, val expectedArgs: Array<UUID>) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean {
        val args: Array<Variable?> = arrayOfNulls<Variable?>(expectedArgs.size)
        for ((i, expectedArg) in expectedArgs.withIndex()) {
            args[i] = ev.variables[expectedArg]
        }
        val notNullArgs: Array<Variable> = Array(args.size, init = {
            args[it]!!
        })
        return predicate.invoke(notNullArgs)
    }

}

data class And(val left: FOLFormula, val right: FOLFormula) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) && right.evaluate(ev)
}

data class Or(val left: FOLFormula, val right: FOLFormula) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) || right.evaluate(ev)
}

data class Negation(val child: FOLFormula) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = !child.evaluate(ev)
}

data class Implies(val given: FOLFormula, val result: FOLFormula) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = !given.evaluate(ev) || result.evaluate(ev)
}

data class IFF(val one: FOLFormula, val two: FOLFormula) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = one.evaluate(ev) == two.evaluate(ev)
}

data class ForAll(val child: FOLFormula, val varUUID: UUID = UUIDUtil.generateUUID()) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.all {
        val `var` = Variable(varUUID, it)
        ev.variables.put(varUUID, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varUUID)
        return res
    }
}

data class Exists(val child: FOLFormula, val varUUID: UUID = UUIDUtil.generateUUID()) : FOLFormula {
    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.any {
        val `var` = Variable(varUUID, it)
        ev.variables.put(varUUID, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varUUID)
        return res
    }
}

class RewriteRules(val rewritePredicateAtom: (PredicateAtom,RewriteRules) -> PredicateAtom = ::recursiveRewritePredicateAtom,
                   val rewriteAnd: (And,RewriteRules) -> And = ::recursiveRewriteAnd,
                   val rewriteOr: (Or,RewriteRules) -> Or = ::recursiveRewriteOr,
                   val rewriteNegation: (Negation,RewriteRules) -> Negation = ::recursiveRewriteNegation,
                   val rewriteImplies: (Implies,RewriteRules) -> Implies = ::recursiveRewriteImplies,
                   val rewriteIFF: (IFF,RewriteRules) -> IFF = ::recursiveRewriteIFF,
                   val rewriteForAll: (ForAll,RewriteRules) -> ForAll = ::recursiveRewriteForAll,
                   val rewriteExists: (Exists,RewriteRules) -> Exists = ::recursiveRewriteExists)

fun recursiveRewrite(original: FOLFormula, rewrite:RewriteRules): FOLFormula {
    //todo make this way more object oriented
    if (original is PredicateAtom) {
        return rewrite.rewritePredicateAtom(original,rewrite)
    }
    if (original is And) {
        return rewrite.rewriteAnd(original,rewrite)
    }
    if (original is Or) {
        return rewrite.rewriteOr(original,rewrite)
    }
    if (original is Negation) {
        return rewrite.rewriteNegation(original,rewrite)
    }
    if (original is Implies) {
        return rewrite.rewriteImplies(original,rewrite)
    }
    if (original is IFF) {
        return rewrite.rewriteIFF(original,rewrite)
    }
    if (original is ForAll) {
        return rewrite.rewriteForAll(original,rewrite)
    }
    if (original is Exists) {
        return rewrite.rewriteExists(original,rewrite)
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

fun recursiveRewritePredicateAtom(toRewrite: PredicateAtom,rewrite: RewriteRules): PredicateAtom {
    return toRewrite.copy()
}

fun recursiveRewriteAnd(toRewrite: And,rewrite: RewriteRules): And {
    return And(recursiveRewrite(toRewrite.left,rewrite),recursiveRewrite(toRewrite.right,rewrite))
}

fun recursiveRewriteOr(toRewrite: Or,rewrite: RewriteRules): Or {
    return Or(recursiveRewrite(toRewrite.left,rewrite),recursiveRewrite(toRewrite.right,rewrite))
}

fun recursiveRewriteNegation(toRewrite: Negation,rewrite: RewriteRules): Negation {
    return Negation(recursiveRewrite(toRewrite.child,rewrite))
}

fun recursiveRewriteImplies(toRewrite: Implies,rewrite: RewriteRules): Implies {
    return Implies(recursiveRewrite(toRewrite.given,rewrite),recursiveRewrite(toRewrite.result,rewrite))
}

fun recursiveRewriteIFF(toRewrite: IFF,rewrite: RewriteRules): IFF {
    return IFF(recursiveRewrite(toRewrite.one,rewrite),recursiveRewrite(toRewrite.two,rewrite))
}

fun recursiveRewriteForAll(toRewrite: ForAll,rewrite: RewriteRules): ForAll {
    return ForAll(recursiveRewrite(toRewrite.child,rewrite),toRewrite.varUUID)
}

fun recursiveRewriteExists(toRewrite: Exists,rewrite: RewriteRules): Exists {
    return Exists(recursiveRewrite(toRewrite.child,rewrite),toRewrite.varUUID)
}

