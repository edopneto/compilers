package traducao_intermediario.Translate;

import traducao_intermediario.Tree.Expr;

public class Exp {
  private Expr exp;

  public Exp(Expr e) {
    exp = e;
  }

  public Expr unEx() {
    return exp;
  }
}
