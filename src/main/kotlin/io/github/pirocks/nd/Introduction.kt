package io.github.pirocks.nd

import io.github.pirocks.logic.*
import java.lang.IllegalArgumentException


/**
 * A for all introduction has a forall const leading to a conclusion. end result removes forall cconst and replaces with general
 * statement
 */
class ForAllIntroduction(val forAllVar : VariableName, val body: List<NDStatement>) : NDIntroductionStatement {
    init {
        if(body.isEmpty()){
            throw IllegalArgumentException("requires at least one statement in body")
        }
    }

    override val proves: FOLFormula
        get() = ForAll(body.last().proves,forAllVar)

    override val uses: Set<NDStatement>
        get() = body.toSet()

//    override fun verify(): Boolean {
//        TODO()
//    }
}

class ExistsIntroduction() : NDIntroductionStatement {
    override val proves: FOLFormula
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val uses: Set<NDStatement>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

//    override fun verify(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

class AndIntroduction(val left: NDStatement,val right: NDStatement) : NDIntroductionStatement {
    override val proves: FOLFormula
        get() = And(left.proves,right.proves)
    override val uses: Set<NDStatement>
        get() = setOf(left,right)

//    override fun verify(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

class OrIntroductionLeft(val left: NDStatement, val right: FOLFormula) : NDIntroductionStatement {
    override val proves: FOLFormula
        get() = Or(left.proves,right)
    override val uses: Set<NDStatement>
        get() = setOf(left)

//    override fun verify(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

class OrIntroductionRight(val left: FOLFormula, val right: NDStatement) : NDIntroductionStatement {
    override val proves: FOLFormula
        get() = Or(left,right.proves)
    override val uses: Set<NDStatement>
        get() = setOf(right)

//    override fun verify(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

class ImpliesIntroduction(val left : NDStatement,val right: NDStatement ) : NDIntroductionStatement {
    override val proves: FOLFormula
        get() = Implies(left.proves,right.proves)
    override val uses: Set<NDStatement>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

//    override fun verify(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

}


//class IFFIntroduction() : NDIntroductionStatement {
//}
//
//class NegationIntroduction() : NDIntroductionStatement {
//}
//
//class TruthIntroduction : NDIntroductionStatement {
//}
//
//class FalsityIntroduction(val contradictoryOne: NDStatement, val contradictoryTwo: NDStatement) : NDIntroductionStatement {
//
//}
//
///**
// * effectively copies a statement. needed for completeness reasons
// */
//class IDIntroduction(val toCopy: NDStatement) : NDIntroductionStatement {
//}