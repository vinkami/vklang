package vklang.parse

import vklang.Constant
import vklang.exception.*
import vklang.lex.Position
import vklang.lex.Token
import vklang.lex.TokenType
import vklang.lex.TokenType.*
import vklang.parse.node.*


class Parser(private val tokens: List<Token>) {
    private var pos = -1

    private val currentToken: Token
        get() = tokens[pos]
    private val currentType: TokenType
        get() = currentToken.type
    private val currentStartPos: Position
        get() = currentToken.startPos
    private val currentEndPos: Position
        get() = currentToken.endPos

    private val nextNonSpaceToken: Token
        get() = tokens.subList(pos + 1, tokens.size).firstOrNull { it.type !in listOf(SPACE, LINEBREAK) } ?: tokens.last()
    private val nextType: TokenType
        get() = nextNonSpaceToken.type

    init {advance()}

    private fun advance() {
        if (pos == tokens.size - 1) throw SyntaxError("Unexpected end of file", currentStartPos, currentEndPos)
        pos++
    }

    private fun skipSpace() {
        while (listOf(SPACE, LINEBREAK).contains(currentType)) advance()
    }

    /**
     * Advance, Skip Space
     */
    private tailrec fun ass(n: Int = 1) {
        advance()
        skipSpace()
        return if (n == 1) Unit else ass(n-1)
    }

    fun parse(): BaseNode {
        val procedures = mutableListOf<BaseNode>()
        val startPos = currentStartPos

        skipSpace()
        while (true) {
            val procedure = parseOnce()
            if (procedure is ArgumentsNode) throw SyntaxError("Nothing to be called", procedure.startPos, procedure.endPos)
            else procedures += procedure
            if (currentType == EOF) break
            ass()
        }
        val endPos = currentEndPos

        if (procedures.size == 1 || procedures.size == 2) return procedures[0]  // 1: Only NullNode from EOF; 2: Only one procedure
        return ProcedureNode(procedures, startPos, endPos)
    }

    private fun parseOnce(): BaseNode {
        var currentProcedure = when (currentType) {
            NUMBER -> NumberNode(currentToken)
            STRING -> StringNode(currentToken)
            IDENTIFIER -> parseIden()
            TRUE, FALSE -> BoolNode(currentToken)
            PLUS, MINUS, NOT -> parseUnaryOp()
            VAR, VAL, DOLLAR -> parseAssign()
            in Constant.bracket.keys -> parseBracket()
            IF -> parseIf()
            WHILE, FOR -> parseLoop()
            BREAK, RETURN -> parseInterrupt()
            FUNC -> parseFuncDef()
            CLASS -> parseClass()
            EOF -> NullNode(currentToken)
            else -> throw SyntaxError("Unexpected token $currentType", currentStartPos, currentEndPos)
        }

        currentProcedure = checkObjFollower(currentProcedure)

        return currentProcedure
    }

    private fun checkObjFollower(node: BaseNode): BaseNode {
        return processBinOp(0, node)
    }

    private fun handleCall(node: BaseNode): BaseNode {
        // Parantheses used for math expression is parsed in parseBracket()
        var paranCount = 1
        val start = pos
        val startToken = currentToken
        while (paranCount > 0) {
            ass()
            val tt = currentType
            if (tt == EOF) throw SyntaxError("Script ended when expecting a )", currentStartPos, currentEndPos)
            else if (tt == L_PAREN) paranCount++
            else if (tt == R_PAREN) paranCount--
        }

        val endToken = currentToken
        val eof = Token(EOF, "EOF", endToken.startPos, endToken.endPos)
        val innerTokens = tokens.subList(start + 1, pos) + eof
        val (args, kwargs) = Parser(innerTokens).generateArguments()
        node.call = ArgumentsNode(args, kwargs, startToken.startPos, currentEndPos)
        return node
    }

