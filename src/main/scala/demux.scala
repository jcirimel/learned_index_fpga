package lifpga

import chisel3._
import hardfloat._

class DemuxIO(expWidth: Int, sigWidth: Int, num_outputs: Int) extends Bundle{
    val input = Input(UInt((expWidth + sigWidth + 1).W))
    val sel = Input(UInt())
    val output = Output(UInt((expWidth + sigWidth + 1).W))
    val enable = Output(Vec(num_outputs, Bool()))
}
class Demux(expWidth: Int, sigWidth: Int, num_outputs: Int) extends Module {
    val io = IO(new DemuxIO(expWidth, sigWidth, num_outputs))
    for (i <- 0 until num_outputs) {
        io.enable(i) := 0.B
    }
    io.enable(io.sel) := 1.B
    io.output := io.input

}