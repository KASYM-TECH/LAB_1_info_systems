package lab4.exception;

public class UsernameOccupiedException extends Exception{
    public UsernameOccupiedException(){
        super("Received username already in use!");
    }
}
