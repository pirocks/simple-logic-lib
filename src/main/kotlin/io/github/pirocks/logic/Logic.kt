package io.github.pirocks.logic

import io.github.pirocks.equivalences.MatchSubstitutions
import io.github.pirocks.util.UUIDUtil
import java.io.Serializable
import java.util.*

/**
 * todo, maybe remove name index. seems unnecessarily complex
 */
val nameIndex: MutableMap<UUID, String> = mutableMapOf()


/**
 * represents anything with an ast
 */
interface Formula: Serializable{
    abstract val subFormulas: Array<FOLFormula>
}

interface FOLPattern: Formula {
    fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean{
        if(formula.javaClass != javaClass){
            return false
        }
        if(formula.subFormulas.isEmpty()){
            return true
        }
        return (0 until formula.subFormulas.size).all {
            subFormulas[it].matches(formula.subFormulas[it],matchSubstitutions)
        }
    }
}

data class SignatureElement(val uuid: UUID)
data class VariableValue(val variableName: VariableName, val value: SignatureElement)
class VariableName : Serializable {
    companion object {

        @JvmStatic
        private var varCount = 0;

        public fun getAndIncrementPredicateCount(): Int {
            varCount++;
            return varCount - 1;
        }
    }


    val uuid: UUID

    constructor(uuid: UUID = UUIDUtil.generateUUID(), name: String = "V" + getAndIncrementPredicateCount().toString()) {
        this.uuid = uuid;
        nameIndex[uuid] = name;
    }

    val name: String
        get() = nameIndex[uuid]!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VariableName) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }


}

data class Signature(val elements: Set<SignatureElement>, val relations: Set<Relation>)
class Relation {
    companion object {
        @JvmStatic
        private var predicateCount = 0;

        fun getAndIncrementPredicateCount(): Int {
            predicateCount++;
            return predicateCount - 1;
        }
    }

    val implmentation: (Array<VariableValue>) -> Boolean
    val uuid: UUID
    val name: String
        get() = nameIndex[uuid]!!

    constructor(implmentation: (Array<VariableValue>) -> Boolean, uuid: UUID = UUIDUtil.generateUUID(), name: String = "P" + getAndIncrementPredicateCount().toString()) {
        this.implmentation = implmentation
        this.uuid = uuid
        nameIndex[uuid] = name
    }

}

class EqualityContext(
        //from: other var
        //to: our vars
        val uuidVariableMappings: Map<VariableName, VariableName> = mapOf())

class HashContext(
        val variableNumberMappings: Map<VariableName, Int> = mutableMapOf()
)

class EvalContext(val signature: Signature, val variables: MutableMap<VariableName, VariableValue>)


sealed class FOLFormula : Formula, FOLPattern {

    @Deprecated("Equals should now be used instead of sameAs", ReplaceWith(".equals()" ))
    open fun sameAs(other: FOLFormula): Boolean {
        return sameAsImpl(other, EqualityContext())
    }

    @Deprecated("Equals should now be used instead of sameAs")
    open fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        //by default if subformulas are equivalent then these are equivalent.
        if (javaClass != other.javaClass || subFormulas.size != other.subFormulas.size) {
            return false
        }
        for (i in 0 until subFormulas.size) {
            if (!subFormulas[i].sameAsImpl(other.subFormulas[i], equalityContext)) {
                return false
            }
        }
        return true
    }

    abstract fun evaluate(ev: EvalContext): Boolean

    abstract fun toMathML2(): String
    fun toHtml(): String = ("<math> <mrow>" + toMathML2() + "</mrow> </math>").replace("\\s(?!separators)".toRegex(), "").trim().trimIndent()
    abstract fun toPrefixNotation(): String
    override fun hashCode(): Int = hashCodeImpl()
    abstract fun hashCodeImpl(hashContext: HashContext = HashContext()) : Int

    override fun equals(other: Any?): Boolean {
        if(other == null ) return false
        if(other.javaClass != this.javaClass) return false
        return this.sameAs(other as FOLFormula)
    }

}

sealed class BinaryRelation(open val left: FOLFormula, open val right: FOLFormula) : FOLFormula() {
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(left, right)

