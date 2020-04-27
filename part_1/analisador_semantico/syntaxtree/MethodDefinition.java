package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class MethodDefinition extends MethodDeclaration {
  public Type type;
  public Identifier identifier;
  public FormalList formalList;
  public VarDefinitionList varDefinitionList;
  public StatementList statementList;
  public Expression expression;

  public MethodDefinition(Type pType, Identifier pIdentifier, FormalList pFormalList, VarDefinitionList pVarDeclaration,
      StatementList pStatementList, Expression pExpression) {
    type = pType;
    identifier = pIdentifier;
    formalList = pFormalList;
    varDefinitionList = pVarDeclaration;
    statementList = pStatementList;
    expression = pExpression;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public Exp accept(VisitorIR v) {
    return v.visit(this);
  }
}