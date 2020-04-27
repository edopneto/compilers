package traducao_intermediario.visitor;

import analisador_semantico.syntaxtree.*;
import traducao_intermediario.Translate.*;

public interface VisitorIR {
  public Exp visit(AndExpression n);

  public Exp visit(ArrayAssignStatement n);

  public Exp visit(ArrayType n);

  public Exp visit(AssignStatement n);

  public Exp visit(BigExpression n);

  public Exp visit(BlockExpression n);

  public Exp visit(BlockStatement n);

  public Exp visit(BooleanType n);

  public Exp visit(ClassSimple n);

  public Exp visit(ClassDeclaration n);

  public Exp visit(ClassDeclarationExtends n);

  public Exp visit(Expression n);

  public Exp visit(FalseExpression n);

  public Exp visit(Formal n);

  public Exp visit(Goal n);

  public Exp visit(Identifier n);

  public Exp visit(IdentifierExpression n);

  public Exp visit(IdentifierType n);

  public Exp visit(IfStatement n);

  public Exp visit(IntegerLiteralExpression n);

  public Exp visit(IntegerType n);

  public Exp visit(LengthExpression n);

  public Exp visit(LessExpression n);

  public Exp visit(ListExpression n);

  public Exp visit(Main n);

  public Exp visit(MainClass n);

  public Exp visit(MethodDeclaration n);

  public Exp visit(MethodDefinition n);

  public Exp visit(MinusExpression n);

  public Exp visit(MultExpression n);

  public Exp visit(NewIdentifierExpression n);

  public Exp visit(NewIntegerExpression n);

  public Exp visit(NotExpression n);

  public Exp visit(PlusExpression n);

  public Exp visit(PrintStatement n);

  public Exp visit(Program n);

  public Exp visit(Statement n);

  public Exp visit(ThisExpression n);

  public Exp visit(TrueExpression n);

  public Exp visit(Type n);

  public Exp visit(VarDeclaration n);

  public Exp visit(VarDefinition n);

  public Exp visit(WhileStatement n);
}