    /**
     * Pratt parsing
     * Used by parseBracket() and checkCallGet() so it's broken down into a separate function
     * There's no parseBinOp because any binary operation must be started from an object and will be checked by checkCallGet()
     *
     * @param minBP Minimum binding power; should be 0 when called from outside and varies according to Constant.bindingPower when called by processBinOp()
     * @param currentNode the node to start parsing at
     */
    private fun processBinOp(minBP: Int, currentNode: BaseNode): BaseNode {
        var lhs = currentNode
        while (nextType in Constant.binaryOps) {
            if (pos == tokens.size - 1) break
            if (nextType == DOT) {  // access property
                ass()
                lhs = parseProp(lhs)
                continue
            } else if (nextType == L_PAREN) {  // make call
                ass()
                lhs = handleCall(lhs)
                continue
            }

            val (leftBP, rightBP) = Constant.bindingPower[nextType]!!
            if (leftBP < minBP) break
            val op = nextNonSpaceToken
            ass(2)

            var rhs = when (currentType) {
                L_PAREN, L_BRACKET -> parseBracket()
                NUMBER -> NumberNode(currentToken)
                STRING -> StringNode(currentToken)
                IDENTIFIER -> parseIden()
                TRUE, FALSE -> BoolNode(currentToken)
                PLUS, MINUS, NOT -> parseUnaryOp()
                else -> throw SyntaxError("Unexpected token $currentType", currentStartPos, currentEndPos)
            }
            rhs = processBinOp(rightBP, processBinOp(rightBP, rhs))
            lhs = BinOpNode(lhs, op, rhs)
        }
        return lhs
    }

    private fun parseUnaryOp(): BaseNode {
        val op = currentToken
        ass()
        val inner = parseOnce()
        return processBinOp(0, UnaryOpNode(op, inner))  // unary node may be a part of math expression
    }

    private fun parseIden(): IdenNode = IdenNode(currentToken)

    private fun parseAssign(): AssignNode {
        val startPos = currentStartPos

        val mutable = currentType == VAR || currentType == DOLLAR
        ass()

        if (currentType != IDENTIFIER) throw SyntaxError("Expected identifier after var", currentStartPos, currentEndPos)
        val iden = parseIden()
        ass()

        if (currentType != ASSIGN) throw SyntaxError("Expected assignment operator after identifier", currentStartPos, currentEndPos)
        ass()

        if (currentType in listOf(EOF, LINEBREAK)) throw SyntaxError("Unexpected end of line", currentStartPos, currentEndPos)
        val value = parseOnce()

        return AssignNode(iden, value, mutable, startPos)
    }

    private fun parseBracket(): BaseNode {
        val bracketTypeL = currentType
        if (bracketTypeL !in Constant.bracket.keys) throw NotYourFaultError("Illegal bracket type $bracketTypeL", currentStartPos, currentEndPos)
        val bracketTypeR = Constant.bracket[bracketTypeL]

        var paranCount = 1
        val start = pos
        val startToken = currentToken
        val startPos = currentStartPos

        while (paranCount > 0) {  // Find the matching closing bracket in terms of number
            advance()
            val tt = currentType
            if (tt == EOF) throw SyntaxError("Script ended when expecting a $bracketTypeR", currentStartPos, currentEndPos)
            else if (Constant.bracket.keys.contains(tt)) paranCount++
            else if (Constant.bracket.values.contains(tt)) paranCount--
        }

        // Confirm the "matching" bracket is the same type
        if (currentType != bracketTypeR) throw SyntaxError("Expected $bracketTypeR, got $currentType", currentStartPos, currentEndPos)

        val endToken = currentToken
        val eof = Token(EOF, "EOF", endToken.startPos, endToken.endPos)
        val innerTokens = tokens.subList(start + 1, pos) + eof

        val node = when (bracketTypeL) {
            L_PAREN -> { // math expression
                // Parentheses used for function call (arguments) is parsed in checkCallGet()
                val innerResult = Parser(innerTokens).parse()
                val node = BracketNode(startToken, innerResult, endToken)
                processBinOp(0, node)  // Try to continue parsing the bracket as a binop, return itself if not anyway
            }

            L_BRACKET -> {  // lua table
                val (args, kwargs) = Parser(innerTokens).generateArguments()
                if (args.isNotEmpty() && kwargs.isNotEmpty()) throw SyntaxError("Table type unsure", startPos, currentEndPos)

                if (kwargs.isEmpty()) ListNode(args, startToken.startPos, currentEndPos)  // both empty -> empty list
                else DictNode(kwargs, startToken.startPos, currentEndPos)
            }

            L_BRACE -> {  // code block
                val innerResult = Parser(innerTokens).parse()
                BracketNode(startToken, innerResult, endToken)
            }

            else -> throw NotYourFaultError("parseBracket() check got bypassed with illegal bracket type $bracketTypeL", currentStartPos, currentEndPos)
        }
        return checkObjFollower(node)
    }

