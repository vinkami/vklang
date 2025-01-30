package vklang.interpret

import vklang.Constant
import vklang.interpret.`object`.function.BuiltinFunc
import vklang.exception.*
import vklang.interpret.`object`.*
import vklang.interpret.`object`.function.Parameter
import vklang.lex.Position
import vklang.lex.Token
import vklang.lex.TokenType.*
import vklang.parse.node.*

class Interpreter(private val globalNode: BaseNode, private val globalst: SymbolTable) {
    fun interpret(): BaseError? {
        try {
            interpret(globalNode, globalst)
        } catch (e: BaseError) {
            return e
        }
        return null
    }

    private fun interpretNoInterrupt(node: BaseNode, st: SymbolTable): InterpretResult {
        val res = interpret(node, st)

        if (res.hasInterrupt) {
            throw SyntaxError("Interrupt should not occur here", node.startPos, node.endPos)
        }

        return res
    }

    private fun interpret(node: BaseNode, st: SymbolTable): InterpretResult {
        node::class.simpleName ?: return InterpretResult(UnknownNodeError(node))

        var res: InterpretResult = when (node) {
            is NumberNode -> interpretNumber(node)
            is StringNode -> InterpretResult(StringObj(node.value, node.startPos, node.endPos))
            is BoolNode -> InterpretResult(BoolObj(node.token.type == TRUE, node.startPos, node.endPos))
            is BinOpNode -> interpretBinOp(node, st)
            is UnaryOpNode -> interpretUnaryOp(node, st)
            is IdenNode -> interpretIden(node, st)
            is AssignNode -> interpretAssign(node, st)
            is ListNode -> interpretList(node, st)
            is DictNode -> interpretDict(node, st)
            is BracketNode -> interpretBracket(node, st)
            is NullNode -> InterpretResult(NullObj(node.startPos, node.endPos))
            is IfNode -> interpretIf(node, st)
            is LoopNode -> interpretLoop(node, st)
            is InterruptNode -> interpretInterrupt(node, st)
            is ProcedureNode -> interpretProcedural(node, st)
            is FuncNode -> interpretFuncCreation(node, st)
            is ClassNode -> interpretClassCreation(node, st)
            is PropAccessNode -> interpretPropAccess(node, st)

            is ArgumentsNode -> throw NotYourFaultError("ArgumentsNode should not be interpreted", node.startPos, node.endPos)
            else -> throw UnknownNodeError(node)
        }

        if (node.call != null) {
            val args = node.call!!.args
            val kwargs = node.call!!.kwargs
            val localst = st.bornChild()
            val startPos = node.startPos
            val endPos = node.endPos

            res = when (res.obj) {
                is FuncObj -> interpretFunc((res.obj as FuncObj).node, args, kwargs, localst, startPos, endPos)
                is BuiltinFunc -> interpretBultinFunc(res.obj as BuiltinFunc, args, kwargs, localst, startPos, endPos)
                is ClassObj -> interpretClass((res.obj as ClassObj).node, args, kwargs, localst, startPos, endPos)
                else -> throw TypeError("${res.obj::class.simpleName} is not callable", startPos, endPos)
            }
        }

        return res
    }

    private fun interpretInterrupt(node: InterruptNode, st: SymbolTable): InterpretResult {
        val interrupt = node.type
        val obj = interpretNoInterrupt(node.innerNode, st).obj
        return InterpretResult(obj, interrupt)
    }

    private fun interpretNumber(node: NumberNode): InterpretResult {
        val valueString = node.value
        try {
            val value = valueString.toFloat()
            return InterpretResult(NumberObj(value, node.startPos, node.endPos))
        } catch (e: NumberFormatException) {
            throw SyntaxError("Invalid number: $valueString", node.startPos, node.endPos)
        }
    }

