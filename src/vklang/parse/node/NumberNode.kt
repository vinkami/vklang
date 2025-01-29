package vklang.parse.node

import vklang.lex.Token

class NumberNode(token: Token): BaseNode(token.startPos, token.endPos) {
    val value = token.value

    override fun toString(): String {
        return value + callStr
    }
}