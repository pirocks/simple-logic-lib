package io.github.pirocks.smt

enum class Model {
    BOOLEAN
}


interface Solver {
    fun solveWithSMTLib(smtLibInput: String)
}