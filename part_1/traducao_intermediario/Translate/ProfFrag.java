package traducao_intermediario.Translate;

import traducao_intermediario.Frame.*;
import traducao_intermediario.Tree.*;

public class ProcFrag extends Frag {
  private Stm body;
  private Frame frame;

  public ProcFrag(Stm b, Frame f) {
    body = b;
    frame = f;
  }
}
