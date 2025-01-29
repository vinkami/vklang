package vklang.exception

import vklang.lex.Position

abstract class BaseError(message: String, startPos: Position, endPos: Position): BaseLangException(message, startPos, endPos)