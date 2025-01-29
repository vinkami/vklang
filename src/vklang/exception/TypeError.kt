package vklang.exception

import vklang.lex.Position

class TypeError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)