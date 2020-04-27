package analisador_semantico.syntaxtree;
import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class ExpList {
   private Vector<Expression> list;

   public ExpList() {
      list = new Vector<Expression>();
   }

   public void addElement(Expression n) {
      list.addElement(n);
   }

   public Expression elementAt(int i)  { 
      return (Expression) list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}