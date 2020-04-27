package geracao_codigo;
import traducao_intermediario.*;
import traducao_intermediario.Frame.*;
import traducao_intermediario.Temp.*;

public class Codegen {
    Frame frame;
    private Assem.InstrList ilist=null, last=null;

    public Codegen(Frame f) {frame=f;}

    private void emit(Assem.Instr inst) {
        if (last!=null)
            last = last.tail = new Assem.InstrList(inst,null);
        else
            last = ilist = new Assem.InstrList(inst,null);
    }

    TempList L(Temp h, TempList t) {
        return new TempList(h,t);
    } 

    Assem.InstrList codegen(Tree.Stm s) {
        Assem.InstrList l;
        munchStm(s);
        l=ilist;
        ilist=last=null;
        return l;
    }

    private void munchStmSeq(SEQ seq){
        munchStm(seq.left);
        munchStm(seq.right);
    }

    private munchStmLabel(LABEL l){
        String sInstr = l.toString()+":\n";
        emit(new Assem.LABEL(sInstr, l.label));
    }

    private void munchStmJump(JUMP s){
        String sInstr = "j " +s.targets.head.toString()+"\n";
		emit(new OPER(sInstr, null, null, s.targets));
    }

    private void munchStmMove(MEM mem, Stm src){
        String sInstr;
		
		//munchStm(MOVE(MEM(BINOP(PLUS,e1,CONST(i))),e2))
		if(mem.exp instanceof BINOP && ((BINOP)mem.exp).binop == BINOP.PLUS && ((BINOP)mem.exp).right instanceof CONST ){										
			BINOP binop = (BINOP)mem.exp;
			sInstr = "SW 's1, "+ ((CONST)(binop).right).value + "('s0)\n";
			emit(new OPER(sInstr, 
					null,
					new TempList(munchExp(binop.left), 
							new TempList(munchExp(src), null)
							)
					)
			);
		}
		
		//munchStm(MOVE(MEM(BINOP(PLUS,CONST(i),e1)),e2))
		else if(mem.exp instanceof BINOP && ((BINOP)mem.exp).binop == BINOP.PLUS && ((BINOP)mem.exp).left instanceof CONST ){
			BINOP binop = (BINOP)mem.exp;
			sInstr = "SW 's1, "+ ((CONST)(binop).left).value + "('s0)\n";
			emit(new OPER(sInstr, 
					null,
					new TempList(munchExp(binop.right), 
							new TempList(munchExp(src), null)
							)
					)
			);
		}
		
		//munchStm(MOVE(MEM(CONST(i)),e2))
		else if(mem.exp instanceof CONST){
			CONST c = (CONST) mem.exp;
			sInstr = "SW 's0, "+c.value+"($zero)\n";
			emit(new OPER(sInstr,
					null,
					new TempList(munchExp(src), null)
					)
			);
		}
		
		//munchStm(MOVE(MEM(e1),e2))
		else{
			sInstr = "ADD 'd0, 's0, $zero";
			emit(new OPER(sInstr,
					null,
					new TempList(munchExp(mem.exp),	
							new TempList(munchExp(src),null)
							)
					)
			);
		}
    }

    private Temp munchExpMem(MEM mem){
        String sInstr;
		Temp r = new Temp();
		
		//munchExp(MEM(BINOP(PLUS,e1,CONST(i))))
		if(mem.exp instanceof BINOP && ((BINOP)mem.exp).binop == BINOP.PLUS && ((BINOP)mem.exp).right instanceof CONST){
			sInstr = "LW 'd0, " + ((CONST)((BINOP)mem.exp).right).value + "('s0)\n";
			emit(new OPER(sInstr,
					new TempList(r, null),
					new TempList(munchExp(((BINOP)mem.exp).left), null)
					)
			);
		}
		
		//munchExp(MEM(BINOP(PLUS,CONST(i),e1)))
		else if(mem.exp instanceof BINOP && ((BINOP)mem.exp).binop == BINOP.PLUS && ((BINOP)mem.exp).left instanceof CONST){
			sInstr = "LW 'd0, " + ((CONST)((BINOP)mem.exp).left).value + "('s0)\n";
			emit(new OPER(sInstr,
					new TempList(r, null),
					new TempList(munchExp(((BINOP)mem.exp).right), null)
					)
			);
			
		}
		
		//munchExp(MEM(CONST(i)))
		else if (mem.exp instanceof CONST){
			sInstr = "LW 'd0, " + ((CONST)mem.exp).value + "($zero)\n";  
			emit(new OPER(sInstr,
					new TempList(r, null),
					null
					)
			);
		}
		
		//munchExp(MEM(e1))
		else {
			sInstr = "LW 'd0, 0('s0)\n"; 
			emit(new OPER(sInstr,
					new TempList(r, null),
					new TempList(munchExp(mem.exp), null)
					)
			);
		}
		
		return r;
    }

