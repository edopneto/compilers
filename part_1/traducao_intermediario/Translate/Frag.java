package traducao_intermediario.Translate;

public abstract class Frag {
  private Frag next;

  public Frag(Frag next) {
    this.next = next;
  }

  public void addNext(Frag next) {
    this.next = next;
  }

  public Frag getNext() {
    return next;
  }
}