    abstract fun getOperatorAsMathML(): String
    override fun toMathML2(): String = """
        <mrow>
        <mfenced separators="">
        <mrow>${left.toMathML2()}</mrow>
        <mo>${getOperatorAsMathML()}</mo>
        <mrow>${right.toMathML2()}</mrow>
        </mfenced>
        </mrow>
    """
    abstract val operatorHashCode: Int;
    override fun hashCodeImpl(hashContext: HashContext): Int {
        var hash = operatorHashCode
        hash = 31*hash + left.hashCodeImpl(hashContext)
        hash = 31*hash + right.hashCodeImpl(hashContext)
        return hash
    }
}

sealed class Quantifier(open val child: FOLFormula, open val varName: VariableName = VariableName()) : FOLFormula() {
    abstract val operatorHashCode: Int;
    override fun hashCodeImpl(hashContext: HashContext): Int {
        var hash = operatorHashCode
        hash = 31*hash + varName.hashCode()
        hash = 31*hash + child.hashCodeImpl(hashContext)
        return hash
    }
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(child)
    abstract val quantifierSymbol: String
    override fun toMathML2(): String = """
    <mrow>
    <mo>${quantifierSymbol}</mo>
    <mrow>${varName.name}</mrow>
    <mrow>
        <mfenced separators="">
        <mrow>${child.toMathML2()}</mrow>
        </mfenced>
    </mrow>
    </mrow>"""

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        if (other.javaClass != javaClass || other !is Quantifier) {
            return false
        }

        val newEqualityContext = EqualityContext(equalityContext.uuidVariableMappings + mutableMapOf(Pair(other.varName, varName)))
        return child.sameAsImpl(other.child, newEqualityContext);
    }

    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        if(formula.javaClass != javaClass){
            return false
        }
        val varSubstitutions = matchSubstitutions.variableSubstitutions
        varSubstitutions[varName] = (formula as Quantifier).varName;
        try {
            return child.matches(formula.child,matchSubstitutions)
        } finally {
            varSubstitutions.remove(formula.varName)
        }
    }

}

class True : FOLFormula() {
    override fun hashCodeImpl(hashContext: HashContext): Int = 31// can be any arbitrary, reasonably sized prime

    override fun toPrefixNotation(): String = "T"

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean = other is True;

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

class False : FOLFormula() {
    override fun hashCodeImpl(hashContext: HashContext): Int = 43

    override fun toPrefixNotation(): String = "F"

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean = other is False;

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()

    override fun evaluate(ev: EvalContext): Boolean = false
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

open class RelationAtom(val relation: Relation, val expectedArgs: Array<VariableName>) : FOLFormula() {
    override fun hashCodeImpl(hashContext: HashContext): Int {
        var hash = relation.uuid.hashCode()
        expectedArgs.forEach {
            hash = 31*hash + hashContext.variableNumberMappings[it]!!
        }
        return hash
    }

    override fun toPrefixNotation(): String = """(${predicateString()} ${expectedArgs.map { it.name }.foldRight("",{s, acc -> s + " " + acc })}"""

    override fun sameAs(other: FOLFormula): Boolean {
        //this should only be called when comparing to atoms. Anything wrapped in quantifiers should not call this:
        assert(expectedArgs.isEmpty())
        if (other !is RelationAtom) {
            return false;
        } else {
            assert(other.expectedArgs.isEmpty())
            return relation.uuid == other.relation.uuid;
        }
    }

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        if (other !is RelationAtom) {
            return false;
        }
        if (other.expectedArgs.size != expectedArgs.size) {
            return false
        }
        if (other.relation.uuid != relation.uuid) {
            return false
        }
        fun translateExpectedArgs(toTranslate: Array<VariableName>): Array<VariableName?> = toTranslate.map { equalityContext.uuidVariableMappings[it]!! }.toTypedArray()// okay to assert not null, because there are no free vars. So there shouldn't be unknown vars
        return expectedArgs.contentDeepEquals(translateExpectedArgs(other.expectedArgs))
    }

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()

    override fun evaluate(ev: EvalContext): Boolean {
        val args: Array<VariableValue?> = arrayOfNulls<VariableValue?>(expectedArgs.size)
        for ((i, expectedArg) in expectedArgs.withIndex()) {
            args[i] = ev.variables[expectedArg]
        }
        val notNullArgs: Array<VariableValue> = Array(args.size, init = {
            args[it]!!
        })
        return relation.implmentation.invoke(notNullArgs)
    }

