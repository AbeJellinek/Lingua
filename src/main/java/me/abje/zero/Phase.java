package me.abje.zero;

public interface Phase<T, U> {
    public U next(T input);
}
