package analisador_semantico.syntaxtree;
import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class FormalList {
   private Vector<Formal> formalList;

   public FormalList() {
      formalList = new Vector<Formal>();
   }

   public void addElement(Formal pFormal) {
      formalList.addElement(pFormal);
   }

   public Formal elementAt(int pElement)  { 
      return (Formal)formalList.elementAt(pElement); 
   }

   public int size() { 
      return formalList.size(); 
   }
}
