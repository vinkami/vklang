package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.lex.Position
import kotlin.math.pow

class NumberObj(override val value: Float, override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Number"
    override val property = SymbolTable(null, isRoot=false)
    override val boolVal
        get() = value != 0f

    override fun toString(): String {
        return value.toString()
    }

    override fun plus(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value + other.value, other.startPos, other.endPos)
        if (other is StringObj) return StringObj(value.toString() + other.value, other.startPos, other.endPos)
        if (other is BoolObj) return NumberObj(value + other.intObj.value, other.startPos, other.endPos)
        return super.plus(other)
    }

    override fun minus(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value - other.value, other.startPos, other.endPos)
        if (other is BoolObj) return NumberObj(value - other.intObj.value, other.startPos, other.endPos)
        return super.minus(other)
    }

    override fun times(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value * other.value, other.startPos, other.endPos)
        if (other is BoolObj) return NumberObj(value * other.intObj.value, other.startPos, other.endPos)
        return super.times(other)
    }

    override fun divide(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value / other.value, other.startPos, other.endPos)
        return super.divide(other)
    }

    override fun mod(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value % other.value, other.startPos, other.endPos)
        return super.mod(other)
    }

    override fun power(other: BaseObject): BaseObject {
        if (other is NumberObj) return NumberObj(value.pow(other.value), other.startPos, other.endPos)
        return super.power(other)
    }

    override fun unaryPlus(): BaseObject {
        return NumberObj(+value, startPos, endPos)
    }

    override fun unaryMinus(): BaseObject {
        return NumberObj(-value, startPos, endPos)
    }

    override fun lessEqual(other: BaseObject): BaseObject {
        if (other is NumberObj) return BoolObj(value <= other.value, startPos, other.endPos)
        return super.lessEqual(other)
    }

    override fun greaterEqual(other: BaseObject): BaseObject {
        if (other is NumberObj) return BoolObj(value >= other.value, startPos, other.endPos)
        return super.greaterEqual(other)
    }

    override fun less(other: BaseObject): BaseObject {
        if (other is NumberObj) return BoolObj(value < other.value, startPos, other.endPos)
        return super.less(other)
    }

    override fun greater(other: BaseObject): BaseObject {
        if (other is NumberObj) return BoolObj(value > other.value, startPos, other.endPos)
        return super.greater(other)
    }
}