    private fun interpretBinOp(node: BinOpNode, st: SymbolTable): InterpretResult {
        if (node.op.type in Constant.definitiveOp) {
            // Variable assignment
            if (node.left !is IdenNode) throw SyntaxError("Invalid assignment", node.startPos, node.endPos)
            val name = node.left.name
            val value = interpretNoInterrupt(node.right, st).obj
            val ogValue =
                st.get(name) ?: throw NameError("Unknown variable $name", node.left.startPos, node.left.endPos)

            when (node.op.type) {
                ASSIGN -> st.reassign(name, value)
                PLUS_ASSIGN -> st.reassign(name, ogValue.plus(value))
                MINUS_ASSIGN -> st.reassign(name, ogValue.minus(value))
                MULTIPLY_ASSIGN -> st.reassign(name, ogValue.times(value))
                DIVIDE_ASSIGN -> st.reassign(name, ogValue.divide(value))
                MODULO_ASSIGN -> st.reassign(name, ogValue.mod(value))
                POWER_ASSIGN -> st.reassign(name, ogValue.power(value))
                else -> throw NotYourFaultError(
                    "Invalid assignment operator ${node.op.type}",
                    node.op.startPos,
                    node.op.endPos
                )  // No other TT are allowed from parser
            }
            return InterpretResult(NullObj(node.startPos, node.endPos))

        } else {
            // Normal calculation
            val left = interpretNoInterrupt(node.left, st).obj
            val right = interpretNoInterrupt(node.right, st).obj

            val obj = when (node.op.type) {
                PLUS -> left.plus(right)
                MINUS -> left.minus(right)
                MULTIPLY -> left.times(right)
                DIVIDE -> left.divide(right)
                MODULO -> left.mod(right)
                POWER -> left.power(right)

                EQUAL -> left.equal(right)
                NOT_EQUAL -> left.notEqual(right)
                LESS_EQUAL -> left.lessEqual(right)
                GREATER_EQUAL -> left.greaterEqual(right)
                LESS -> left.less(right)
                GREATER -> left.greater(right)

                AND -> left.and(right)
                OR -> left.or(right)

                else -> throw UnknownNodeError(node)
            }

            return InterpretResult(obj)
        }
    }

    private fun interpretUnaryOp(node: UnaryOpNode, st: SymbolTable): InterpretResult {
        val innerObj = interpretNoInterrupt(node.innerNode, st).obj

        val obj =  when (node.op.type) {
            PLUS -> innerObj.unaryPlus()
            MINUS -> innerObj.unaryMinus()
            NOT -> innerObj.not()
            else -> throw UnknownNodeError(node)
        }

        return InterpretResult(obj)
    }

    private fun interpretIden(node: IdenNode, st: SymbolTable): InterpretResult {
        return InterpretResult(st.get(node.name) ?: throw NameError("Undefined name \"${node.name}\"", node.startPos, node.endPos))
    }

    private fun interpretAssign(node: AssignNode, st: SymbolTable): InterpretResult {
        val value = interpretNoInterrupt(node.value, st).obj

        st.set(node.iden.name, value, node.mutable)
        return InterpretResult(NullObj(node.startPos, node.endPos))
    }

    private fun interpretList(node: ListNode, st: SymbolTable): InterpretResult {
        val list = mutableListOf<BaseObject>()
        for (item in node.nodes) {
            list.add(interpretNoInterrupt(item, st).obj)
        }
        return InterpretResult(ListObj(list, node.startPos, node.endPos))
    }

    private fun interpretDict(node: DictNode, st: SymbolTable): InterpretResult {
        val dict = mutableMapOf<BaseObject, BaseObject>()
        for ((key, value) in node.dict) {
            dict[StringObj(key.value, key.startPos, key.endPos)] = interpretNoInterrupt(value, st).obj
        }
        return InterpretResult(DictObj(dict, node.startPos, node.endPos))
    }

    private fun interpretBracket(node: BracketNode, st: SymbolTable): InterpretResult {
        return interpret(node.innerNode, st)
    }

