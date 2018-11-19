package io.github.pirocks.logic

/**
 * TODO needs testing
 */
fun containsVar(formula: FOLFormula, varName: VariableName):Boolean{
    var res = false;
    object : RewritingVisitor(){
        override fun rewritePredicateAtom(toRewrite: RelationAtom): FOLFormula {
            res = toRewrite.expectedArgs.any { it == varName }
            return super.rewritePredicateAtom(toRewrite)
        }
    }.rewrite(formula)
    return res;
}

/**
 * TODO needs testing
 */
fun containsVarsOtherThan(formula: FOLFormula, whitelist: Array<VariableName>):Boolean{
    var res = false;
    val whitelistSet = whitelist.toHashSet()
    object : RewritingVisitor(){
        override fun rewritePredicateAtom(toRewrite: RelationAtom): FOLFormula {
            res = res || toRewrite.expectedArgs.any { it !in whitelistSet }
            return super.rewritePredicateAtom(toRewrite)
        }
    }.rewrite(formula);
    return res;
}