    private fun generateArguments(): Pair<List<BaseNode>, Map<Token, BaseNode>> {
        val args = mutableListOf<BaseNode>()
        val kwargs = mutableMapOf<Token, BaseNode>()

        skipSpace()
        var argsEnd = false
        var paramsEnd = currentType == EOF

        while (!paramsEnd) {
            if (!argsEnd) {
                if (nextType == ASSIGN) {  // treat this and all future params as kwargs
                    argsEnd = true
                    val name = currentToken
                    ass(2)
                    kwargs[name] = parseOnce()
                } else {
                    args += parseOnce()
                }

            } else {  // kwargs
                val name = currentToken
                if (nextType != ASSIGN) throw SyntaxError("No assign symbol in between", currentStartPos, currentEndPos)
                ass(2)
                kwargs[name] = parseOnce()
            }

            if (nextType == EOF) paramsEnd = true
            else if (nextType != COMMA) throw SyntaxError("No comma seperating arguments", currentStartPos, currentEndPos)
            else ass(2)
        }

        return args to kwargs
    }

    private fun parseIf(): IfNode {
        val startPos = currentStartPos

        ass()
        val mainCond = parseBracket()
        ass()
        val mainAction = parseBracket()
        var endPos = currentEndPos

        val elif = mutableMapOf<BaseNode, BaseNode>()
        while (nextType == ELIF) {
            ass(2)
            val cond = parseBracket()
            ass()
            val action = parseBracket()

            endPos = currentEndPos
            elif[cond] = action
        }

        var elseAction: BaseNode? = null
        if (nextType == ELSE || nextType == OR) {
            ass(2)
            elseAction = parseBracket()
            endPos = currentEndPos
        }

        return IfNode(mainCond, mainAction, elif, elseAction, startPos, endPos)
    }

    private fun parseLoop(): LoopNode {
        val startPos = currentStartPos
        val loopTT = currentType

        ass()
        var condition: BaseNode? = null
        if (loopTT == WHILE) {
            condition = parseBracket()
        } else if (loopTT == FOR) {
            val argStartPos = currentStartPos
            val hasParen = currentType == L_PAREN
            if (hasParen) ass()
            if (currentType != IDENTIFIER) throw SyntaxError("First argument of a for loop should be an identifier", currentStartPos, currentEndPos)
            val variable = parseIden()
            ass()

            if (currentType != COMMA) throw SyntaxError("The arguments of a for loop should be separated by a comma", currentStartPos, currentEndPos)
            ass()

            val iterable = parseOnce()
            ass()

            if (hasParen) {
                if (currentType != R_PAREN) throw SyntaxError("Parentheses should come in pairs", currentStartPos, currentEndPos)
                ass()
            }
            if (currentType == R_PAREN) throw SyntaxError("Parentheses should come in pairs", currentStartPos, currentEndPos)

            condition = ArgumentsNode(listOf(variable, iterable), mapOf(), argStartPos, currentEndPos)
        }

        if (condition == null) throw NotYourFaultError("Illegal loop type $loopTT", startPos, currentEndPos)

        ass()
        if (currentType != L_BRACE) throw SyntaxError("Expected {", currentStartPos, currentEndPos)
        val mainAction = parseBracket()
        var endPos = currentEndPos

        var compAction: BaseNode? = null
        var incompAction: BaseNode? = null
        while (nextType in Constant.loopCompleteTT) {
            ass()
            val tt = currentType
            ass()
            when (tt) {
                COMPLETE -> compAction = parseBracket()
                INCOMPLETE -> incompAction = parseBracket()
                else -> throw NotYourFaultError("Illegal loop complete type $tt", currentStartPos, currentEndPos)
            }
            endPos = currentEndPos
        }

        return LoopNode(loopTT, condition, mainAction, compAction, incompAction, startPos, endPos)
    }

    private fun parseInterrupt(): InterruptNode {
        val startPos = currentStartPos
        val type = currentType
        advance()
        while (currentType == SPACE) advance()

        val node = if (currentType == LINEBREAK) NullNode(currentToken) else parseOnce()
        return InterruptNode(node, type, startPos)
    }