    private fun interpretIf(node: IfNode, st: SymbolTable): InterpretResult {
        val localst = st.bornChild()

        val cond = interpretNoInterrupt(node.condition, localst).obj
        if (cond.boolVal) {
            return interpret(node.action, localst)
        }

        for ((elifCondNode, elifActionNode) in node.elif) {
            val elifCond = interpretNoInterrupt(elifCondNode, localst).obj
            if (elifCond.boolVal) {
                return interpret(elifActionNode, localst)
            }
        }

        if (node.elseAction != null) {
            return interpret(node.elseAction, localst)
        }

        return InterpretResult(NullObj(node.startPos, node.endPos))
    }

    // TODO: Add for loop
    private fun interpretLoop(node: LoopNode, globalst: SymbolTable): InterpretResult {
        val st = globalst.bornChild()
        var finalObj: BaseObject = NullObj(node.startPos, node.endPos)

        var complete = true
        if (node.loopTokenType == WHILE) {
            var cond = interpretNoInterrupt(node.condition, st).obj

            while (cond.boolVal) {
                val res = interpret(node.mainAction, st)
                if (res.interrupt == BREAK) {
                    complete = false
                    res.clearInterrupt()  // just in case; not really useful for now
                    break
                }
                if (res.interrupt == RETURN) {
                    return res
                }
                finalObj = res.obj
                cond = interpretNoInterrupt(node.condition, st).obj
            }

//        } else if (node.loopTokenType == FOR) {
//            if (node.condition !is ArgumentsNode) throw NotYourFaultError("Invalid for loop in the condition", node.startPos, node.endPos)
//            val variable = node.condition.args[0]
//            val iterable = interpretNoInterrupt(node.condition.args[1], st).obj

        } else throw NotYourFaultError("Unknown loop token type: ${node.loopTokenType}", node.startPos, node.endPos)

        if (complete) {
            val compRes = interpret(node.compAction ?: NullNode(node.startPos, node.endPos), st).also { if (it.interrupt != null) return it }
            if (compRes.hasObject) finalObj = compRes.obj
        } else {
            val incompRes = interpret(node.incompAction ?: NullNode(node.startPos, node.endPos), st).also { if (it.interrupt != null) return it }
            if (incompRes.hasObject) finalObj = incompRes.obj
        }

        return InterpretResult(finalObj)
    }

    private fun interpretProcedural(node: ProcedureNode, st: SymbolTable): InterpretResult {
        var finalObj: BaseObject = NullObj(node.startPos, node.endPos)
        for (procedure in node.procedures) {
            finalObj = interpret(procedure, st).also { if (it.interrupt != null) return it }.obj
        }
        return InterpretResult(finalObj)
    }

    private fun interpretFuncCreation(node: FuncNode, st: SymbolTable): InterpretResult {
        val obj = FuncObj(node)
        st.set(node.name.value, obj, false)
        return InterpretResult(obj)
    }

    private fun interpretFunc(node: FuncNode, args: List<BaseNode>, kwargs: Map<Token, BaseNode>, st: SymbolTable, startPos: Position, endPos: Position): InterpretResult {
        val params = node.params.map { convertParamNode(it, st) }
        setParams(params, args, kwargs, st, startPos, endPos)
        val res = interpret(node.body, st)
        return res.clearInterrupt()  // remove possible return interrupt
    }

    private fun interpretBultinFunc(obj: BuiltinFunc, args: List<BaseNode>, kwargs: Map<Token, BaseNode>, st: SymbolTable, startPos: Position, endPos: Position): InterpretResult {  // BuiltinFunc doesn't have positional information
        setParams(obj.parameters, args, kwargs, st, startPos, endPos)
        return InterpretResult(obj(st, startPos, endPos))
    }

    private fun interpretClassCreation(node: ClassNode, st: SymbolTable): InterpretResult {
        val obj = ClassObj(node)
        st.set(node.name.value, obj, false)
        return InterpretResult(obj)
    }

