package vklang.parse.node

import vklang.lex.Position

class IfNode(val condition: BaseNode, val action: BaseNode,
             val elif: MutableMap<BaseNode, BaseNode>, val elseAction: BaseNode?,
             startPos: Position, endPos: Position): BaseNode(startPos, endPos) {

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("(if $condition -> $action")

        for ((elifCondition, elifAction) in elif) {
            sb.append(" | $elifCondition -> $elifAction")
        }

        if (elseAction != null) {
            sb.append(" | $elseAction")
        }

        sb.append(")")
        sb.append(callStr)
        return sb.toString()
    }
}