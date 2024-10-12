package lab4.exception;

public class ForbiddenException extends Exception{
    public ForbiddenException(){
        super("User is not allowed!");
    }
}
