package vklang.exception

import vklang.lex.Position

class LoopInterrupt(startPos: Position, endPos: Position): BaseError("Loop interrupted", startPos, endPos)