package vklang.exception

import vklang.lex.Position

class IllegalCharError(value: String, startPos: Position, endPos: Position): BaseError(value, startPos, endPos)