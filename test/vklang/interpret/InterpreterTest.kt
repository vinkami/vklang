package vklang.interpret

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import vklang.Script

class InterpreterTest {
    private class Result {
        var output: String = ""
        val ref: Referables = Referables({ s: String -> output += s })
    }

    @Test
    fun testPrint() {
        val result = Result()
        val script = Script("<test>", "print(\"Hello, world!\")")
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Hello, world!", result.output)
    }

    @Test
    fun testMutability() {
        val result = Result()
        val code = """
            var x = 5
            x = 10
            print(x)
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("10.0", result.output)
    }

    @Test
    fun testImmutability() {
        val result = Result()
        val code = """
            val x = 5
            x = 10
            print(x)
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNotNull(err)
    }

    @Test
    fun testIf() {
        val result = Result()
        val code = """
            val x = 5
            if (x == 5) {
                print("Hello, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Hello, world!", result.output)
    }

    @Test
    fun testIfElse() {
        val result = Result()
        val code = """
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } else {
                print("Goodbye, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodbye, world!", result.output)
    }

    @Test
    fun testIfElseSymbol() {
        val result = Result()
        val code = """
            val x = 5
            ? (x == 10) {
                print("Hello, world!")
            } | {
                print("Goodbye, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodbye, world!", result.output)
    }

    @Test
    fun testElif() {
        val result = Result()
        val code = """
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } elif (x == 5) {
                print("Goodbye, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodbye, world!", result.output)
    }

    @Test
    fun testElifSymbols() {
        val result = Result()
        val code = """
            val x = 6
            ? (x == 10) {
                print("Hello, world!")
            } |? (x == 6) {
                print("Goodbye, world!")
            } | {
                print("Goodnight, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodbye, world!", result.output)
    }

    @Test
    fun testElifElse() {
        val result = Result()
        val code = """
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } elif (x == 6) {
                print("Goodbye, world!")
            } else {
                print("Goodnight, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodnight, world!", result.output)
    }
}