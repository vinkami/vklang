package vklang.exception

import vklang.lex.Position

class NotYourFaultError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)