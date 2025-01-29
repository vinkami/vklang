package vklang.parse.node

import vklang.lex.Token

class BracketNode(val left: Token, val innerNode: BaseNode, val right: Token): BaseNode(left.startPos, right.endPos) {
    override fun toString(): String {
        return "($left $innerNode $right)$callStr"
    }
}