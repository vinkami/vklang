package vklang.parse.node

import vklang.lex.Token

class BinOpNode(val left: BaseNode, val op: Token, val right: BaseNode): BaseNode(left.startPos, right.endPos) {
    override fun toString(): String {
        return "($left $op $right)$callStr"
    }
}