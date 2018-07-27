package uk.ac.ic.doc.fpn17

import uk.ac.ic.doc.fpn17.logic.*
import uk.ac.ic.doc.fpn17.util.UUIDUtil

fun main(args: Array<String>) {
    val forAllVar = VariableName()
    val existsVar = VariableName()
    val predicate = PredicateAtom(Predicate({ false }), arrayOf(forAllVar, existsVar))
    val baseExpression = And(Or(IFF(predicate, True()), False()), Implies(True(), False()))
    println(Exists(ForAll(baseExpression,forAllVar),existsVar).toHtml())
//    println((PredicateAtom(Predicate({ arrayOfVariables -> false }), arrayOf(VariableName(),VariableName()) )).toHtml())
}