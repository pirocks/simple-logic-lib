package uk.ac.ic.doc.fpn17.logic

import uk.ac.ic.doc.fpn17.util.UUIDUtil
import java.util.*

data class SignatureElement(val uuid: UUID)
data class Variable(val uuid: UUID, val value: SignatureElement)
class Signature(val elements: Set<SignatureElement>/*, val predicates: Set<(SignatureElement) -> Boolean>*/)
class EvalContext(val signature: Signature, val variables: MutableMap<UUID, Variable>)

class EqualityContext(
        //from: other var
        //to: our vars
        val uuidVariableMappings: Map<UUID,UUID> = mutableMapOf())
interface FOLFormula {
    val subFormulas: Array<FOLFormula>
    fun sameAs(other:FOLFormula):Boolean{
        return sameAsImpl(other, EqualityContext())
    }
    open fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext):Boolean {
        //by default if subformulas are equivalent then these are equivalent.
        if(javaClass != other.javaClass || subFormulas.size != other.subFormulas.size){
            return false
        }
        for (i in 0 until subFormulas.size){
            if(!subFormulas[i].sameAsImpl(other.subFormulas[i],equalityContext)){
                return false
            }
        }
        return true
    }
    fun evaluate(ev: EvalContext): Boolean
    fun toMathML2(): String
    fun toHtml(): String = ("<math> <mrow>" + toMathML2() + "</mrow> </math>").replace("\\s(?!separators)".toRegex(), "").trim().trimIndent()
}

class True : FOLFormula {
    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext):Boolean = other is True;

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()

    override fun evaluate(ev: EvalContext): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toMathML2(): String = "<mi>T</mi>"

}

class False : FOLFormula {
    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext):Boolean = other is False;

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()

    override fun evaluate(ev: EvalContext): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toMathML2(): String = "<mi>&perp;</mi>"

}

data class Predicate(val implmentation: (Array<Variable>) -> Boolean, val uuid: UUID)

data class PredicateAtom(val predicate: Predicate, val expectedArgs: Array<UUID>) : FOLFormula {
    override fun sameAs(other: FOLFormula): Boolean{
        //this should only be called when comparing to atoms. Anything wrapped in quantifiers should not call this:
        assert(expectedArgs.isEmpty())
        if(other !is PredicateAtom){
            return false;
        } else{
            assert(other.expectedArgs.isEmpty())
            return predicate.uuid == other.predicate.uuid;
        }
    }

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        if(other !is PredicateAtom){
            return false;
        }
        if(other.expectedArgs.size != expectedArgs.size){
            return false
        }
        if(other.predicate.uuid != predicate.uuid){
            return false
        }
        fun translateExpectedArgs(toTranslate: Array<UUID> ): Array<UUID?> = toTranslate.map { equalityContext.uuidVariableMappings[it]!! }.toTypedArray()// okay to assert not null, because there are no free vars. So there shouldn't be unknown vars
        return expectedArgs.contentDeepEquals(translateExpectedArgs(other.expectedArgs))
    }

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()

    override fun evaluate(ev: EvalContext): Boolean {
        val args: Array<Variable?> = arrayOfNulls<Variable?>(expectedArgs.size)
        for ((i, expectedArg) in expectedArgs.withIndex()) {
            args[i] = ev.variables[expectedArg]
        }
        val notNullArgs: Array<Variable> = Array(args.size, init = {
            args[it]!!
        })
        return predicate.implmentation.invoke(notNullArgs)
    }

    private fun predicateString():String = "predicateNumber" + predicate.hashCode().toString(16)

    override fun toMathML2(): String = """<mrow><mi>${predicateString()}</mi><mfenced>${expectedArgs.map { "<mrow>" + UUIDUtil.toMathML2(it) + "</mrow>"}.reduceRight { s: String, acc: String -> s + acc }}</mfenced></mrow>"""

}

data class And(val left: FOLFormula, val right: FOLFormula) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(left,right)

    override fun toMathML2(): String = """
    <mrow>
    ${left.toMathML2()}
    <mo>&and;</mo>
    ${right.toMathML2()}
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) && right.evaluate(ev)
}

data class Or(val left: FOLFormula, val right: FOLFormula) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(left,right)

    override fun toMathML2(): String = """
    <mrow>
    <mfenced separators="">
    <mrow>${left.toMathML2()}</mrow>
    <mo>&or;</mo>
    <mrow>${right.toMathML2()}</mrow>
    </mfenced>
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) || right.evaluate(ev)
}

data class Negation(val child: FOLFormula) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(child)

    override fun toMathML2(): String = """
    <mrow>
    <mo>&not;</mo>
    ${child.toMathML2()}
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = !child.evaluate(ev)
}

data class Implies(val given: FOLFormula, val result: FOLFormula) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(given,result)

    override fun toMathML2(): String = """
    <mrow>
    <mfenced separators="">
    <mrow>${given.toMathML2()}</mrow>
    <mo>&rArr;</mo>
    <mrow>${result.toMathML2()}</mrow>
    </mfenced>
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = !given.evaluate(ev) || result.evaluate(ev)
}

data class IFF(val one: FOLFormula, val two: FOLFormula) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(one,two)

    override fun toMathML2(): String = """
    <mrow>
    <mfenced separators="">
    <mrow>${one.toMathML2()}</mrow>
    <mo>&hArr;</mo>
    <mrow>${two.toMathML2()}</mrow>
    </mfenced>
    </mrow>"""
    override fun evaluate(ev: EvalContext): Boolean = one.evaluate(ev) == two.evaluate(ev)
}

data class ForAll(val child: FOLFormula, val varUUID: UUID = UUIDUtil.generateUUID()) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(child)

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        if(other !is ForAll){
            return false
        }

        val newEqualityContext = EqualityContext(equalityContext.uuidVariableMappings + mutableMapOf<UUID,UUID>(Pair(other.varUUID,varUUID)))
        return child.sameAsImpl(other.child,newEqualityContext);
    }

    override fun toMathML2(): String = """
    <mrow>
    <mo>&forall;</mo>
    <mrow>${UUIDUtil.toMathML2(varUUID)}</mrow>
    <mrow>
        <mfenced separators="">
        <mrow>${child.toMathML2()}</mrow>
        </mfenced>
    </mrow>
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.all {
        val `var` = Variable(varUUID, it)
        ev.variables.put(varUUID, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varUUID)
        return res
    }
}

data class Exists(val child: FOLFormula, val varUUID: UUID = UUIDUtil.generateUUID()) : FOLFormula {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(child)

    override fun toMathML2(): String = """
    <mrow>
    <mo>&exist;</mo>
    <mrow>${UUIDUtil.toMathML2(varUUID)}</mrow>
    <mrow>
        <mfenced separators="">
        <mrow>${child.toMathML2()}</mrow>
        </mfenced>
    </mrow>
    </mrow>"""

    //todo possible duplication with exsists
    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        if(other !is Exists){
            return false
        }

        val newEqualityContext = EqualityContext(equalityContext.uuidVariableMappings + mutableMapOf<UUID,UUID>(Pair(other.varUUID,varUUID)))
        return child.sameAsImpl(other.child,newEqualityContext);
    }

    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.any {
        val `var` = Variable(varUUID, it)
        ev.variables.put(varUUID, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varUUID)
        return res
    }
}

//todo refactor implmentations of variables so they have string names and are abstracted instead of having a uuid only.
