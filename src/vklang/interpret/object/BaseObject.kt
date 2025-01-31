package vklang.interpret.`object`

import vklang.exception.SyntaxError
import vklang.interpret.SymbolTable
import vklang.lex.Position

interface BaseObject {
    val type: String
    val value: Any
    val startPos: Position
    val endPos: Position
    val property: SymbolTable
    val boolVal: Boolean
        get() = true

    fun plus(other: BaseObject): BaseObject = throw SyntaxError("Can't add $this and $other", startPos, endPos)
    fun minus(other: BaseObject): BaseObject = throw SyntaxError("Can't subtract $this by $other", startPos, endPos)
    fun times(other: BaseObject): BaseObject = throw SyntaxError("Can't multiply $this and $other", startPos, endPos)
    fun divide(other: BaseObject): BaseObject = throw SyntaxError("Can't divide $this by $other", startPos, endPos)
    fun mod(other: BaseObject): BaseObject = throw SyntaxError("Can't get remainder of $this divided by $other", startPos, endPos)
    fun power(other: BaseObject): BaseObject = throw SyntaxError("Can't get $this to the power of $other", startPos, endPos)

    fun unaryPlus(): BaseObject = if (boolVal) NumberObj(1f, startPos, endPos) else NumberObj(0f, startPos, endPos)  // hi JS
    fun unaryMinus(): BaseObject = if (boolVal) NumberObj(-1f, startPos, endPos) else NumberObj(0f, startPos, endPos)

    fun equal(other: BaseObject): BaseObject = BoolObj(value == other.value, startPos, other.endPos)
    fun notEqual(other: BaseObject): BaseObject = BoolObj(value != other.value, startPos, other.endPos)
    fun lessEqual(other: BaseObject): BaseObject = throw SyntaxError("Can't compare $this and $other", startPos, other.endPos)
    fun greaterEqual(other: BaseObject): BaseObject = throw SyntaxError("Can't compare $this and $other", startPos, other.endPos)
    fun less(other: BaseObject): BaseObject = throw SyntaxError("Can't compare $this and $other", startPos, other.endPos)
    fun greater(other: BaseObject): BaseObject = throw SyntaxError("Can't compare $this and $other", startPos, other.endPos)

    fun and(other: BaseObject): BaseObject = BoolObj(boolVal && other.boolVal, startPos, other.endPos)
    fun or(other: BaseObject): BaseObject = BoolObj(boolVal || other.boolVal, startPos, other.endPos)
    fun not(): BaseObject = BoolObj(!boolVal, startPos, endPos)

    fun iter(): BaseObject = throw SyntaxError("Can't iterate over $this", startPos, endPos)
}
