package vklang.parse.node

import vklang.lex.Position
import vklang.lex.Token

class NullNode: BaseNode {
    constructor(token: Token): super(token.startPos, token.endPos)
    constructor(startPos: Position, endPos: Position): super(startPos, endPos)

    override fun toString(): String {
        return "null$callStr"
    }
}