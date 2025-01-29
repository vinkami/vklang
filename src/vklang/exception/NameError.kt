package vklang.exception

import vklang.lex.Position

class NameError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)