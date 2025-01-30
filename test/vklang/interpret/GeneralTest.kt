package vklang.interpret

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GeneralTest {
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
        val (_, err) = run("""
            val x = 5
            x = 10
            print(x)
        """.trimIndent())
        assertNotNull(err)
    }

    @Test
    fun testLogic() {
        val (output, err) = run("""
            print(5 == 5)
            print(5 != 6)
            print(5 < 6)
            print(5 <= 5)
            print(5 > 4)
            print(5 >= 4)
            print(true & true)
            print(true | false)
            print(!(true & false))
        """.trimIndent())
        assertNull(err)
        assertEquals("true\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\n", output)
    }

    @Test
    fun testMath() {
        val (output, err) = run("""
            print(10 + 5)
            print(10 - 5)
            print(10 * 5)
            print(10 / 5)
            print(10 % 3)
            print(10 ** 3)
        """.trimIndent())
        assertNull(err)
        assertEquals("15.0\n5.0\n50.0\n2.0\n1.0\n1000.0\n", output)
    }

    @Test
    fun testMathExpr() {
        val (output, err) = run("""
            print(10 + 5 * 2)
            print((10 + 5) * 2)
            print(2 * 10 + 5)
            print(2 * (10 + 5))
            print(3 * 2 + 5 ** (3 - 1))
        """.trimIndent())
        assertNull(err)
        assertEquals("20.0\n30.0\n25.0\n30.0\n31.0\n", output)
    }
}