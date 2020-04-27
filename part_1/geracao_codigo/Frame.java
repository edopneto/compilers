package geracao_codigo;

import traducao_intermediario.Frame.*;

class Frame extends Frame {
    static TempList returnSink = L(ZERO, L(RA, L(SP, calleeSaves)));
    
    static Assem.InstrList append(Assem.InstrList a, Assem.InstrList b) {
        if (a==null) return b;
        else {
            Assem.InstrList p;
            for(p=a; p.tail!=null; p=p.tail) {}
                p.tail=b;
            return a;
        }
    }
   
    public Assem.InstrList procEntryExit2(Assem.InstrList body) {
        return append(body, new Assem.InstrList(new Assem.OPER("", null, returnSink),null));
    }
} 