package vklang.exception

import vklang.lex.Position

@Suppress("unused")
class DebugMessage(message: String, startPosition: Position, endPosition: Position): BaseError(message, startPosition, endPosition)