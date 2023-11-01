package fusespinal

import com.fasterxml.jackson.core.{JsonFactory, JsonParser}
import com.fasterxml.jackson.databind.json.JsonMapper
import spinal.core._
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.dataformat.yaml.{YAMLFactory, YAMLGenerator}
import com.fasterxml.jackson.module.scala._
import spinal.lib.bus.regif.JsonGenerator

abstract class FuseSocGeneratorBuilder[P, C<:Component](compName: String, default: P) {
  private val yamlMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
  yamlMapper.registerModule(DefaultScalaModule)
  private val jsonMapper = new ObjectMapper(new JsonFactory().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true).configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true))
  jsonMapper.registerModule(DefaultScalaModule)

  case class FileDesc(file_type: String = "verilogSource")
  case class Output(files: List[Map[String, FileDesc]] = List(Map(compName+".v"->FileDesc())))

  case class Generate(generator: String="spinalhdl", parameters: P2SParam)
  case class P2SParam(spinal_parameter: P, output: Output = Output(), target_directory: String = "./")

  case class DependDesc(depend: List[String] = List("chenbosoft:utils:generators:0.0.0"))

  class CoreFile {
    val name: String = "::" + compName + ":0"
    val filesets = Map("rtl"->DependDesc())
    val generate: Map[String, Generate] = Map(compName+"_gen"->Generate(parameters = P2SParam(default)))
  }

  def buildScript: String = {
    "CAPI=2:\n\n"++yamlMapper.writeValueAsString(new CoreFile)
  }

  def buildComponent(parameter: P): C

  def run(args: Array[String]): Unit = {
    val p = getParameter(args)
    val config = SpinalConfig(targetDirectory = p.target_directory)
    SpinalVerilog(config)(buildComponent(p.spinal_parameter))
  }

  private def getParameter(args: Array[String]): P2SParam = {
    val builder = scopt.OParser.builder[P2SParam]
    // todo
    val parser = scopt.OParser.sequence(
      builder.opt[String]("spinal_parameter").required().action((x,c)=>c.copy(spinal_parameter=jsonMapper.readValue(x, default.getClass()))),
      builder.opt[String]("target_directory").required().action((x,c)=>c.copy(target_directory = x))
    )
    scopt.OParser.parse(parser, args, P2SParam(default)).getOrElse(throw new Exception(""))
  }

}


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
  case class MP(width: Int = 8)
  val fusesoc = new FuseSocGeneratorBuilder[MP,MyTopLevel]("MyTopLevel", MP() ) {
    override def buildComponent(parameter: MP): MyTopLevel = {
      MyTopLevel(parameter.width)
    }
  }
  println(fusesoc.buildScript)
  fusesoc.run(args)


  //  class Person(_name: String, _age: Int, _habbit: List[String], _isStudent: Option[Boolean]) {
//    val name = _name
//    val age = _age
//    val habbit = _habbit
//    val isStudent = _isStudent
//    val hhh = Map("abc" -> "def")
//  }
//  val p = new Person("cb", 12, List(), Some(true))
//  val mapper = new ObjectMapper(new YAMLFactory())
//  mapper.registerModule(DefaultScalaModule)
//
//  val str = mapper.writeValueAsString(p)
//  println(str)
//  val p1 = mapper.readValue(str, classOf[Person])
//  println(p1.age, p1.habbit, p1.isStudent, p1.name)
  // val reader = new FileReader("sample.yaml")
  // val mapper = new ObjectMapper(new YAMLFactory())
  // val config: Sample = mapper.readValue(reader, classOf[Sample])
  // config.things.foreach(x=>println(x.colour, x.priority))
  // println(config.parameters)
  // val yamlstr = mapper.writeValueAsString(config.things(0))
  // val wpath = Paths.get("osample.yaml")
  // Files.writeString(wpath, yamlstr)


}

// object MyTopLevelVhdl extends App {
//   Config.spinal.generateVhdl(MyTopLevel())
// }
