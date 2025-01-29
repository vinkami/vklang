package vklang.parse.node

import vklang.lex.Position
import vklang.lex.Token

class ClassNode(val name: Token, val initParams: List<ParamNode>, val parent: IdenNode?, val body: BaseNode, startPos: Position, endPos: Position): BaseNode(startPos, endPos) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("(class $name")
        if (initParams.isNotEmpty()) {
            sb.append("(")
            sb.append(initParams.joinToString(", "))
            sb.append(")")
        }
        if (parent != null) sb.append(": $parent")
        sb.append(" $body)")
        sb.append(callStr)
        return sb.toString()
    }
}