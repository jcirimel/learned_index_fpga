
package lifpga

import chisel3._
import hardfloat._

class test_structure(){
    val weights = Seq(Seq(1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U), 
                      Seq(1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U),
                      Seq(1.U))
    val biases = Seq(Seq(0.U, 0.U, 0.U, 0.U, 0.U, 0.U, 0.U, 0.U),
                     Seq(1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U, 1.U),
                     Seq(0.U))
    val second_stage = Seq(Seq("nn",1.U, 1.U), 
                           Seq("tree", Seq(1, 2), Seq(1, 2), true, 0))

    val len_stages = second_stage.length
}
