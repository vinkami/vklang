package vklang.exception

import vklang.lex.Position

class AttributeError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)