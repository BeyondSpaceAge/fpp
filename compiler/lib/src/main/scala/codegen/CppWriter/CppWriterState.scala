package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** C++ Writer state */
case class CppWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The include guard prefix */
  guardPrefix: Option[String] = None,
  /** The list of include path prefixes */
  pathPrefixes: List[String] = Nil,
  /** The default string size */
  defaultStringSize: Int = CppWriterState.defaultDefaultStringSize,
  /** The name of the tool using the CppWriter */
  toolName: Option[String] = None,
  /** The map from strings to locations */
  locationMap: Map[String, Option[Location]] = Map()
) {

  /** Adds the component name prefix to a name.
   *  This is to work around the fact that we can't declare
   *  constants inside component classes, because we are using
   *  F Prime XML to generate the component classes. */
  def addComponentNamePrefix(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(componentSymbol: Symbol.Component) =>
        val componentName = componentSymbol.getUnqualifiedName
        s"${componentName}_$name"
      case _ => name
    }
  }

  /** Removes the longest prefix from a Java path */
  def removeLongestPathPrefix(path: File.JavaPath): File.JavaPath =
    File.removeLongestPrefix(pathPrefixes)(path)

  /** Gets the relative path for a file */
  def getRelativePath(fileName: String): File.JavaPath = {
    val path = java.nio.file.Paths.get(fileName).toAbsolutePath.normalize
    removeLongestPathPrefix(path)
  }

  /** Constructs an include guard from the prefix and a name */
  def includeGuardFromPrefix(name: String): String = {
    val rawPrefix = guardPrefix.getOrElse(getRelativePath(".").toString)
    val prefix = "[^A-Za-z0-9_]".r.replaceAllIn(rawPrefix, "_")
    prefix match {
      case "" =>  s"${name}_HPP"
      case _ => s"${prefix}_${name}_HPP"
    }
  }

  /** Constructs a C++ identifier from a qualified symbol name */
  def identFromQualifiedSymbolName(s: Symbol): String =
    CppWriter.identFromQualifiedName(a.getQualifiedName(s))

  /** Constructs an include guard from a qualified name and a kind */
  def includeGuardFromQualifiedName(s: Symbol, name: String): String = {
    val guard = a.getEnclosingNames(s) match {
      case Nil => name
      case names =>
        val prefix = CppWriter.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${prefix}_$name"
    }
    s"${guard}_HPP"
  }

  /** Gets the C++ namespace associated with a symbol */
  def getNamespace(symbol: Symbol): Option[String] =
    a.parentSymbolMap.get(symbol).map(
      s => CppWriter.writeQualifiedName(a.getQualifiedName(s))
    )

  /** Gets the list of identifiers representing the namespace
   *  associated with a symbol */
  def getNamespaceIdentList(symbol: Symbol): List[String] =
    a.parentSymbolMap.get(symbol) match {
      case Some(s) => a.getQualifiedName(s).toIdentList
      case None => Nil
    }

  /** Gets the unqualified name associated with a symbol.
   *  If a symbol is defined in a component, then we prefix its name
   *  with the component name. This is to work around the fact that
   *  we cannot define classes inside components in the F Prime XML. */
  def getName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case _ => name
    }
  }

  /** Write an FPP symbol as C++ */
  def writeSymbol(sym: Symbol): String = {
    // Skip component names in qualifiers
    // Those appear in the prefixes of definition names
    def removeComponentQualifiers(
      symOpt: Option[Symbol],
      out: List[String]
    ): List[String] = symOpt match {
      case None => out
      case Some(sym) =>
        val psOpt = a.parentSymbolMap.get(sym)
        val out1 = sym match {
          case cs: Symbol.Component => out
          case _ => getName(sym) :: out
        }
        removeComponentQualifiers(psOpt, out1)
    }
    val qualifiedName = sym match {
      // For component symbols, use the qualified name
      case cs: Symbol.Component => a.getQualifiedName(cs)
      // For other symbols, remove component qualifiers
      case _ => {
        val identList = removeComponentQualifiers(Some(sym), Nil)
        Name.Qualified.fromIdentList(identList)
      }
    }
    CppWriter.writeQualifiedName(qualifiedName)
  }

}

object CppWriterState {

  /** The default default string size */
  val defaultDefaultStringSize = 80

  /** A mapping from special built-in types to their
   *  default values */
  val zero: Value.Integer = Value.Integer(0)
  val builtInTypes: Map[String,Value.Integer] = Map(
    "FwBuffSizeType" -> zero,
    "FwChanIdType" -> zero,
    "FwEnumStoreType" -> zero,
    "FwEventIdType" -> zero,
    "FwOpcodeType" -> zero,
    "FwPacketDescriptorType" -> zero,
    "FwPrmIdType" -> zero,
    "FwTimeBaseStoreType" -> zero,
    "FwTimeContextStoreType" -> zero,
    "NATIVE_INT_TYPE" -> zero,
    "NATIVE_UINT_TYPE" -> zero,
    "POINTER_CAST" -> zero,
  )

}
