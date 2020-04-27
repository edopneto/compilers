package analisador_semantico.visitors;
import analisador_semantico.syntaxtree.*;
import analisador_semantico.context.*;

import java.awt.im.spi.InputMethodContext;
import java.util.*;

public class TypeChecking implements TypeVisitor{
    private ClassContext currClass;
    private Method currMethod;
    private MainContext programTable;
    private ErrorContext errorMsg;

    public TypeChecking(MainContext pProgramT){
        programTable = pProgramT;
    }

    public Type visit(Program n){
        n.mainClass.accept(this);
        for (int i = 0; i < n.classList.size(); i++) {
            n.classList.elementAt(i).accept(this);
        }
        return null;
    }

    public Type visit(Main n){
        n.identifier1.accept(this);
        currClass = programTable.getClasses(Symbol.symbol(n.identifier1.toString()));
        n.identifier2.accept(this);
        currMethod = currClass.getMethods(Symbol.symbol(n.identifier2.toString()));
        n.statement.accept(this);
        return null;
    }

    public Type visit(ClassSimple pClassSimple){
        pClassSimple.identifier1.accept(this);
        currClass = programTable.getClasses(Symbol.symbol(pClassSimple.identifier1.toString()));
        currMethod = null;
        for (int i = 0; i < pClassSimple.varDeclaration.size(); i++ ){
            pClassSimple.varDeclaration.elementAt(i).accept(this);
        }
        for (int i = 0; i < pClassSimple.methodDeclaration.size(); i++ ){
            pClassSimple.methodDeclaration.elementAt(i).accept(this);
        }
        return null;

    }

    public Type visit(ClassDeclarationExtends pClassDecExt){

        pClassDecExt.i.accept(this);
        currClass = programTable.getClasses(Symbol.symbol(pClassDecExt.i.toString()));
        pClassDecExt.j.accept(this);
        currMethod = null;
        for (int i = 0; i < pClassDecExt.vl.size(); i++ ){
            pClassDecExt.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < pClassDecExt.ml.size(); i++ ){
            pClassDecExt.ml.elementAt(i).accept(this);
        }
        return null;

    }

    public Type visit(MethodDefinition n){

        n.type.accept(this);
        n.identifier.accept(this);
        currMethod = currClass.getMethods(Symbol.symbol(n.identifier.toString()));
        for (int i = 0; i < n.formalList.size(); i++ ){
            n.formalList.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.varDefinitionList.size(); i++ ){
            n.varDefinitionList.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.statementList.size(); i++ ){
            n.statementList.elementAt(i).accept(this);
        }
        Type typeE = n.expression.accept(this); // O tipo que expressão retornou
        if (!(typeE.toString()).equals(n.type.toString())){
            errorMsg.complain("Tipo de retorno da expressão não é compativel com o retorno do método");
        }
        return null;
    }

    public Type visit(Formal n){

        n.identifier.accept(this);
        n.type.accept(this);
        return null;

    }

    public Type visit(BooleanType n){

        return new BooleanType();

    }

    public Type visit(IntegerType n){

        return new IntegerType();

    }

    public Type visit(IdentifierType n) {

        return n;

    }

    public Type visit(BlockStatement n) {
        for ( int i = 0; i < n.s1.size(); i++ ) {
            n.s1.elementAt(i).accept(this);
        }
        return null;

    }


    public Type visit(ArrayType n){

        return new ArrayType();

    }


    public Type visit(VarDefinition n){

        n.type.accept(this);
        n.identifier.accept(this);
        return null;

    }
    public Type visit(AndExpression n){

        if(! (n.e1.accept(this) instanceof BooleanType) ){
            errorMsg.complain ("Lado esquerdo do operador && deve ser do tipo Boolean");
        }
        if(! (n.e2.accept(this) instanceof BooleanType) ){
            errorMsg.complain ("Lado direito do operador && deve ser do tipo Boolean");
        }
        return new BooleanType();

    }

    public Type visit(AssignStatement pAssignStatement) {
        Type tipo;
        Type exp = pAssignStatement.e.accept(this);

        if ((currMethod != null) ){
            if (currMethod.getVars(Symbol.symbol(pAssignStatement.id.toString()))!=null) {
                tipo = currMethod.getVars(Symbol.symbol(pAssignStatement.id.toString()));
                if (exp == null || ! tipo.toString().equals(exp.toString())) {
                    errorMsg.complain("Os tipos do Assign não são compativeis");
                }
            }
            if (currMethod.getParams(Symbol.symbol(pAssignStatement.id.toString()))!=null) {
                tipo = currMethod.getParams(Symbol.symbol(pAssignStatement.id.toString()));
                if (exp == null || ! tipo.toString().equals(exp.toString())) {
                    errorMsg.complain("Os tipos do Assign não são compativeis");
                }
            }
        }

        if ((currClass != null) && currClass.getVars(Symbol.symbol(pAssignStatement.id.toString()))!=null) {
            tipo = currClass.getVars(Symbol.symbol(pAssignStatement.id.toString()));
            if (exp == null || ! tipo.toString().equals(exp.toString())) {
                errorMsg.complain("Os tipos do Assign não são compativeis");
            }
        }
        if (programTable.getClasses(Symbol.symbol(pAssignStatement.id.toString())) != null) {
            tipo = new IdentifierType(pAssignStatement.id.toString());
            ClassContext nomeClasse = programTable.getClasses(Symbol.symbol( ((IdentifierType) tipo).toString()));
            if (nomeClasse == null || !nomeClasse.toString().equals(exp.toString()))
                errorMsg.complain("Os tipos do Assign não são compativeis");
        }

        return null;
    }

