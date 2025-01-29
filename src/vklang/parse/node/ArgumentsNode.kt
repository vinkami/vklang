package vklang.parse.node

import vklang.lex.Position
import vklang.lex.Token

class ArgumentsNode(val args: List<BaseNode>, val kwargs: Map<Token, BaseNode>, startPos: Position, endPos: Position): BaseNode(startPos, endPos) {
    override fun toString(): String {
        return "(args: $args, kwargs: $kwargs)$callStr"
    }
}