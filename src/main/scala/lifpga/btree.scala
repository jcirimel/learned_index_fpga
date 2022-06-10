package lifpga

import chisel3._
import hardfloat._
import os.read

class BTreeNodeIO() extends Bundle {
    val input = Input(UInt())
    val enable = Input(Bool())
    val out = Output(UInt())
    val key = Output(UInt(32.W))
}

class BTreeNode(expWidth: Int, sigWidth: Int, keys: Seq[Int], values: Seq[Int], leaf: Boolean, sel_id: Int) extends Module {
    val io = IO(new BTreeNodeIO())

    if(!leaf){
        io.key := io.input
    }
    //io.enable := 0.B
    for( i <- 0 to keys.length - 1) {

        
        val compare_left = Module(new CompareRecFN(expWidth, sigWidth))
        val compare_right = Module(new CompareRecFN(expWidth, sigWidth))

        compare_left.io.a := io.input
        compare_right.io.a := io.input

        compare_left.io.b := keys(i).U
        compare_right.io.b := keys(i).U

        compare_left.io.signaling := 0.B
        compare_left.io.signaling := 0.B

        val gt = compare_left.io.lt
        val eq = compare_left.io.eq
        val lt = compare_right.io.lt

        when (gt || eq && lt) {
            io.out := values(i).U
            //io.enable := 1.U
        }
    }



}