    public Type visit(BigExpression n) {
        Type exp = n.e1.accept(this);
        if(exp == null ||  !(exp instanceof IdentifierType) ) {
            errorMsg.complain ("Classe do objeto é inexistente.");
        }
        IdentifierType exp1 = (IdentifierType) exp;
        ClassContext classeExp = programTable.getClasses(Symbol.symbol(exp1.toString()));
        Method metodoId = classeExp.getMethods(Symbol.symbol(n.id1.toString()));
        if(metodoId == null) {
            errorMsg.complain ("Método não encontrado.");
        }
        if(n.el.size() != metodoId.getParamsSize()) {
            errorMsg.complain ("Quantidade de parametros inconsistente.");
        }

        Set chavesParam =  metodoId.getParams().entrySet();
        Symbol s;
        Map.Entry<Symbol, Type> me;
        Iterator it = chavesParam.iterator();
        for(int i = 0; i < n.el.size(); i++) {
            Type tipoChamado = n.el.elementAt(i).accept(this);
            me = (Map.Entry<Symbol, Type>) it.next();
            s = (Symbol) me.getKey();
            Type tipoParam = metodoId.getParams(s);

            if(tipoChamado instanceof IdentifierType) {
                IdentifierType tipo1 = (IdentifierType) tipoChamado;
                ClassContext classeChamada = programTable.getClasses(Symbol.symbol(tipo1.toString()));
                if(tipoParam.toString().equals("IdentifierType")) {
                    if(classeChamada == null || !classeChamada.toString().equals( ((IdentifierType) tipoParam).toString())) {
                        errorMsg.complain ("Tipo do argumento passado:" + classeChamada.toString() + "não compatível com o tipo esperado:" + tipoParam.toString());
                    }
                }
                else {
                    errorMsg.complain ("Tipo do argumento passado:" + classeChamada.toString() + "não compatível com o tipo esperado:" + tipoParam.toString());
                }
            }
            else if (tipoChamado == null || !tipoChamado.toString().equals(tipoParam.toString()) ) {
                errorMsg.complain ("Tipo do argumento passado:" + tipoChamado.toString() + "não compatível com o tipo esperado:" + tipoParam.toString() );
            }
        }

        return metodoId.getType();
    }


    public Type visit(ArrayAssignStatement pArray) {
        Type var = null;
        Type tipo = null;

        if ((currMethod != null) ){
            if (currMethod.getVars(Symbol.symbol(pArray.id.toString()))!=null) {
                tipo = currMethod.getVars(Symbol.symbol(pArray.id.toString()));
            }
            if (currMethod.getParams(Symbol.symbol(pArray.id.toString()))!=null) {
                tipo = currMethod.getParams(Symbol.symbol(pArray.id.toString()));
            }
        }
        if ((currClass != null) && currClass.getVars(Symbol.symbol(pArray.id.toString()))!=null) {
            tipo = currClass.getVars(Symbol.symbol(pArray.id.toString()));
        }
        if (programTable.getClasses(Symbol.symbol(pArray.id.toString())) != null) {
            tipo = new IdentifierType(pArray.id.toString());
        }

        if(tipo == null) { //
            errorMsg.complain("O array não foi declarado");
        }

        if( !(tipo instanceof IntegerType) ) {
            errorMsg.complain("O array precisa ser inteiro");
        }

        if (! (pArray.e1.accept(this) instanceof IntegerType)) {
            errorMsg.complain("A chave do array precisa ser inteiro");
        }

        if (! (pArray.e2.accept(this) instanceof IntegerType)) {
            errorMsg.complain("O lado direito da atribuição precisa ser inteiro");
        }

        return null;
    }


    public Type visit(LessExpression pLess) {
        System.out.println(pLess.e1.accept(this));
        if (! (pLess.e1.accept(this) instanceof IntegerType))
            errorMsg.complain("Left side of operator < must be integer");
        if (! (pLess.e2.accept(this) instanceof IntegerType))
            errorMsg.complain("Right side of operator < must be integer");
        return new BooleanType();
    }

