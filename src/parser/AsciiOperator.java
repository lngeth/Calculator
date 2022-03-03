package parser;

/**
 * Class containing the ASCII number of the operator when minus 48.
 * Useful to know if it is an operand or probably a operator
 */
public class AsciiOperator {
    public static final int SPACE = -16;
    public static final int OPEN_PARENTHESIS = -8;
    public static final int CLOSE_PARENTHESIS = -7;
    public static final int MULTIPLICATION = -6;
    public static final int ADDITION = -5;
    public static final int SUBSTRACTION = -3;
    public static final int DIVISION = -1;
    public static final int NEGATIVE = -15;
}