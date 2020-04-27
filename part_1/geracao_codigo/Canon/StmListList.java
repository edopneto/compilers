package geracao_codigo.Canon;

import traducao_intermediario.Tree.*;

public class StmListList {
  public StmList head;
  public StmListList tail;

  public StmListList(StmList h, StmListList t) {
    head = h;
    tail = t;
  }
}
