package io.github.pirocks.parsers

import io.github.pirocks.logic.And
import io.github.pirocks.logic.FOLFormula
import io.github.pirocks.logic.Implies
import io.github.pirocks.logic.Or
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.UnbufferedCharStream
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.File

class DefaultFOLParser : FOLParser{

    private class AntlrVisitor : DefaultBaseVisitor<FOLFormula>() {


        override fun visitAnd(ctx: DefaultParser.AndContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return And(left, right)
        }

        override fun visitOr(ctx: DefaultParser.OrContext): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return Or(left, right)//todo duplication
        }

        override fun visitNot(ctx: DefaultParser.NotContext?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitImplies(ctx: DefaultParser.ImpliesContext?): FOLFormula {
            val left = visit(ctx.formula(0))
            val right = visit(ctx.formula(1))
            return Implies(left, right)//todo duplication
        }

        override fun visitIff(ctx: DefaultParser.IffContext?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitFalse_(ctx: DefaultParser.False_Context?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitTrue_(ctx: DefaultParser.True_Context?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitForall(ctx: DefaultParser.ForallContext?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitExists(ctx: DefaultParser.ExistsContext?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitPredicateAtom(ctx: DefaultParser.PredicateAtomContext?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visit(p0: ParseTree?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitChildren(p0: RuleNode?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitErrorNode(p0: ErrorNode?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun visitTerminal(p0: TerminalNode?): FOLFormula {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    override fun parse(toParse: String): FOLFormula {
        val lexer = DefaultLexer(CharStreams.fromString(toParse))
        val parser = DefaultParser(CommonTokenStream(lexer))
        val antlrTree = parser.formula()
        return AntlrVisitor().visit(antlrTree)
    }


}
