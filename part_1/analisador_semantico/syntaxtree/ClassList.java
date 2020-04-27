package analisador_semantico.syntaxtree;
import analisador_semantico.syntaxtree.*;
import java.util.Vector;

public class ClassList {
    private Vector<ClassDeclaration> list;

    public ClassList() {
        list = new Vector<ClassDeclaration>();
    }

    public void addElement(ClassDeclaration n) {
        list.addElement(n);
    }

    public ClassDeclaration elementAt(int i)  {
        return (ClassDeclaration)list.elementAt(i);
    }

    public int size() {
        return list.size();
    }
}
