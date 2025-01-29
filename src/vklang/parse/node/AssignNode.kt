package vklang.parse.node

import vklang.lex.Position

class AssignNode(val iden: IdenNode, val value: BaseNode, val mutable: Boolean, startPos: Position): BaseNode(startPos, value.endPos) {
    override fun toString(): String {
        return "(${if (mutable) "var" else "val"} $iden = $value)$callStr"
    }
}