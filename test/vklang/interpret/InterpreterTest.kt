package vklang.interpret

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import vklang.Script

class InterpreterTest {
    private class Result {
        var output: String = ""
        val ref: Referables = Referables({ s: String -> output += s + "\n" })
    }

    @Test
    fun testPrint() {
        val result = Result()
        val script = Script("<test>", "print(\"Hello, world!\")")
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Hello, world!\n", result.output)
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
        assertEquals("10.0\n", result.output)
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
        assertEquals("Hello, world!\n", result.output)
    }

    @Test
    fun testIfSymbols() {
        val result = Result()
        val code = """
            val x = 5
            ? (x == 5) {
                print("Hello, world!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Hello, world!\n", result.output)
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
        assertEquals("Goodbye, world!\n", result.output)
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
        assertEquals("Goodbye, world!\n", result.output)
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
        assertEquals("Goodbye, world!\n", result.output)
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
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("Goodbye, world!\n", result.output)
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
        assertEquals("Goodnight, world!\n", result.output)
    }

    @Test
    fun testWhile() {
        val result = Result()
        val code = """
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n3.0\n4.0\n", result.output)
    }

    @Test
    fun testWhileComplete() {
        val result = Result()
        val code = """
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
            } complete {
                print("Done!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n3.0\n4.0\nDone!\n", result.output)
    }

    @Test
    fun testWhileBreak() {
        val result = Result()
        val code = """
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
                if (x == 3) {
                    break
                }
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n", result.output)
    }

    @Test
    fun testWhileIncomplete() {
        val result = Result()
        val code = """
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
                if (x == 3) {
                    break
                }
            } incomplete {
                print("Incomplete!")
            }
        """.trimIndent()
        val script = Script("<test>", code)
        val err = script.interpret(result.ref)
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\nIncomplete!\n", result.output)
    }
}