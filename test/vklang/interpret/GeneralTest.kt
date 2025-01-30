package vklang.interpret

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GeneralTest {
    @Test
    fun testPrint() {
        val (output, err) = run("print(\"Hello, world!\")")
        assertNull(err)
        assertEquals("Hello, world!\n", output)
    }

    @Test
    fun testMutability() {
        val (output, err) = run("""
            var x = 5
            x = 10
            print(x)
        """.trimIndent())
        assertNull(err)
        assertEquals("10.0\n", output)
    }
    
    @Test
    fun testImmutability() {
        val (output, err) = run("""
            val x = 5
            x = 10
            print(x)
        """.trimIndent())
        assertNotNull(err)
    }
}