    private fun interpretClass(node: ClassNode, args: List<BaseNode>, kwargs: Map<Token, BaseNode>, st: SymbolTable, startPos: Position, endPos: Position): InterpretResult {
        val thisst = setParams(node.initParams.map { convertParamNode(it, st) }, args, kwargs, st, startPos, endPos)

        // inheritance
        node.parent?.let {
            val parent = interpretNoInterrupt(it, thisst).obj
            thisst.set("that", parent, false)
        }

        // set class methods, constants, etc. and does the init work
        interpretNoInterrupt(node.body, thisst)
        val thisObj = CustomObj(node.name.value, thisst, node.startPos, node.endPos)
        thisst.set("this", thisObj, false)

        return InterpretResult(thisObj)
    }

    private fun convertParamNode(node: ParamNode, st: SymbolTable): Parameter {
        val type = node.type?.name
        val default = node.default?.let { interpret(node.default, st).obj }
        return Parameter(node.name, type, default, node.variable, node.kwvariable)
    }

    private fun setParams(params: List<Parameter>, args: List<BaseNode>, kwargs: Map<Token, BaseNode>, st: SymbolTable, startPos: Position, endPos: Position): SymbolTable {
        val varArg = params.find { it.variable }
        val varKwarg = params.find { it.kwvariable }

        // normal arguments
        val modifier = (if (varArg != null) 1 else 0) + (if (varKwarg != null) 1 else 0)
        val argParamNumber = params.count { it.default == null } - modifier
        if (args.size < argParamNumber) throw TypeError("Not enough arguments", startPos, endPos)
        if (varArg != null) {
            val varArgsIndex = params.indexOf(varArg)

            // first part of arguments
            args.subList(0, varArgsIndex).forEachIndexed { i, it ->  st.set(params[i].name, interpret(it, st).obj, false) }

            // variable arguments
            val argvEnd = args.size - argParamNumber + varArgsIndex
            val argv = args.subList(varArgsIndex, argvEnd).map { interpret(it, st).obj }
            st.set(varArg.name, ListObj(argv, startPos, endPos), false)

            // last part of arguments
            args.subList(argvEnd, args.size).forEachIndexed { i, it ->  st.set(params[i + varArgsIndex + 1].name, interpret(it, st).obj, false) }
        } else {
            if (args.size > params.size) throw TypeError("Too many arguments", startPos, endPos)
            args.forEachIndexed { i, it ->  st.set(params[i].name, interpret(it, st).obj, false)}
        }

        // keyword arguments
        if (varKwarg != null) {
            // normal keyword arguments
            kwargs.filter { it.key.value in params.map { param -> param.name } - varKwarg.name }.forEach { st.set(it.key.value, interpret(it.value, st).obj, false) }

            // variable kw arguments
            val kwargMap = mutableMapOf<String, BaseObject>()
            kwargs.filter { it.key.value !in params.map { param -> param.name } - varKwarg.name }.forEach { kwargMap[it.key.value] = interpret(it.value, st).obj }
            st.set(varKwarg.name, MapObj(kwargMap, startPos, endPos), false)
        } else {
            kwargs.forEach { st.set(it.key.value, interpret(it.value, st).obj, false) }
        }

        // check for any missing
        for (param in params) {
            if (st.contain(param.name)) continue
            if (param.default != null) st.set(param.name, param.default, false)
            else throw TypeError("Missing argument: ${param.name}", startPos, endPos)
        }

        return st
    }

    private fun interpretPropAccess(node: PropAccessNode, st: SymbolTable): InterpretResult {
        val obj = interpretNoInterrupt(node.parent, st).obj

        val prop = obj.property.getLocal(node.property.name)
            ?: obj.property.getLocal("that")?.property?.getLocal(node.property.name)  // class inheritance
            ?: throw AttributeError("Property \"${node.property.name}\" does not exist", node.startPos, node.endPos)

        return InterpretResult(prop)
    }
}