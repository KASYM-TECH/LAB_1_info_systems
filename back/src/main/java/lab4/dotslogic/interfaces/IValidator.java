package lab4.dotslogic.interfaces;

@FunctionalInterface
public interface IValidator<T> {
    boolean validate(T value);
}
