package vklang.exception

import vklang.lex.Position

class SyntaxError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)