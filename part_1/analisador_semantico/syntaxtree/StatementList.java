package analisador_semantico.syntaxtree;
import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class StatementList {
   private Vector<Statement> statementList;

   public StatementList() {
      statementList = new Vector<Statement>();
   }

   public void addElement(Statement pStatement) {
      statementList.addElement(pStatement);
   }

   public Statement elementAt(int pElement)  { 
      return (Statement) statementList.elementAt(pElement); 
   }

   public int size() { 
      return statementList.size(); 
   }
}