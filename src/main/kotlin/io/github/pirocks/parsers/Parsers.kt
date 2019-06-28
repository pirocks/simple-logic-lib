package io.github.pirocks.parsers

import io.github.pirocks.logic.FOLFormula
import java.io.File

interface Parser{
    fun parse(toParse : File) : FOLFormula
    fun parse(toParse : String): FOLFormula
}