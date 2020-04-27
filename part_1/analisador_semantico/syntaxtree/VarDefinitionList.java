package analisador_semantico.syntaxtree;
import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class VarDefinitionList {
   private Vector<VarDeclaration> list;

   public VarDefinitionList() {
      list = new Vector<VarDeclaration>();
   }

   public void addElement(VarDeclaration n) {
      list.addElement(n);
   }

   public VarDeclaration elementAt(int i)  { 
      return (VarDeclaration) list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}