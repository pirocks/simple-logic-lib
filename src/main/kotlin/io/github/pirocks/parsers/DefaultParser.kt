package io.github.pirocks.parsers

import io.github.pirocks.logic.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ErrorNode

class PrefixNotationFOLParser : FOLParser{

    private class AntlrVisitor : PrefixNotationBaseVisitor<FOLFormula>() {
        private val variableNameMap = mutableMapOf<String,VariableName>()

        fun getVarFromName(name : String): VariableName {
            return variableNameMap[name] ?: let {
                variableNameMap[name] = VariableName(name = name)
                return variableNameMap[name]!!
            }
        }

        override fun visitAnd(ctx: PrefixNotationParser.AndContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return And(left, right)
        }

        override fun visitOr(ctx: PrefixNotationParser.OrContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return Or(left, right)//todo duplication
        }

        override fun visitNot(ctx: PrefixNotationParser.NotContext): FOLFormula {
            val child = visit(ctx.formula())
            return Not(child)
        }

        override fun visitImplies(ctx: PrefixNotationParser.ImpliesContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return Implies(left, right)//todo duplication
        }

        override fun visitIff(ctx: PrefixNotationParser.IffContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return IFF(left, right)//todo duplication
        }

        override fun visitFalse_(ctx: PrefixNotationParser.False_Context): FOLFormula = False()

        override fun visitTrue_(ctx: PrefixNotationParser.True_Context): FOLFormula = True()

        override fun visitForall(ctx: PrefixNotationParser.ForallContext): FOLFormula {
            val varName = ctx.IDENT().text
            val child = visit(ctx.formula())
            return ForAll(child,varName = getVarFromName(varName))
        }

        override fun visitExists(ctx: PrefixNotationParser.ExistsContext): FOLFormula {
            val varName = ctx.IDENT().text
            val child = visit(ctx.formula())
            return Exists(child,varName = getVarFromName(varName))
        }

        override fun visitPredicateAtom(ctx: PrefixNotationParser.PredicateAtomContext): FOLFormula {
            val predicateName = ctx.IDENT().first().symbol.text
            val args = ctx.IDENT().drop(1).map { getVarFromName(it.symbol.text) }
            return PredicateAtom(Predicate(name = predicateName, implmentation = {
                throw IllegalStateException("Predicate cannot be evaluated b/c predicate was parsed")
            }), args.toTypedArray() )
        }

        override fun visitErrorNode(p0: ErrorNode?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun parse(toParse: String): FOLFormula {
        val lexer = PrefixNotationLexer(CharStreams.fromString(toParse))
        val parser = PrefixNotationParser(CommonTokenStream(lexer))
        val antlrTree = parser.formula()
        return AntlrVisitor().visit(antlrTree)
    }


}
