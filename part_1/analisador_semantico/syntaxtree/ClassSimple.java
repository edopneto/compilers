package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class ClassSimple extends ClassDeclaration {
    public Identifier identifier1;
    public VarDefinitionList varDeclaration;
    public MethodDeclarationList methodDeclaration;

    public ClassSimple(Identifier pIdentifier1, VarDefinitionList pVarDeclaration,
            MethodDeclarationList pMethodDeclaration) {
        identifier1 = pIdentifier1;
        varDeclaration = pVarDeclaration;
        methodDeclaration = pMethodDeclaration;
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