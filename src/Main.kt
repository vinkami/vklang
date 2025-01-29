import vklang.Script
import vklang.interpret.Referables

fun main() {
    val script = Script("src/test.vk")
    val ref = Referables({s: String -> println(s)})
    val err = script.interpret(ref)
    if (err != null) {
        println(err)
        println(script.parse())
    }
}