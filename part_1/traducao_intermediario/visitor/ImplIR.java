package analisador_semantico.visitors;

import analisador_semantico.context.*;
import analisador_semantico.syntaxtree.*;
import traducao_intermediario.visitor.*;
import traducao_intermediario.Frame.*;
import traducao_intermediario.Translate.*;
import traducao_intermediario.Tree.*;
import traducao_intermediario.Temp.*;
import traducao_intermediario.Mips.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class ImplIR implements VisitorIR {
    private Frame currFrame;
    private Frag frag_inicio;
    private Frag frags;
    private MainContext mainContext;
    private ClassContext classe;
    private Method method;

    public ImplIR(MainContext maincontext, Frame frame) {
        this.mainContext = maincontext;
        this.currFrame = frame;
        this.frags = new Frag(null);
        this.frag_inicio = frags;
        method = null;
        classe = null;
    }

    public Frag getFrag() {
        return this.frags;
    }

    // MainClass mainClass;
    // ClassList classList;
    @Override
    public Exp visit(Program pProgram) {
        pProgram.mainClass.accept(this);
        for (int i = 0; i < pProgram.classList.size(); i++) {
            pProgram.classList.elementAt(i).accept(this);
        }
        return null;
    }

    @Override
    public Exp visit(ClassSimple pClassDeclaration) {
        currClass = mainContext.getClass(Symbol.symbol(pClassDeclaration.identifier.getValue()));
        currMethod = null;
        for (int i = 0; i < pClassDeclaration.varDeclaration.size(); i++) {
            pClassDeclaration.vld.elementAt(i).accept(this);
        }
        for (int i = 0; i < pClassDeclaration.methodDeclaration.size(); i++) {
            pClassDeclaration.methodDeclaration.elementAt(i).accept(this);
        }
        return null;
    }

    @Override
    public Exp visit(ClassDeclarationExtends pClassDecExt) {
        currClass = mainTable.getClass(Symbol.symbol(pClassDecExt.i.getValue()));
        currMethod = null;
        for (int i = 0; i < pClassDecExt.vl.size(); i++) {
            pClassDecExt.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < pClassDecExt.m1.size(); i++) {
            pClassDecExt.m1.elementAt(i).accept(this);
        }
        return null;
    }
    
    @Override
    public Exp visit(VarDefinition pVarDef) {
        return null;
    }


    public void procEntryExit (Exp body)
    {
        ProcFrag f = new ProcFrag(body, currFrame);
        frags.addNext(f);
        frags = frags.getNext();
    }

    @Override
    public Exp visit(MethodDefinition n) {
        currFrame = new Mips.MipsFrame();
        ArrayList<Boolean> varEscapes = new ArrayList<Boolean>();
        method = currClass.getMethod(Symbol.symbol(n.identifier.toString()));
        for (int i = 0; i < n.formalList.size(); i++) {
            varEscapes.add(false);//false porque no minijava variáveis nao escapam
        }
        currFrame = currFrame.newFrame(Symbol.symbol(n.identifier.toString()), varEscapes);
        //accept para as variaveis do metodo
        for (int i = 0; i < n.varDefinitionList.size(); i++) {
            n.varDefinitionList.elementAt(i).accept(this);
        }

        Tree.Stm body =  null;
        Tree.Exp rExp = null;

        if (!n.statementList.size())
        {
            rExp = n.expression.accept(this);
            body = new MOVE( new Tree.TEMP(currFrame.RV()) , new Tree.ESEQ( null , rtExp ) );
        }
        else
        {
            body = n.statementList.elementAt(0).accept(this);
            for ( int i = 1; i < n.statementList.size(); i++ ) {
                body = new SEQ(body, n.statementList.elementAt(i).accept(this));
            }
            rExp = n.expression.accept(this);
            body = new MOVE( new Tree.TEMP(currFrame.RV()) , new Tree.ESEQ( body , rExp ) );
        }

        body = new Tree.MOVE(new Tree.TEMP(currFrame.RV()), new Tree.ESEQ(body, rExp));
        procEntryExit(body);
        return null;
    }

    @Override
    public Exp visit(Main n) {
        n.identifier1.accept(this);
        classe = mainContext.getClasses(Symbol.symbol(n.identifier1.toString()));
        method = classe.getMethods(Symbol.symbol(n.identifier2.toString()));
        Frame mFrame = currFrame(Symbol.symbol(n.identifier2.toString()));
        ArrayList<Boolean> varEscapes = new ArrayList<Boolean>();
        for (int i = 0; i < n.formalList.size(); i++) {
            varEscapes.add(false);//false porque no minijava variáveis nao escapam
        }
        Frame f = currFrame(Symbol.symbol(n.identifier2.toString()), varEscapes);
        currFrame = f;

        Translate.Exp st = n.statement.accept(this);
        Translate.Exp rExp = new CONST(0);
        Translate.Exp body = new MOVE( new Tree.TEMP(currentFrame.RV()) , new Tree.ESEQ( st , rExp ) );
        procEntryExit(body);
        return null;
    }

    // Expression e1, e2
    @Override
    public Exp visit(AndExpression n) {
        Exp e1 = n.e1.accept(this);
        Exp e2 = n.e2.accept(this);

        BINOP binop = new BINOP(BINOP.PLUS, e1.unEx(), e2.unEx());
        return new Exp(binop);
    }

    // Expression e1, e2
    // Identifier id
    @Override
    public Exp visit(ArrayAssignStatement n) {
        Exp e1 = n.e1.accept(this);
        Exp e2 = n.e2.accept(this);

        // TEMP temp = new TEMP(n.id.toString()); // Vê oq eu faço aquiiiiiiii
        TEMP temp = new TEMP(new Temp()); // TODO: Vê se isso tá certo
        MEM mem = new MEM(temp);
        BINOP binop = new BINOP(BINOP.PLUS, mem, e1.unEx());
        MOVE move = new MOVE(binop, e2.unEx());

        return new Exp(new ESEQ(move, null)); // só pra converter pra Exp

    }

    // nothing
    @Override
    public Exp visit(ArrayType n) {
        return null;
    }

    // Identifier id;
    // Expression e;
    @Override
    public Exp visit(AssignStatement n) {
        Exp e1 = n.e.accept(this);
        TEMP temp = new TEMP(new Temp()); // TODO: Vê se isso tá certo
        MOVE move = new MOVE(temp, e1.unEx());

        return new Exp(new ESEQ(move, null));
    }

    // Expression e1;
    // Identifier id1;
    // ExpList el;
    //TODO
    @Override
    public Exp visit(BigExpression n) {
        n.id1.accept(this);
        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);
        }
    }

    // Expression e1;
    //TODO
    @Override
    public Exp visit(BlockExpression n) {
        n.e1.accept(this);
    }

    // StatementList s1;
    @Override
    public Exp visit(BlockStatement n) {
        Stm atual = new EXP(n.s1.elementAt(0).accept(this).unEx()); // problema de tipo
        Stm prox;
        SEQ seq;

        for (int i = 1; i < n.s1.size(); i++) {
            prox = new EXP(n.s1.elementAt(i).accept(this).unEx());
            seq = new SEQ(seq, prox);
        }
        return new Exp(new ESEQ(seq, null));
    }

    // nothing
    @Override
    public Exp visit(BooleanType n) {
        return null;
    }

    // abstrato
    @Override
    public Exp visit(ClassDeclaration n) {
    }

    // abstrato
    @Override
    public Exp visit(Expression n) {
    }

    // nothing
    @Override
    public Exp visit(FalseExpression n) {
        CONST falseConst = new CONST(0);
        return new Exp(falseConst);
    }

    // Type type;
    // Identifier identifier;
    @Override
    public Exp visit(Formal n) { // TODO: ve se isso aqui é nulo msms
        return null;
    }

    // abstract
    @Override
    public Exp visit(Goal n) {
    }

    // String s;
    //TODO
    @Override
    public Exp visit(Identifier n) {
        TEMP registrador = new TEMP(new Temp());
        return new Exp(registrador);
    }

    // String s;
    //TODO
    @Override
    public Exp visit(IdentifierExpression n) {
        TEMP registrador = new TEMP(new Temp());
        return new Exp(registrador);
    }

    // String s;
    @Override
    public Exp visit(IdentifierType n) {
        return null;
    }

    // Expression e;
    // Statement s1, s2;
    @Override
    public Exp visit(IfStatement n) {
        Exp exp = n.e.accept(this);
        Stm stm1 = new EXP(n.s1.accept(this).unEx());
        Stm stm2 = new EXP(n.s2.accept(this).unEx());

        Label tru = new Label();
        Label fals = new Label();
        Label endif = new Label();

        SEQ falseStm = new SEQ(stm2, new LABEL(fals));
        SEQ trueStm = new SEQ(stm1, new LABEL(tru));
        SEQ seqTrueFalse = new SEQ(trueStm, falseStm);

        CJUMP cjump = new CJUMP(CJUMP.EQ, new CONST(1), exp.unEx(), tru, fals);
        SEQ condicional = new SEQ(new LABEL(endif), cjump);
        SEQ secondSeq = new SEQ(condicional, seqTrueFalse);
        SEQ mainSeq = new SEQ(secondSeq, new LABEL(endif));

        return new Exp(new ESEQ(mainSeq, null));
    }

    // int i;
    @Override
    public Exp visit(IntegerLiteralExpression n) {
        return new Exp(new CONST(n.i));
    }

    // nothing
    @Override
    public Exp visit(IntegerType n) {
        return null;
    }

    // Expression e1;
    @Override
    public Exp visit(LengthExpression n) {

        Exp exp1 = n.e1.accept(this);
        // não precisamos multiplicar por w pois queremos acessar a posição 0. A primeira posição guarda o tamanho do vetor
        MEM mem = new MEM(exp1.unEx());
    }

    // Expression e1, e2;
    @Override
    public Exp visit(LessExpression n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Expression e1, e2;
    @Override
    public Exp visit(ListExpression n) {
        Exp exp1 = n.e1.accept(this);
        Exp exp2 = n.e2.accept(this);

        MEM men3 = new MEM(new BINOP(BINOP.PLUS, exp1.unEx(),
                           new BINOP(BINOP.MUL, exp2.unEx(), new CONST(currFrame.wordSize()))));

        return(new Exp(men3));
    }

    // abstract
    @Override
    public Exp visit(MainClass n) {
    }

    // abstract
    @Override
    public Exp visit(MethodDeclaration n) {
    }

    // Type type;
    // Identifier identifier;
    // FormalList formalList;
    // VarDefinitionList varDefinitionList;
    // StatementList statementList;
    // Expression expression;
    // REGRA DO MÉTODO DECLARATION
    @Override
    public Exp visit(MethodDefinition n) {
        n.type.accept(this);
        n.identifier.accept(this);

        // Criiando o novo método Type Identifier
        Method newMethod = new Method(n.identifier.toString(), classe.toString(), n.type);

        // Aí eu vejo se esse método já está declarado nessa classe atual, se não tiver
        // eu addMethod e pego o metodo atual como esse metodo que criei
        if (!classe.addMethod(newMethod, Symbol.symbol(n.identifier.toString()))) {
            error.complain("Method " + n.identifier.toString() + " already defined.");
        } else {
            method = newMethod;
        }

        // Daí os type identifier posteriores (que estão na nossa lista formallist) são
        // os parametros
        // aí eu faço o accept deles que aí lá eles vão ser adicionados
        for (int i = 0; i < n.formalList.size(); ++i) {
            n.formalList.elementAt(i).accept(this);
        }

        // Daí as variaveis declaradas para esse metodo estão na nossa lista
        // varDefinitionList
        // aí eu faço o accept deles que aí lá eles vão ser adicionados
        for (int i = 0; i < n.varDefinitionList.size(); ++i) {
            n.varDefinitionList.elementAt(i).accept(this);
        }

        // Daí os statement para esse metodo estão na nossa lista statementList
        // aí eu faço o accept deles que aí lá eles vão ser adicionados
        for (int i = 0; i < n.statementList.size(); ++i) {
            n.statementList.elementAt(i).accept(this);
        }

        n.expression.accept(this);
    }

    // Expression e1, e2;
    @Override
    public Exp visit(MinusExpression n) {
        Exp e1 = n.e1.accept(this);
        Exp e2 = n.e2.accept(this);

        BINOP binop = new BINOP(BINOP.MINUS, e1.unEx(), e2.unEx());
        return new Exp(binop);
    }

    // Expression e1, e2;
    @Override
    public Exp visit(MultExpression n) {
        Exp e1 = n.e1.accept(this);
        Exp e2 = n.e2.accept(this);

        BINOP binop = new BINOP(BINOP.MUL, e1.unEx(), e2.unEx());
        return new Exp(binop);
    }

    // Identifier id;
    @Override
    public Exp visit(NewIdentifierExpression n) {
        n.id.accept(this);
    }

    // Expression e1;
    @Override
    public Exp visit(NewIntegerExpression n) {
        /*Exp exp1 = n.e1.accept(this);
        ArrayList<MOVE> moves = new ArrayList<MOVE>();
        TEMP temp = new TEMP(new Temp());
        for (int i = exp1.unEx(); i < 0; i--) {
            BINOP binopMult = new BINOP(BINOP.MUL, new CONST(i), new CONST(currFrame.wordSize()));
            BINOP binopPlus = new BINOP(BINOP.PLUS, temp, binopMult);
            MOVE move = new MOVE(new MEM(binopPlus), new CONST(0));
            moves.add(move);
        }
        // colocar tamanho do vetor no primeiro elemento
        MOVE moveTamanho = new MOVE(new MEM(new BINOP(BINOP.PLUS, temp, new CONST(0))), exp1.unEx());*/
        Exp exp1 = n.e1.accept(this);
        //Adicionar 1 ao tamanho para guardar o tamanho do array
        BINOP binop1 = new BINOP(BINOP.PLUS, exp1.unEx(), new CONST(1));
        Expr tamanhoVet = new BINOP(BINOP.MUL, binop1, new CONST(currFrame.wordSize()));
        ArrayList<Expr> args = new ArrayList<>();
        args.add(tamanhoVet);
        Expr alocacao = currFrame.externalCall("alloc", args);
        TEMP t = new TEMP(new Temp());
        MOVE move = new MOVE(t, alocacao);
        //Nao tenho certeza dessa parte
        ESEQ eseq = new ESEQ(move, t);
        return new Exp(eseq);
    }

    // Expression e1;
    // nao tenho certeza
    @Override
    public Exp visit(NotExpression n) {
        Exp exp1 = n.e1.accept(this);
        BINOP binop = new BINOP(BINOP.MINUS, new CONST(1), exp1.unEx());
        return new Exp(binop);
    }

    // Expression e1, e2;
    @Override
    public Exp visit(PlusExpression n) {
        Exp exp1 = n.e1.accept(this);
        Exp exp2 = n.e2.accept(this);

        BINOP binop = new BINOP(BINOP.PLUS, exp1.unEx(), exp2.unEx());
        return new Exp(binop);
    }

    // Expression e;
    @Override
    public Exp visit(PrintStatement n) {
        Exp exp = n.e.accept(this);
        List<Expr> args = new LinkedList<Expr>();
        args.add(exp.unEx());
        Expr ex = currFrame.externalCall("print", args);
        return new Exp(ex);
    }

    // abstract
    @Override
    public Exp visit(Statement n) {
        return null;
    }

    // nothing
    @Override
    public Exp visit(ThisExpression n) {
    }

    // nothing
    @Override
    public Exp visit(TrueExpression n) {
        CONST trueConst = new CONST(1);
        return new Exp(trueConst);
    }

    // abstract
    @Override
    public Exp visit(Type n) {
        return null;
    }

    // abstract
    @Override
    public Exp visit(VarDeclaration n) {
    }

    // Type type;
    // Identifier identifier;
    //TODO
    @Override
    public Exp visit(VarDefinition n) {
        n.type.accept(this);
        n.identifier.accept(this);

        // aqui a gente tem que ver se o this é um método ou classe na vdd
        // pra saber se adicionaremos em um método ou em uma classe
        // aí no caso eu verifico se o método atual é nulo, pq isso significa que
        // teremos que adicionar na classe
        if (method == null) {
            if (!classe.addVariavel(n.type, Symbol.symbol(n.identifier.toString()))) {
                error.complain(
                        "Variavel " + n.identifier.toString() + " already defined in the Class " + classe.toString());
            }
        }
        if (method != null) {
            if (!method.addVariavel(n.type, Symbol.symbol(n.identifier.toString()))) {
                error.complain("Variavel " + n.identifier.toString() + " already defined in the Method "
                        + method.toString() + " Class " + classe.toString());
            }
        }

    }

    // Expression e;
    // Statement s1;
    @Override
    public Exp visit(WhileStatement n) {
        Expr exp = n.e.accept(this).unEx();
        Stm stm = new EXP(n.s1.accept(this).unEx());

        Label loop = new Label();
        Label done = new Label();
        Label body = new Label();

        SEQ bodyStm = new SEQ(stm, new LABEL(body));
        JUMP jump = new JUMP(done);
        SEQ seqExec = new SEQ(bodyStm, jump);
        CJUMP cjump = new CJUMP(CJUMP.EQ, new CONST(1), exp, body, done); // TODO; ve se n troquei a ordem do body e
                                                                          // done
        SEQ seqTeste = new SEQ(new LABEL(loop), cjump);
        SEQ secondSeq = new SEQ(seqTeste, seqExec);
        SEQ mainSeq = new SEQ(secondSeq, new LABEL(done));

        return new Exp(new ESEQ(mainSeq, null));
    }

}