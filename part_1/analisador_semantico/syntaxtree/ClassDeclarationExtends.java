package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class ClassDeclarationExtends extends ClassDeclaration {
    public Identifier i;
    public Identifier j;
    public VarDefinitionList vl;
    public MethodDeclarationList ml;

    public ClassDeclarationExtends(Identifier ai, Identifier aj, VarDefinitionList avl, MethodDeclarationList aml) {
        i = ai;
        j = aj;
        vl = avl;
        ml = aml;
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