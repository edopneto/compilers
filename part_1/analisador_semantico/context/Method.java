package analisador_semantico.context;

import analisador_semantico.syntaxtree.*;
import analisador_semantico.context.*;
import java.util.HashMap;
import java.util.Map;

public class Method {
    String nome;
    Type tipo;
    String pai;
    Map<Symbol, Type> params;
    Map<Symbol, Type> local;

    public Method(String nome, String pai, Type tipo){
        this.nome = nome;
        this.pai = pai;
        this.tipo = tipo;
        this.params = new HashMap<Symbol, Type>();
        this.local = new HashMap<Symbol, Type>();
    }

    // método para adicionar parametro
    public boolean addParametro(Type tipo, Symbol symbol){
        if(this.params.get(symbol) == null){
            this.params.put(symbol, tipo);
            return true;
        }
        return false;
    }

    // método para adicionar variavel
    public boolean addVariavel(Type tipo, Symbol symbol){
        if(this.local.get(symbol) == null){
            this.local.put(symbol, tipo);
            return true;
        }
        return false;
    }

    // get parametros
    public Map<Symbol, Type> getParams(){
        return this.params;
    }

    // get variaveis
    public Map<Symbol, Type> getVars(){
        return this.local;
    }

    // get parametros
    public Type getParams(Symbol symbol){
        return this.params.get(symbol);
    }

    // get variaveis
    public Type getVars(Symbol symbol){
        return this.local.get(symbol);
    }

    public int getParamsSize() {
        return this.params.size();
    }

    public int getVarSize() {
        return this.local.size();
    }

    public Type getType(){
        return this.tipo;
    }

    public String toString(){
        return this.nome;
    }

    public String getPai(){
        return this.pai;
    }

    public boolean removeVar(Symbol symbol){
        if(this.local.get(symbol) == null)
            return false;
        this.local.remove(symbol);
        return true;
    }

    public boolean removeParam(Symbol symbol){
        if(this.params.get(symbol) == null)
            return false;
        this.params.remove(symbol);
        return true;
    }

}