    private fun predicateString(): String = "predicateNumber" + relation.hashCode().toString(16)

    override fun toMathML2(): String = """<mrow><mi>${predicateString()}</mi><mfenced>${expectedArgs.map { "<mrow>" + it.name + "</mrow>" }.reduceRight { s: String, acc: String -> s + acc }}</mfenced></mrow>"""

}

class And(override val left: FOLFormula, override val right: FOLFormula) : BinaryRelation(left, right) {
    override val operatorHashCode: Int
        get() = 31

    override fun toPrefixNotation(): String = """(and ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""

    override fun getOperatorAsMathML(): String = "&and;";
    override fun toMathML2(): String = """
        <mrow>
        ${left.toMathML2()}
        <mo>${getOperatorAsMathML()}</mo>
        ${right.toMathML2()}
        </mrow>
    """

    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) && right.evaluate(ev)
}

class Or(override val left: FOLFormula, override val right: FOLFormula) : BinaryRelation(left, right) {
    override val operatorHashCode: Int
        get() = 41

    override fun toPrefixNotation(): String = """(or ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""
    override fun getOperatorAsMathML(): String = "&or;"
    override fun evaluate(ev: EvalContext): Boolean = left.evaluate(ev) || right.evaluate(ev)
}

class Negation(val child: FOLFormula) : FOLFormula() {
    override fun hashCodeImpl(hashContext: HashContext): Int = 107*31 + child.hashCodeImpl(hashContext)

    override fun toPrefixNotation(): String = """(neg ${child.toPrefixNotation()})"""
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf(child)

    override fun toMathML2(): String = """
    <mrow>
    <mo>&not;</mo>
    ${child.toMathML2()}
    </mrow>"""

    override fun evaluate(ev: EvalContext): Boolean = !child.evaluate(ev)
}
typealias Not = Negation


class Implies(val given: FOLFormula, val result: FOLFormula) : BinaryRelation(given, result) {
    override val operatorHashCode: Int
        get() = 43

    override fun toPrefixNotation(): String = """(implies ${given.toPrefixNotation()} ${result.toPrefixNotation()})"""
    override fun getOperatorAsMathML(): String = "&rArr;"
    override fun evaluate(ev: EvalContext): Boolean = !given.evaluate(ev) || result.evaluate(ev)
}

class IFF(val one: FOLFormula, val two: FOLFormula) : BinaryRelation(one, two) {
    override val operatorHashCode: Int
        get() = 101

    override fun toPrefixNotation(): String = """(iff ${one.toPrefixNotation()} ${two.toPrefixNotation()})"""
    override fun getOperatorAsMathML(): String = "&hArr;"
    override fun evaluate(ev: EvalContext): Boolean = one.evaluate(ev) == two.evaluate(ev)
}

class ForAll(override val child: FOLFormula, override val varName: VariableName = VariableName()) : Quantifier(child, varName) {
    override val operatorHashCode: Int
        get() = 71*107

    override fun toPrefixNotation(): String = """(forall ${varName.name}  ${child.toPrefixNotation()})"""


    override val quantifierSymbol: String
        get() = "&forall;"

    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.all {
        val `var` = VariableValue(varName, it)
        ev.variables.put(varName, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varName)
        return res
    }
}

class Exists(override val child: FOLFormula, override val varName: VariableName = VariableName()) : Quantifier(child, varName) {
    override val operatorHashCode: Int
        get() = 73*103

    override fun toPrefixNotation(): String = """(exists ${varName.name} ${child.toPrefixNotation()})"""

    override val quantifierSymbol: String
        get() = "&exist;"

