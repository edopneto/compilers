package analisador_semantico.context;

import analisador_semantico.context.*;
import java.util.HashMap;
import java.util.Map;

public class MainContext {
    String nome;
    Map<Symbol, ClassContext> classes;

    public MainContext(String nome){
        this.nome = nome;
        this.classes = new HashMap<Symbol, ClassContext>();
    }



    public MainContext(){
        this.classes = new HashMap<Symbol, ClassContext>();
    }

    // método para adicionar classe
    public boolean addClasse(ClassContext classe, Symbol simbol){
        if(this.classes.get(simbol) == null){
            this.classes.put(simbol, classe);
            return true;
        }
        return false;
    }

     // get classes
     public Map<Symbol, ClassContext> getClasses(){
        return this.classes;
    }

    // get classe
    public ClassContext getClasses(Symbol symbol){
        return this.classes.get(symbol);
    }

    public String toString(){
        return this.nome;
    }

    public boolean removeClass(Symbol symbol){
        if(this.classes.get(symbol) == null)
            return false;
        this.classes.remove(symbol);
        return true;
    }
}                              