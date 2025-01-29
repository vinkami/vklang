package vklang.parse.node

import vklang.lex.Position
import vklang.lex.Token

class FuncNode(val name: Token, val params: List<ParamNode>, val returnType: IdenNode?, val body: BaseNode, startPos: Position, endPos: Position): BaseNode(startPos, endPos) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("(fun $name(")
        sb.append(params.joinToString(", "))
        sb.append(")")
        if (returnType != null) sb.append(": $returnType")
        sb.append(" $body)")
        sb.append(callStr)
        return sb.toString()
    }
}