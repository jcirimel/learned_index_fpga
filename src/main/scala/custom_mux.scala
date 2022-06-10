package lifpga

import chisel3._
import hardfloat._

class CustomMuxIO(expWidth: Int, sigWidth: Int, num_inputs: Int) extends Bundle{
    val input = Input(Vec(num_inputs, UInt((expWidth + sigWidth + 1).W)))
    val sel = Input(UInt())
    val output = Output(UInt((expWidth + sigWidth + 1).W))
}
class CustomMux(expWidth: Int, sigWidth: Int, num_inputs: Int) extends Module {
    val io = IO(new CustomMuxIO(expWidth, sigWidth, num_inputs))
    io.output := 0.B
    io.output := io.input(io.sel)

}