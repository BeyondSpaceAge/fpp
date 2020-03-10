package fpp.compiler.codegen

import fpp.compiler.ast._

/** Write out an FPP AST as lines */
trait LineWriter {

  def defAbsType(dat: Ast.DefAbsType): List[Line]

  def defArray(da: Ast.DefArray): List[Line]

  def defComponent(dc: Ast.DefComponent): List[Line]

  def defComponentInstance(dc: Ast.DefComponentInstance): List[Line]

  def defConstant(dc: Ast.DefConstant): List[Line]

  def defEnum(de: Ast.DefEnum): List[Line]

  def defEnumConstant(dec: Ast.DefEnumConstant): List[Line]

  def defModule(dm: Ast.DefModule): List[Line]

  def defPort(dm: Ast.DefPort): List[Line]

  def defStruct(ds: Ast.DefStruct): List[Line]

  def defTopology(ds: Ast.DefTopology): List[Line]

  def expr(e: Ast.Expr): List[Line]

  def specInclude(sl: Ast.SpecInclude): List[Line]

  def specInit(sl: Ast.SpecInit): List[Line]

  def specLoc(sl: Ast.SpecLoc): List[Line]

  def structMember(sm: Ast.StructMember): List[Line]

  def transUnit(tu: Ast.TransUnit): List[Line]

  def tuMember(tum: Ast.TUMember): List[Line]

  def typeName(tn: Ast.TypeName): List[Line]

}