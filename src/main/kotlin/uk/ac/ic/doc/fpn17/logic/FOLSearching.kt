package uk.ac.ic.doc.fpn17.logic

fun containsVar(formula: FOLFormula, varName: VariableName):Boolean{
    var res = false;
    object :RewritingVisitor(){
        override fun rewritePredicateAtom(toRewrite: PredicateAtom): FOLFormula {
            res = toRewrite.expectedArgs.any { it == varName }
            return super.rewritePredicateAtom(toRewrite)
        }
    }
    return res;
}

fun containsVarsOtherThan(formula: FOLFormula, whitelist: Array<VariableName>):Boolean{
    var res = false;
    val whitelistSet = whitelist.toHashSet()
    object :RewritingVisitor(){
        override fun rewritePredicateAtom(toRewrite: PredicateAtom): FOLFormula {
            res = res || toRewrite.expectedArgs.any { it !in whitelistSet }
            return super.rewritePredicateAtom(toRewrite)
        }
    }
    return res;
}