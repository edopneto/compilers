package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class BlockExpression extends Expression {
    public Expression e1;

    public BlockExpression(Expression e1) {
        this.e1 = e1;
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