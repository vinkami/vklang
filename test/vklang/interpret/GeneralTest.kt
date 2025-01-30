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
        val (output, err) = run("""
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
}