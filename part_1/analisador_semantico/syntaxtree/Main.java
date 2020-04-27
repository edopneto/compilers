package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class Main extends MainClass {
    public Identifier identifier1, identifier2;
    public Statement statement;

    public Main(Identifier pIdentifier1, Identifier pIdentifier2, Statement pStatement) {
        identifier1 = pIdentifier1;
        identifier2 = pIdentifier2;
        statement = pStatement;
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