package lifpga

import chisel3._
import hardfloat._


class FirstStageIO() extends Bundle {
  val key = Input(UInt())
  val out = Output(UInt())
}

class FirstStage(neurons: Int, expWidth: Int, sigWidth: Int, weights: Seq[Seq[UInt]], biases: Seq[Seq[UInt]]) extends Module {
    val io = IO(new FirstStageIO())
    val layer1 = for (i <- 0 until neurons) yield {
            val mod = Module(new Neuron(1,expWidth, sigWidth,weights(0)(i), biases(0)(i),true))
            mod
        }
    val layer2 = for (i <- 0 until neurons) yield {
        val mod = Module(new Neuron(neurons,expWidth, sigWidth,weights(1)(i), biases(1)(i),false))
        mod
    }
    
    val layer_1_input_vector = Vec(1, UInt())
    layer_1_input_vector(0) := io.key
    for(neuron <- layer1){
        neuron.io.inputs := layer_1_input_vector
    }
    val layer_2_input_vector = Vec(neurons, UInt())
    for (i <- 0 until layer1.size) {
        layer_2_input_vector(i) := layer1(i).io.out
    }

    for(neuron <- layer2){
        neuron.io.inputs := layer_2_input_vector
    }

    val final = Module(new Neuron())
}