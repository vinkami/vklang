package vklang.parse.node

class PropAccessNode(val parent: BaseNode, val property: IdenNode): BaseNode(parent.startPos, property.endPos) {
    override fun toString(): String {
        return "$parent.$property$callStr"
    }
}