    public Type visit(IfStatement pIfStatement) {
        if (! (pIfStatement.e.accept(this) instanceof BooleanType))
            errorMsg.complain("If statement condition must be boolean");
        pIfStatement.s1.accept(this);
        pIfStatement.s2.accept(this);
        return null;
    }

    public Type visit(WhileStatement pWhileStatement) {
        if (! (pWhileStatement.e.accept(this) instanceof BooleanType))
            errorMsg.complain("While statement condition must be boolean");
        pWhileStatement.s1.accept(this);
        return null;
    }

    public Type visit(PlusExpression pPlus) {
        if (! (pPlus.e1.accept(this) instanceof IntegerType) )
            errorMsg.complain("Left side of LessThan must be of type integer");
        if (! (pPlus.e2.accept(this) instanceof IntegerType) )
            errorMsg.complain("Right side of LessThan must be of type integer");
        return new IntegerType();
    }

    public Type visit(MinusExpression pMinus) {
        if (! (pMinus.e1.accept(this) instanceof IntegerType) )
            errorMsg.complain("Left side of LessThan must be of type integer");
        if (! (pMinus.e2.accept(this) instanceof IntegerType) )
            errorMsg.complain("Right side of LessThan must be of type integer");
        return new IntegerType();
    }

    public Type visit(MultExpression pMult) {
        if (! (pMult.e1.accept(this) instanceof IntegerType) )
            errorMsg.complain("Left side of LessThan must be of type integer");
        if (! (pMult.e2.accept(this) instanceof IntegerType) )
            errorMsg.complain("Right side of LessThan must be of type integer");
        return new IntegerType();
    }


    public Type visit(ThisExpression n) {
        if (currClass == null)
            errorMsg.complain("Class Environment not found");
        return new IdentifierType(currClass.toString());
    }

    public Type visit(VarDeclaration n){
        return null;
    }

    public Type visit(Type n){
        return null;
    }

    public Type visit(TrueExpression n){
        return new BooleanType();
    }

    public Type visit(Statement n){
        return null;
    }

    public Type visit(PrintStatement n){
        n.e.accept(this);
        return null;
    }

    public Type visit(NotExpression n){
        if(! (n.e1.accept(this) instanceof BooleanType) ){
            errorMsg.complain ("Lado direito do operador deve ser do tipo Boolean");
        }
        return new BooleanType();
    }

    public Type visit(NewIntegerExpression n){
        n.e1.accept(this);
        return new IntegerType();
    }

    public Type visit(NewIdentifierExpression n){
        currClass = programTable.getClasses(Symbol.symbol(n.id.toString()));
        if (currClass == null){
            errorMsg.complain("Classe usada no objeto não existe");
        }
        return new IdentifierType(currClass.toString());
    }

    public Type visit(MethodDeclaration n){
        return null;
    }

    public Type visit(MainClass n){
        return null;
    }

    public Type visit(ListExpression n){
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    public Type visit(LengthExpression n){
        n.e1.accept(this);
        return null;
    }

    public Type visit(IntegerLiteralExpression n){
        return new IntegerType();
    }

    public Type visit(IdentifierExpression n) {
        Type tipo = null;
        if ((currMethod != null) ){
            if (currMethod.getVars(Symbol.symbol(n.toString()))!=null) {
                tipo = currMethod.getVars(Symbol.symbol(n.toString()));
            }
            if (currMethod.getParams(Symbol.symbol(n.toString()))!=null) {
                tipo =  currMethod.getParams(Symbol.symbol(n.toString()));
            }
        }
        if ((currClass != null) && currClass.getVars(Symbol.symbol(n.toString()))!=null) {
            tipo = currClass.getVars(Symbol.symbol(n.toString()));
        }
        if (programTable.getClasses(Symbol.symbol(n.toString())) != null) {
            tipo = new IdentifierType(n.toString());
        }

        if(tipo == null) {
            errorMsg.complain("Identificador não encontrado");
        }

        return tipo;
    }

    public Type visit(Identifier n) {

        if ((currMethod != null) ){
            if (currMethod.getVars(Symbol.symbol(n.toString()))!=null) {
                return currMethod.getVars(Symbol.symbol(n.toString()));
            }
            if (currMethod.getParams(Symbol.symbol(n.toString()))!=null) {
                return currMethod.getParams(Symbol.symbol(n.toString()));
            }
        }
        if ((currClass != null) && currClass.getVars(Symbol.symbol(n.toString()))!=null) {
            return currClass.getVars(Symbol.symbol(n.toString()));
        }
        if (programTable.getClasses(Symbol.symbol(n.toString())) != null) {
            return new IdentifierType(n.toString());
        }

        return null;
    }
    
    public Type visit(Goal n){
        return null;
    }
    
    public Type visit(FalseExpression n){
        return null;
    }
    
    public Type visit(Expression n){
        return new IntegerType();
    }
    
    public Type visit(ClassDeclaration n){
        return null;
    }
    
    public Type visit(BlockExpression n){
        n.e1.accept(this);
        return null;
    }
}