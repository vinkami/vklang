package vklang.exception

import vklang.lex.Position

class AssignmentError(message: String, startPos: Position, endPos: Position): BaseError(message, startPos, endPos)