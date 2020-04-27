package traducao_intermediario.Mips;

import traducao_intermediario.Tree.*;
import traducao_intermediario.Temp.*;
import traducao_intermediario.Frame.*;

public class InReg extends Access {
    Temp temp;

    InReg(Temp t) {
        temp = t;
    }

    public Expr exp(Expr fp) {
        return new TEMP(temp);
    }

    public String toString() {
        return temp.toString();
    }
}