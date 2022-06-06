package lifpga

import scala.collection.mutable.ArrayBuffer
import scala.math._
import scala.math.log
import chisel3._
import firrtl.Utils
import hardfloat._

class AdderTreeIO(p_num_inputs: Int, expWidth: Int, sigWidth: Int) extends Bundle {
    val inputs = Input(Vec(p_num_inputs, UInt((expWidth + sigWidth + 1).W)))
    val out = Output(UInt((expWidth + sigWidth + 1).W))
}

class AdderTree(p_num_inputs: Int, expWidth: Int, sigWidth: Int) extends Module {
  require((p_num_inputs & (p_num_inputs - 1)) == 0)
  val io = IO(new AdderTreeIO(p_num_inputs, expWidth, sigWidth))  

    val depth = (log10(p_num_inputs)/log10(2.0)).toInt + 1
    val sizes = ArrayBuffer[Int]()
    sizes += p_num_inputs
    for (i <- 1 until depth) {
        sizes += sizes(i-1) / 2
    }
    val default = new RawFloat(expWidth, sigWidth)
    val tree = for (layer_sizes <- sizes.slice(1,sizes.length)) yield {
        val mods = for (i <- 0 until layer_sizes) yield {
            val mod = Module(new AddRecFN(expWidth, sigWidth))

            mod.io.subOp := 0.B
            mod.io.roundingMode := 0.B
            mod.io.detectTininess := 0.B
            mod
        }
        mods
    }

    var layer = 0
    println("building adder tree")
    println(tree)
    for (size <- sizes.slice(0,sizes.length-1)) {
        for (i <- 0 until size) {
            println(layer, size, i, depth)
            if (layer == depth-1) {
                
            } else if (layer == 0){
                if(i % 2 == 0){
                    if(i == 0){
                        tree(0)(0).io.a := io.inputs(i)
                        tree(0)(0).io.b := io.inputs(i+1)
                    } else {
                        tree(0)(i/2).io.a := io.inputs(i)
                        tree(0)(i/2).io.b := io.inputs(i+1)
                    }
                }
            } else if (layer == depth-2) {
                tree(depth-2)(0).io.a := tree(depth-3)(0).io.out
                tree(depth-2)(0).io.b := tree(depth-3)(1).io.out
                io.out := tree(depth-2)(0).io.out
            } else {
                if(i % 2 == 0){
                    if(i == 0){
                        tree(layer)(0).io.a := tree(layer-1)(i).io.out
                        tree(layer)(0).io.b := tree(layer-1)(i+1).io.out                            
                    } else {
                        tree(layer)(i/2).io.a := tree(layer-1)(i).io.out
                        tree(layer)(i/2).io.b := tree(layer-1)(i+1).io.out
                    }
                }
            }
        }
        layer = layer + 1
    }
}

