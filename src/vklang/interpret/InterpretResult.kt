package vklang.interpret

import vklang.exception.NotYourFaultError
import vklang.interpret.`object`.BaseObject
import vklang.interpret.`object`.NullObj
import vklang.lex.TokenType

class InterpretResult(val obj: BaseObject, var interrupt: TokenType? = null) {
    // interrupt: TT.BREAK / TT.RETURN

    init {
        if (interrupt !in listOf(null, TokenType.BREAK, TokenType.RETURN)) throw NotYourFaultError("Invalid interrupt type: $interrupt", obj.startPos, obj.endPos)
    }

    val hasInterrupt: Boolean
        get() = interrupt != null

    val hasObject: Boolean
        get() = obj !is NullObj

    fun clearInterrupt() = apply {
        interrupt = null
    }
}