package vklang.interpret

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LoopTest {
    @Test
    fun testWhile() {
        val (output, err) = run("""
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n3.0\n4.0\n", output)
    }

    @Test
    fun testWhileComplete() {
        val (output, err) = run("""
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
            } complete {
                print("Complete!")
            } incomplete {
                print("Incomplete!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n3.0\n4.0\nComplete!\n", output)
    }

    @Test
    fun testWhileBreak() {
        val (output, err) = run("""
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
                if (x == 3) {
                    break
                }
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\n", output)
    }

    @Test
    fun testWhileIncomplete() {
        val (output, err) = run("""
            var x = 0
            while (x < 5) {
                print(x)
                x = x + 1
                if (x == 3) {
                    break
                }
            } complete {
                print("Complete!")
            } incomplete {
                print("Incomplete!")
            }
        """.trimIndent())
        assertNull(err)
        assertEquals("0.0\n1.0\n2.0\nIncomplete!\n", output)
    }
}