    private Temp munchExpBinop(BINOP exp){
        String sInstr;
		Temp r = new Temp();
		
		if(exp.binop == BINOP.PLUS){
			if(exp.right instanceof CONST){
				sInstr = "ADDI 'd0, 's0, " + ((CONST)exp.right).value + "\n";
				emit(new OPER(sInstr,
						new TempList(r, null),
						new TempList(munchExp(exp.left), null)
						)
				);			
			}
			else if(exp.left instanceof CONST){
				sInstr = "ADDI 'd0, 's0, " + ((CONST)exp.left).value + "\n";
				emit(new OPER(sInstr,
						new TempList(r, null),
						new TempList(munchExp(exp.right), null)
						)
				);
			}
			else{
				sInstr = "ADD 'd0, 's0, 's1\n";
				emit(new OPER(sInstr,
						new TempList(r, null),
						new TempList(munchExp(exp.left), new TempList(munchExp(exp.right), null))
						)
				);
			}
		}
        return r;
    }

    private Temp munchExpConst(CONST e1){
        Temp r = new Temp();
        emit(new OPER("ADDI 'd0 <- r0+" + i + "\n", null, L(munchExp(e1),null)));
        return r;
    }

    // checar retorno vê se tá ok
    private Temp munchExpCall(CALL c){
        Temp r = munchExp(c.func); 
        TempList l = munchArgs(0, c.args);
        emit(new OPER("CALL 's0\n",calldefs,L(r,l)));
        return r;
    }

    private Temp munchExpTemp(TEMP t){
        return t.temp;
    }

    void munchStm(Tree.Stm s) {
        if(s instanceof SEQ){
            munchStmSeq((SEQ) s);
        }

        else if(s instanceof LABEL){
            munchStmLabel((LABEL) s);
        }

        else if(s instanceof JUMP){
            munchStmJump((JUMP) s);
        }

        else if(s instanceof MOVE && ((MOVE)s).dst instanceof MEM){
            MOVE move = (MOVE) s;
            munchStmMove((MEM)move.dst, move.src);
        }

        else if(s instanceof MOVE && ((MOVE)s).dst instanceof TEMP){
            MOVE move = (MOVE) s;
            munchStmMove((TEMP)move.dst, move.src);
        }
    }

    Temp.Temp munchExp(Tree.Expr s) {
        if(e instanceof MEM){
            return munchExpMem((MEM)e);
        }

        else if(e instanceof BINOP){
            return munchExpBinop((BINOP)e);
        }

        else if(e instanceof CONST){
            return munchExpConst((CONST)e);
        }

        else if(e instanceof CALL)
            return munchExpCall((CALL)e);

        else{
            return munchExpTemp((TEMP)e);
        }
    } 

    void munchMove(MEM dst, Expr src) {
        // MOVE(MEM(BINOP(PLUS, e1, CONST(i))), e2)
        if (dst.exp instanceof BINOP && ((BINOP)dst.exp).oper==BINOP.PLUS
        && ((BINOP)dst.exp).right instanceof CONST)
        {munchExp(((BINOP)dst.exp).left); munchExp(src); emit("STORE");}
        // MOVE(MEM(BINOP(PLUS, CONST(i), e1)), e2)
        else if (dst.exp instanceof BINOP && ((BINOP)dst.exp).oper==BINOP.PLUS
        && ((BINOP)dst.exp).left instanceof CONST)
        {munchExp(((BINOP)dst.exp).right); munchExp(src); emit("STORE");}
        // MOVE(MEM(e1), MEM(e2))
        else if (src instanceof MEM)
        {munchExp(dst.exp); munchExp(((MEM)src).exp); emit("MOVEM");}
        // MOVE(MEM(e1, e2)
        else
        {munchExp(dst.exp); munchExp(src); emit("STORE");}
       }
       void munchMove(TEMP dst, Expr src) {
        // MOVE(TEMP(t1), e)
        munchExp(src); emit("ADD");
       }
       void munchMove(Expr dst, Expr src) {
        // MOVE(d, e)
        if (dst instanceof MEM) munchMove((MEM)dst,src);
        else if (dst instanceof TEMP) munchMove((TEMP)dst,src);
       }
}
