package traducao_intermediario.Frame;

import traducao_intermediario.Tree.*;

public abstract class Access {
  public abstract String toString();

  public abstract Expr exp(Expr e);
}
