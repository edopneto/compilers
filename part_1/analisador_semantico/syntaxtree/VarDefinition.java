package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.visitors.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Translate.*;

public class VarDefinition extends VarDeclaration {
    public Type type;
    public Identifier identifier;

    public VarDefinition(Type pType, Identifier pIdentifier) {
        type = pType;
        identifier = pIdentifier;
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