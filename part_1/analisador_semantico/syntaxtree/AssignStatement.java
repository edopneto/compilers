package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class AssignStatement extends Statement {
    public Identifier id;
    public Expression e;

    public AssignStatement(Expression e, Identifier id) {
        this.e = e;
        this.id = id;
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