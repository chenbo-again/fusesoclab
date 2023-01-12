package fusespinal

import spinal.core._
import spinal.core.sim._

object Config {
  def spinal = SpinalConfig(
    targetDirectory = "hw/gen",
    defaultConfigForClockDomains = ClockDomainConfig(
      resetActiveLevel = HIGH
    ),
    onlyStdLogicVectorAtTopLevelIo = true
  )

  def sim = SimConfig.withConfig(spinal).withFstWave
}

object CmdConfig {
  case class Params(
      width: Int = 8
  )

  val parser = new scopt.OptionParser[Params]("test") {
    head("this is a test for scopt params")

    opt[Int]("width")
      .optional()
      .action { (x, c) =>
        c.copy(width = x)
      }
      .text("default width is 8")
  }
  def parse(args: Array[String]): Params = {
    val params = parser.parse(args, Params()).get
    params
  }  
}
