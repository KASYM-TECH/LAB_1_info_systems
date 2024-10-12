package lab4.exception;

public class UserDoesNotExistException extends Exception{
    public UserDoesNotExistException(){
        super("User with entered username does not exist!");
    }
}
