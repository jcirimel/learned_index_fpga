package lifpga

import chisel3._
import hardfloat._
import os.read

class BTreeNodeIO(expWidth: Int, sigWidth: Int) extends Bundle {
    val input = Input(UInt((expWidth+sigWidth+1).W))
    val enable = Input(Bool())
    val out = Output(UInt())
    val key = Output(UInt((expWidth+sigWidth+1).W))
}

class BTreeNode(expWidth: Int, sigWidth: Int, keys: Seq[Int], values: Seq[Int], leaf: Boolean, sel_id: Int) extends Module {
    val io = IO(new BTreeNodeIO(expWidth, sigWidth))


    io.key := io.input

    io.out := values(values.length -1).U
    //io.enable := 0.B

    for( i <- 0 to keys.length - 1) {

        
        val compare_left = Module(new CompareRecFN(expWidth, sigWidth))
        val compare_right = Module(new CompareRecFN(expWidth, sigWidth))

        compare_left.io.a := io.input
        compare_right.io.a := io.input

        compare_left.io.b := keys(i).U
        compare_right.io.b := keys(i).U

        compare_left.io.signaling := 0.B
        compare_right.io.signaling := 0.B

        val gt = compare_left.io.lt
        val eq = compare_left.io.eq
        val lt = compare_right.io.lt

        when (gt || eq && lt) {
            io.out := values(i).U
            //io.enable := 1.U
        } 
    }

    val compare_min = Module(new CompareRecFN(expWidth, sigWidth))
    compare_min.io.signaling := 0.U
    compare_min.io.a := io.input
    compare_min.io.b := keys(0).U
    when(compare_min.io.lt){
        io.out := values(0).U
    }
    
}
