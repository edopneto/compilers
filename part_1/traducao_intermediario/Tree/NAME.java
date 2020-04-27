package traducao_intermediario.Tree;
import traducao_intermediario.Temp.*;

public class NAME extends Expr {
  public Label label;
  
  public NAME(Label l) {label=l;}
  
  public ExpList kids() {return null;}
  
  public Expr build(ExpList kids) {return this;}
}
