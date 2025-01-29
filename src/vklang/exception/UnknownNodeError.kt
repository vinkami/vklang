package vklang.exception

import vklang.parse.node.BaseNode

class UnknownNodeError(node: BaseNode): BaseError(node::class.simpleName.toString(), node.startPos, node.endPos)