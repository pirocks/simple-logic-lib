package io.github.pirocks.logic

infix fun FOLFormula.and(right: FOLFormula): FOLFormula {
    return And(this, right)
}

infix fun FOLFormula.or(right: FOLFormula): FOLFormula {
    return Or(this, right)
}

infix fun FOLFormula.iff(right: FOLFormula): FOLFormula {
    return IFF(this, right)
}

infix fun FOLFormula.implies(right: FOLFormula): FOLFormula {
    return Implies(this, right)
}

fun not(toNegate: FOLFormula): FOLFormula {
    return Not(toNegate)
}

fun forall(variableName: VariableName = VariableName(), init: VariableName.() -> FOLFormula): FOLFormula {
    return ForAll(variableName.init(), variableName)
}

fun exists(variableName: VariableName = VariableName(), init: VariableName.() -> FOLFormula): FOLFormula {
    return Exists(variableName.init(), variableName)
}