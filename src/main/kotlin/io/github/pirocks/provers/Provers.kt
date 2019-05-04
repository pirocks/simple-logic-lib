package io.github.pirocks.provers

import io.github.pirocks.logic.FOLFormula

interface Proof{

}

interface Prover{
    fun prove(givens: Collection<FOLFormula>, toProve : FOLFormula) : Proof
}