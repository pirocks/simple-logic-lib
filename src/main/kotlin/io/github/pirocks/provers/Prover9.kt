package io.github.pirocks.provers

import io.github.pirocks.logic.FOLFormula
import java.io.File

class Prover9(val prover9Exe: File = File("/usr/bin/prover9")) : Prover {
    init {
        val process = Runtime.getRuntime().exec(arrayOf(prover9Exe.absolutePath.toString(), "-h"))
        process.waitFor()
        val expectProver9Output = "============================== Prover9 ==============================="
        val validProver9 = process.inputStream.bufferedReader().lines().anyMatch { it == expectProver9Output }
        if(!validProver9){
            throw IllegalArgumentException("No valid prover9 executable found/passed in.")
        }
    }

    override fun prove(givens: Collection<FOLFormula>, toProve: FOLFormula): Proof {
        val proverGivensInput = "formulas(assumptions).\n${givens.map { it.prover9Form() }.joinToString(separator = ".\n", postfix = ".\n")}end_of_list.\n"
        val proverGoals = "formulas(goals).\n${toProve.prover9Form()}.\nend_of_list.\n"
        val process = Runtime.getRuntime().exec(arrayOf(prover9Exe.absolutePath.toString()))
        val writer = process.outputStream.bufferedWriter()
        val reader = process.inputStream.bufferedReader()
        writer.write(proverGivensInput)
        println(proverGivensInput)
        writer.write(proverGoals)
        println(proverGoals)
        writer.flush()
        writer.close()
        process.waitFor()
        reader.forEachLine { println(it)  }
        TODO()
    }

}
