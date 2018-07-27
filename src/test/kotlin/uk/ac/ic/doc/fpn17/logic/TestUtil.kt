package uk.ac.ic.doc.fpn17.logic

fun equalsAndSameAs(formula1:FOLFormula,formula2:FOLFormula):Boolean{
    return formula1.equals(formula2) && formula1.sameAs(formula2)
}