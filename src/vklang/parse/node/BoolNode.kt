package vklang.parse.node

import vklang.lex.Token

class BoolNode(val token: Token): BaseNode(token.startPos, token.endPos) {
    override fun toString(): String {
        return token.toString()
    }
}