// All detector auth generator objects should extend this class and override its contents accordingly
class Detector {
  val (authGenerator: Function[Any, String]) = null
}
