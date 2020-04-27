package traducao_intermediario.Mips;

import traducao_intermediario.Tree.*;
import traducao_intermediario.Frame.*;

public class InFrame extends Access {
    int offset;

    InFrame(int o) {
        offset = o;
    }

    public Expr exp(Expr fp) {
        return new MEM(new BINOP(BINOP.PLUS, fp, new CONST(offset)));
    }

    public String toString() {
        Integer offset = new Integer(this.offset);
        return offset.toString();
    }
}