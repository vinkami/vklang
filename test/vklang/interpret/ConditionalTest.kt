package vklang.interpret

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ConditionalTest {
    @Test
    fun testIf() {
        val (output, err) = run("""
            val x = 5
            if (x == 5) {
                print("Hello, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Hello, world!\n", output)
    }

    @Test
    fun testIfSymbols() {
        val (output, err) = run("""
            val x = 5
            ? (x == 5) {
                print("Hello, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Hello, world!\n", output)
    }

    @Test
    fun testIfElse() {
        val (output, err) = run("""
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } else {
                print("Goodbye, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Goodbye, world!\n", output)
    }

    @Test
    fun testIfElseSymbol() {
        val (output, err) = run("""
            val x = 5
            ? (x == 10) {
                print("Hello, world!")
            } | {
                print("Goodbye, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Goodbye, world!\n", output)
    }

    @Test
    fun testElif() {
        val (output, err) = run("""
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } elif (x == 5) {
                print("Goodbye, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Goodbye, world!\n", output)
    }

    @Test
    fun testElifSymbols() {
        val (output, err) = run("""
            val x = 6
            ? (x == 10) {
                print("Hello, world!")
            } |? (x == 6) {
                print("Goodbye, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Goodbye, world!\n", output)
    }

    @Test
    fun testElifElse() {
        val (output, err) = run("""
            val x = 5
            if (x == 10) {
                print("Hello, world!")
            } elif (x == 6) {
                print("Goodbye, world!")
            } else {
                print("Goodnight, world!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("Goodnight, world!\n", output)
    }
}