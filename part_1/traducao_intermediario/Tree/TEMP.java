package traducao_intermediario.Tree;
import traducao_intermediario.Temp.*;

public class TEMP extends Expr {
    public Temp temp;

    public TEMP(Temp t) {temp=t;}
    
    public ExpList kids() {return null;}
    
    public Expr build(ExpList kids) {return this;}
}
