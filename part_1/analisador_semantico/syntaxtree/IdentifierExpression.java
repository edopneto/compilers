package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class IdentifierExpression extends Expression {
    public String s;

    public IdentifierExpression(String s) {
        this.s = s;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }

    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }

    public String toString() {
        return s;
    }

    public Exp accept(VisitorIR v) {
        return v.visit(this);
    }
}