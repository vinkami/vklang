package vklang.parse.node

import vklang.lex.Token

class UnaryOpNode(val op: Token, val innerNode: BaseNode): BaseNode(op.startPos, innerNode.endPos) {
    override fun toString(): String {
        return "($op $innerNode)$callStr"
    }
}