    private fun parseFuncDef(): FuncNode {
        val startPos = currentStartPos
        var params = listOf<ParamNode>()
        var returnType: IdenNode? = null

        ass()

        val name = currentToken
        ass()

        if (currentType != L_PAREN) throw SyntaxError("Expected ( after function name", currentStartPos, currentEndPos)

        if (nextType != R_PAREN) {
            // Has params
            ass()
            params = generateParams()
            paramCheck(params)
        }

        ass()
        if (nextType == COLON) {
            // Has returnType
            ass()
            if (nextType != IDENTIFIER) throw SyntaxError("Expected return type after colon", currentStartPos, currentEndPos)
            ass()
            returnType = parseIden()
        }

        if (nextType != L_BRACE) throw SyntaxError("Expected { after function declaration", currentStartPos, currentEndPos)
        ass()
        val body = parseBracket()

        return FuncNode(name, params, returnType, body, startPos, currentEndPos)
    }

    private fun parseClass(): ClassNode {
        val startPos = currentStartPos
        var params = listOf<ParamNode>()
        var parent: IdenNode? = null

        ass()
        val name = currentToken
        ass()

        // has constructor params
        if (currentType == L_PAREN) {
            ass()
            if (currentType == IDENTIFIER) {
                params = generateParams()

                if (nextType != R_PAREN) throw SyntaxError("Unclosed bracket", currentStartPos, currentEndPos)
                ass(2)
            } else if (currentType != R_PAREN) throw SyntaxError("Unclosed bracket", currentStartPos, currentEndPos)
            else ass()  // skip useless R_PARAN

            paramCheck(params)
        }

        // has inheritance
        if (currentType == COLON) {
            ass()
            if (currentType != IDENTIFIER) throw SyntaxError("Expect class name after colon", currentStartPos, currentEndPos)
            parent = parseIden()
            ass()
        }

        if (currentType != L_BRACE) throw SyntaxError("Expected { after class declaration", currentStartPos, currentEndPos)
        val body = parseBracket()

        return ClassNode(name, params, parent, body, startPos, currentEndPos)
    }

    private fun generateParams(): List<ParamNode> {
        val params = mutableListOf<ParamNode>()
        params += parseParam()

        while (nextType == COMMA) {
            ass()
            if (nextType !in listOf(IDENTIFIER, MULTIPLY, POWER)) throw SyntaxError("Expected parameter name after comma", currentStartPos, currentEndPos)
            ass()
            params += parseParam()
        }
        return params
    }

    private fun paramCheck(params: List<ParamNode>) {
        if (params.find { it.variable } != params.findLast { it.variable }) throw SyntaxError("More than 1 variable arguments", currentStartPos, currentEndPos)
        if (params.find { it.kwvariable } != params.findLast { it.kwvariable }) throw SyntaxError("More than 1 variable keyword arguments", currentStartPos, currentEndPos)

        val firstDefault = params.indexOfFirst { it.default != null } .let { if (it == -1) params.size else it }
        if (params.subList(firstDefault, params.size).find { it.default == null } != null) throw SyntaxError("Paramaters with default values must be after those without default values", currentStartPos, currentEndPos)
    }

    private fun parseParam(): ParamNode {
        var variable = false
        var kwvariable = false

        if (currentType == MULTIPLY) { variable = true; advance() }
        else if (currentType == POWER) { kwvariable = true; advance() }

        if (currentType != IDENTIFIER) throw SyntaxError("Illegal character", currentStartPos, currentEndPos)

        val name = parseIden()
        var type: IdenNode? = null
        var default: BaseNode? = null

        if (nextType == COLON) {
            ass()
            if (nextType != IDENTIFIER) throw SyntaxError("Expected type after :", currentStartPos, currentEndPos)
            ass()
            type = parseIden()
        }

        if (nextType == ASSIGN) {
            ass(2)
            default = parseOnce()
        }

        return ParamNode(name, type, default, currentEndPos, variable, kwvariable)
    }

    private fun parseProp(node: BaseNode): PropAccessNode {
        if (currentType != DOT) throw NotYourFaultError("Not accessing property with a .", currentStartPos, currentEndPos)
        ass()
        if (currentType != IDENTIFIER) throw SyntaxError("Expected property name after .", currentStartPos, currentEndPos)
        val prop = parseIden()

        return PropAccessNode(node, prop)
    }
}