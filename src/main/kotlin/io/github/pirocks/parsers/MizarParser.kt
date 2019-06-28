package io.github.pirocks.parsers

import io.github.pirocks.logic.FOLFormula
import java.io.File
import java.io.InputStream

class MizarParser  : Parser{
    override fun parse(toParse: File): FOLFormula {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parse(toParse: String): FOLFormula {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun parseImpl(toParse : InputStream) : FOLFormula{
        TODO()
    }

}