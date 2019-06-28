package io.github.pirocks.parsers

import io.github.pirocks.logic.FOLFormula
import java.io.File

interface FOLParser{
    fun parse(toParse : File) : FOLFormula = parse(toParse.reader().readText())
    fun parse(toParse : String): FOLFormula
}