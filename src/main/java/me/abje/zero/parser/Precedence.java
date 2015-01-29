package me.abje.zero.parser;

public class Precedence {
    public static final int CALL = 10;
    public static final int PREFIX = 9;  // ! -
    public static final int PRODUCT = 8;  // * / %
    public static final int SUM = 7;  // + -
    public static final int EXPONENT = 6;  // ^
    public static final int COMPARISON = 5;  // < > <= >=
    public static final int EQUALITY = 4;  // == !=
    public static final int LOGICAL = 3;  // && ||
    public static final int RECORD = 2;  // ,
    public static final int ASSIGNMENT = 1;
}
