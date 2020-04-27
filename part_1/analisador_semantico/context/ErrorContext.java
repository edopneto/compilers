package analisador_semantico.context;

public class ErrorContext {
    boolean anyErrors;
    
    public void complain(String msg) {
        anyErrors = true;
        System.out.println(msg);
    }
}