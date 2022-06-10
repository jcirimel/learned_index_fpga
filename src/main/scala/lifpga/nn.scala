// See README.md for license details.

package lifpga

import chisel3._
import hardfloat._


class NeuronIO(p_num_inputs: Int, expWidth: Int, sigWidth: Int, relu: Boolean) extends Bundle {
  val inputs = Input(Vec(p_num_inputs, UInt((expWidth + sigWidth + 1).W)))
  val out = Output(UInt((expWidth + sigWidth + 1).W))
}

class Neuron(p_num_inputs: Int, expWidth: Int, sigWidth: Int, weight: UInt, bias: UInt, relu: Boolean) extends Module {
  require(p_num_inputs > 0)
  val io = IO(new NeuronIO(p_num_inputs, expWidth, sigWidth, relu))
  val mul = Module(new MulRecFN(expWidth, sigWidth))
  val add = Module(new AddRecFN(expWidth, sigWidth))
  val sum = UInt()

  mul.io.detectTininess := 0.B
  mul.io.roundingMode := 0.U

  add.io.detectTininess := 0.B
  add.io.roundingMode := 0.U

  if(p_num_inputs > 1){
    val tree = Module(new AdderTree(p_num_inputs, expWidth, sigWidth))
    tree.io.inputs := io.inputs
    mul.io.a := tree.io.out
  } else {
    mul.io.a := io.inputs(0)  
  }
  //mul.io.a := sum
  mul.io.b := weight

  add.io.a := mul.io.out
  add.io.b := bias

  if(relu == true){
    when((io.out & (0x8000000.U)) === 1.U) {
      io.out := 0.U
    }
  }
}
