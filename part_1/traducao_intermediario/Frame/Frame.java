package traducao_intermediario.Frame;

import traducao_intermediario.Tree.*;
import analisador_semantico.context.*;
import traducao_intermediario.Temp.*;
import traducao_intermediario.Frame.*;
import geracao_codigo.*;
import java.util.List;

public abstract class Frame implements TempMap {
    public Label name;
    public List<Access> formals;

    public abstract Frame newFrame(Symbol name, List<Boolean> formals);

    public abstract Access allocLocal(boolean escape);

    public abstract Temp FP();

    public abstract int wordSize();

    public abstract Expr externalCall(String func, List<Expr> args);

    public abstract Temp RV();

    public abstract String string(Label label, String value);

    public abstract Label badPtr();

    public abstract Label badSub();

    public abstract String tempMap(Temp temp);

    public abstract List<Assem.Instr> codegen(List<Stm> stms);

    public abstract void procEntryExit1(List<Stm> body);

    public abstract void procEntryExit2(List<Assem.Instr> body);

    public abstract void procEntryExit3(List<Assem.Instr> body);

    public abstract Temp[] registers();

    public abstract void spill(List<Assem.Instr> insns, Temp[] spills);

    public abstract String programTail(); // append to end of target code
}
