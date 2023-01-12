package fusespinal

import spinal.core._
import scopt.OptionParser

// Hardware definition
case class MyTopLevel(width: Int) extends Component {
  val io = new Bundle {
    val cond0 = in  Bool()
    val cond1 = in  Bool()
    val flag  = out Bool()
    val state = out UInt(width bits)
  }

  val counter = Reg(UInt(width bits)) init 0

  when(io.cond0) {
    counter := counter + 1
  }

  io.state := counter
  io.flag := (counter === 0) | io.cond1
}

object MyTopLevelVerilog extends App {
  val param = CmdConfig.parse(args)
  Config.spinal.generateVerilog(MyTopLevel(param.width))
}

// object MyTopLevelVhdl extends App {
//   Config.spinal.generateVhdl(MyTopLevel())
// }
