// See README.md for license details.

package lifpga


import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

import hardfloat._
/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly gcd.GcdDecoupledTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly gcd.GcdDecoupledTester'
  * }}}
  */
class AdderTreeTester extends AnyFreeSpec with ChiselScalatestTester {
  val expWidth = 8
  val sigWidth = 23
  "build 8:1 floating point adder tree with" in {
    test(new AdderTree(8, 8, 23)) { dut =>

      dut.io.inputs(0).poke(1065353216) 
      dut.io.inputs(1).poke(1065353216) 
      dut.io.inputs(2).poke(1065353216) 
      dut.io.inputs(3).poke(1065353216) 
      dut.io.inputs(4).poke(1065353216) 
      dut.io.inputs(5).poke(1065353216) 
      dut.io.inputs(6).poke(1065353216) 
      dut.io.inputs(7).poke(1065353216) 


      dut.clock.step(1)
      println(dut.io.out.expect(1077936128)) 
    }
  }
}
