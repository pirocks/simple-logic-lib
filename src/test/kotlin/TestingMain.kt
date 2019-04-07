import io.github.pirocks.logic.*


class TestingMain{
companion object {

    @JvmStatic
    fun main(args: Array<String>) {
        val forAllVar = VariableName()
        val existsVar = VariableName()
        val predicate = PredicateAtom(Predicate.newUnEvaluatableRelation(), arrayOf(forAllVar, existsVar))
        val baseExpression = And(Or(IFF(predicate, True()), False()), Implies(True(), False()))
        println(Exists(ForAll(baseExpression, forAllVar), existsVar).toHtml())
    }
}

}
