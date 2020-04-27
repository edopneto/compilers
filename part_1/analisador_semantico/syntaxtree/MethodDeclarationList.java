package analisador_semantico.syntaxtree;

import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class MethodDeclarationList {
   private Vector<MethodDeclaration> list;

   public MethodDeclarationList() {
      list = new Vector<MethodDeclaration>();
   }

   public void addElement(MethodDeclaration n) {
      list.addElement(n);
   }

   public MethodDeclaration elementAt(int i) {
      return (MethodDeclaration) list.elementAt(i);
   }

   public int size() {
      return list.size();
   }
}