package lifpga

import chisel3._
import hardfloat._
import scala.math.log10
import firrtl.transforms.SortModules
import chisel3.stage.ChiselStage


class topIO(expWidth: Int, sigWidth: Int) extends Bundle {
  val key = Input(UInt((expWidth+sigWidth+1).W))
  val out = Output(UInt((expWidth+sigWidth+1).W))
}

class top(neurons: Int, expWidth: Int, sigWidth: Int, len_stages: Int) extends Module {
    val io = IO(new topIO(expWidth, sigWidth))
    val data = new test_structure()
    val layer1 = for (i <- 0 until neurons) yield {
            val mod = Module(new Neuron(1,expWidth, sigWidth, data.weights(0)(i), data.biases(0)(i),true))
            mod
        }
    val layer2 = for (i <- 0 until neurons) yield {
        val mod = Module(new Neuron(neurons,expWidth, sigWidth, data.weights(1)(i), data.biases(1)(i),false))
        mod
    }
    
    //val layer_1_input_vector = Vec(1, UInt())
    //layer_1_input_vector(0) := io.key
    for(neuron <- layer1){
        neuron.io.inputs(0) := io.key
    }

    for(neuron <- layer2){
        for (i <- 0 until layer1.size) {
            neuron.io.inputs(i) := layer1(i).io.out
        }
    }
    val len_stages_bits = (log10(len_stages)/log10(2.0)).toInt + 1
    val final_neuron = Module(new Neuron(neurons, expWidth, sigWidth, data.weights(2)(0), data.biases(2)(0), false))
    for(i <- 0 until neurons){
        final_neuron.io.inputs(i) := layer2(i).io.out
    }
    val stages_to_float = Module(new INToRecFN(len_stages_bits, expWidth, sigWidth))
    stages_to_float.io.in := (len_stages.U)
    stages_to_float.io.signedIn := 0.B
    stages_to_float.io.roundingMode := 0.U
    stages_to_float.io.detectTininess := 0.U
    

    val mul = Module(new MulRecFN(expWidth, sigWidth))
    mul.io.detectTininess := 0.B
    mul.io.roundingMode := 0.B

    mul.io.a := final_neuron.io.out
    mul.io.b := stages_to_float.io.out
    
    val demux = Module(new Demux(expWidth, sigWidth, len_stages))

    demux.io.input := final_neuron.io.out
    demux.io.sel := 0.U

    val cmux = Module(new CustomMux(expWidth, sigWidth, len_stages))
    val ss = for(i <- 0 until len_stages) yield{
            val asd = println(i)
            if (data.second_stage(i)(0) == "nn"){
                val mod = Module(new Neuron(1,expWidth, sigWidth, data.second_stage(i)(1).asInstanceOf[UInt],data.second_stage(i)(2).asInstanceOf[UInt],false))
                mod.io.inputs := Seq(demux.io.output)
                cmux.io.input(i) := mod.io.out

                mod
            }
            else if (data.second_stage(i)(0) == "tree"){
                val mod = Module(new BTreeNode(expWidth, sigWidth, data.second_stage(i)(1).asInstanceOf[Seq[Int]], data.second_stage(i)(2).asInstanceOf[Seq[Int]], data.second_stage(i)(3).asInstanceOf[Boolean], data.second_stage(i)(4).asInstanceOf[Int]))
                mod.io.input := demux.io.output
                mod.io.enable := demux.io.enable(i)
                cmux.io.input(i) := mod.io.out

                mod
            }
            else{
                print("data format error\n")
                0
            }
            
    }
    cmux.io.sel := 0.U
    io.out := cmux.io.output
}
object VerilogMain extends App {
    (new ChiselStage).emitVerilog(new top(8, 8, 23,2))
}