    override fun evaluate(ev: EvalContext): Boolean = ev.signature.elements.any {
        val `var` = VariableValue(varName, it)
        ev.variables.put(varName, `var`)
        val res = child.evaluate(ev)
        ev.variables.remove(varName)
        return res
    }
}


//todo technically this could all done with a relation atom
//todo maybe make atom class to abstract the redundancy here:
class EvaluatedAPatternException() : Exception("You tried to evaluate a pattern. Patterns cannot be evaluated by definition.")

sealed class PatternMember : FOLFormula(){
    val uuid = UUIDUtil.generateUUID()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PatternMember) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    fun defaultMatchSubstitution(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        val actualFormula = formula
        //todo duplication with AllowOnlyCertainVars and ForbidCertainVars
        if (this in matchSubstitutions.matchedPatterns) {
            //we already found this pattern elsewhere
            //need to check if same as elsewhere
            val expectedFormula = matchSubstitutions.matchedPatterns[this]!!
            //todo check that the order of parameters does not need reversing
            //todo this could still encounter vars from higher up the rewriting visitor
            return expectedFormula.sameAsImpl(actualFormula, EqualityContext(matchSubstitutions.variableSubstitutions))
        } else {
            matchSubstitutions.matchedPatterns[this] = formula;
            return true
        }
    }
}



class AllowAllVars : PatternMember(){
    override fun hashCodeImpl(hashContext: HashContext): Int = 103*101

    override fun toPrefixNotation(): String = """(Pattern matches anything Pattern#${super.hashCode()})"""


    override fun toMathML2(): String = """<mrow><mi>PatternMatchesAnything_PatternNumber_${(super.hashCode()).toString(36)}</mi></mrow>"""

    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        return defaultMatchSubstitution(formula, matchSubstitutions)
    }


    override fun sameAs(other: FOLFormula): Boolean {
        TODO("Need to map patterns check sameness")
    }

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        TODO("Need to translate variables to check sameness")
    }

    override fun evaluate(ev: EvalContext): Boolean {
        throw EvaluatedAPatternException()
    }

    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()
}

class AllowOnlyCertainVars(val vars: Array<VariableName>) : PatternMember() {
    /**
     * todo duplication with relation atom
     */
    override fun hashCodeImpl(hashContext: HashContext): Int {
        var hash = super.hashCode()
        vars.forEach {
            hash = 31*hash + hashContext.variableNumberMappings[it]!!
        }
        return hash
    }

    override fun toPrefixNotation(): String = """(Pattern Allows Vars: ${vars.map { it.name }.foldRight("",{s, acc -> s + " " + acc })}"""


    override fun toMathML2(): String = """<mrow><mi>PatternAllowsVars_PatternNumber_${(super.hashCode()).toString(36)}</mi><mfenced>${vars.map { "<mrow>" + it.name + "</mrow>" }.reduceRight { s: String, acc: String -> s + acc }}</mfenced></mrow>"""

    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        if(containsVarsOtherThan(formula, vars)){
            return false
        }
        return defaultMatchSubstitution(formula, matchSubstitutions)
    }

    override fun sameAs(other: FOLFormula): Boolean {
        TODO("Need to translate variables to check sameness")
    }

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        TODO("Need to translate variables to check sameness")
    }

    override fun evaluate(ev: EvalContext): Boolean {
        throw EvaluatedAPatternException()
    }
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()
}

class ForbidCertainVars(val vars: Array<VariableName>) : PatternMember() {
    override fun hashCodeImpl(hashContext: HashContext): Int {
        var hash = super.hashCode()
        vars.forEach {
            hash = 31*hash + hashContext.variableNumberMappings[it]!!
        }
        return hash
    }

    override fun toPrefixNotation(): String = """(Pattern Excludes Vars: ${vars.map { it.name }.foldRight("",{s, acc -> s + " " + acc })})"""

    override fun toMathML2(): String = """<mrow><mi>PatternExcludesVars_PatternNumber_${(super.hashCode()).toString(36)}</mi><mfenced>${vars.map { "<mrow>" + it.name + "</mrow>" }.reduceRight { s: String, acc: String -> s + acc }}</mfenced></mrow>"""

    override fun matches(formula: FOLFormula, matchSubstitutions: MatchSubstitutions): Boolean {
        if(vars.any{ containsVar(formula, it) }){
            return false
        }
        return defaultMatchSubstitution(formula, matchSubstitutions)
    }

    override fun sameAs(other: FOLFormula): Boolean {
        TODO("Need to translate variables to check sameness")
    }

    override fun sameAsImpl(other: FOLFormula, equalityContext: EqualityContext): Boolean {
        TODO("Need to translate variables to check sameness")
    }

    override fun evaluate(ev: EvalContext): Boolean {
        throw EvaluatedAPatternException()
    }
    override val subFormulas: Array<FOLFormula>
        get() = arrayOf()
}