import vklang.Script
import vklang.interpret.Referables

fun main() {
    val script = Script("src/test.vk")
    val ref = Referables({s: String -> println(s)})
    script.interpret(ref)
//    println(script.parse())
}