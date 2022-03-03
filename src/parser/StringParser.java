package parser;

import java.util.ArrayList;

public class StringParser {
    /**
     * Return the ASCII code of a char
     * @param c character to convert to ASCII
     * @return the ASCII code in int
     */
    public static int getAsciiValueOfChar(char c) {
        return ((int) c) - 48;
    }

    /**
     * Check if a expression formatted in string is a good mathematical expression (as much closing parenthesis as opening)
     * @param stringCalcul the string to verify
     * @return true if it is conform, false if not
     */
    public static boolean verifyStringEntry(String stringCalcul) {
        int nbParenthesisOpen = 0;
        int nbParenthesisClose = 0;
        for (int i = 0; i < stringCalcul.length(); i++) {
            int asciiValue = getAsciiValueOfChar(stringCalcul.charAt(i));
            if (asciiValue > 9) { // pas un nombre, ni un opérateur
                return false;
            }
            if (asciiValue == AsciiOperator.OPEN_PARENTHESIS)
                nbParenthesisOpen++;
            if (asciiValue == AsciiOperator.CLOSE_PARENTHESIS)
                nbParenthesisClose++;
        }
        return nbParenthesisOpen == nbParenthesisClose;
    }

    /**
     * Check if a ASCII code is an operator
     * @param nbASCII if under 0, it is probably a operator
     * @return true if it is operator, false if not
     */
    public static boolean isOperator(int nbASCII) {
        return nbASCII == AsciiOperator.MULTIPLICATION || nbASCII == AsciiOperator.DIVISION || nbASCII == AsciiOperator.ADDITION || nbASCII == AsciiOperator.SUBSTRACTION;
    }

    /**
     * Convert the string to analyse to a list of single element (also called token)
     * @param stringCalcul the string to convert
     * @return an ArrayList of token
     */
    public static ArrayList<Integer> convertStrToInt(String stringCalcul) {
        int stringLength = stringCalcul.length();
        ArrayList<Integer> tabAsciiOfString = new ArrayList<Integer>();
        int nbToInsert = 0;
        int lastAddedElement = 0;

        for (int i = 0; i < stringLength; i++) {
            int chiffre = getAsciiValueOfChar(stringCalcul.charAt(i));
            switch (chiffre) {
                case AsciiOperator.SPACE: // espace
                    break;
                case AsciiOperator.OPEN_PARENTHESIS: // (
                    tabAsciiOfString.add(chiffre);
                    lastAddedElement = chiffre;
                    break;
                case AsciiOperator.CLOSE_PARENTHESIS: // )
                    tabAsciiOfString.add(nbToInsert);
                    tabAsciiOfString.add(chiffre);
                    lastAddedElement = chiffre;
                    nbToInsert = 0;
                    break;
                case AsciiOperator.SUBSTRACTION: // -
                    if ((lastAddedElement == 0 || lastAddedElement == AsciiOperator.OPEN_PARENTHESIS) && nbToInsert == 0) { // first true element of the string
                        tabAsciiOfString.add(AsciiOperator.NEGATIVE);
                        tabAsciiOfString.add(AsciiOperator.MULTIPLICATION);
                    } else {
                        tabAsciiOfString.add(nbToInsert);
                        tabAsciiOfString.add(chiffre);
                        lastAddedElement = chiffre;
                        nbToInsert = 0;
                    }
                    break;
                case AsciiOperator.MULTIPLICATION: // *
                case AsciiOperator.ADDITION: // +
                case AsciiOperator.DIVISION: // /
                    if (tabAsciiOfString.size() > 0) {
                        int lastIndex = tabAsciiOfString.size() - 1;
                        if (tabAsciiOfString.get(lastIndex) == AsciiOperator.CLOSE_PARENTHESIS) {
                            tabAsciiOfString.add(chiffre);
                        } else {
                            tabAsciiOfString.add(nbToInsert);
                            tabAsciiOfString.add(chiffre);
                            nbToInsert = 0;
                        }
                    } else {
                        tabAsciiOfString.add(nbToInsert);
                        tabAsciiOfString.add(chiffre);
                        nbToInsert = 0;
                    }
                    break;
                default:
                    nbToInsert = (nbToInsert * 10) + chiffre;
                    break;
            }
        }
        if (nbToInsert != 0) {
            tabAsciiOfString.add(nbToInsert);
        }
        return tabAsciiOfString;
    }

    /**
     * Return the postfixe expression of a list of token (useful for evaluation of the math expression)
     * @param liste the list of token
     * @return an ArrayList of number representing the postfixe expression
     */
    public static ArrayList<Integer> returnPostfixe(ArrayList<Integer> liste) {
        ArrayList<Integer> pile = new ArrayList<Integer>();
        ArrayList<Integer> postfixe = new ArrayList<Integer>();

        for (int i : liste) {
            if (isOperator(i)) { // si opérateur
                if (isPileEmpty(pile)) { // si aucun élement dans la pile
                    pile.add(i);
                } else {
                    int iPriority = returnPriorityLevel(i);
                    int lastIndex = pile.size() - 1;
                    while (isOperator(pile.get(lastIndex))) { // si c'est une parenthèse, ça sort
                        if (returnPriorityLevel(pile.get(lastIndex)) >= iPriority) {
                            int elementToRemove = pile.get(lastIndex);
                            pile.remove(lastIndex);
                            postfixe.add(elementToRemove);
                        }
                        if (lastIndex == 0)
                            break;
                        lastIndex--;
                    }
                    pile.add(i);
                }
            } else if (i == AsciiOperator.OPEN_PARENTHESIS) {
                pile.add(i);
            } else if (i == AsciiOperator.CLOSE_PARENTHESIS) {
                int lastIndex = pile.size() - 1;
                while (pile.get(lastIndex) != AsciiOperator.OPEN_PARENTHESIS) { // on dépile jusqu'à trouver la parenthèse ouvrante dans la pile
                    int elementToRemove = pile.get(lastIndex);
                    pile.remove(lastIndex);
                    postfixe.add(elementToRemove);
                    lastIndex--;
                }
                pile.remove(lastIndex);
            } else {
                postfixe.add(i);
            }

            // décommenter(commenter) la ligne suivante pour voir(ne plus voir) le processus des piles pour chaque boucle
            // printProcessPile(pile, postfixe, i);
        }
        if (!isPileEmpty(pile)) { // s'il reste des éléments dans la pile, on dépile et mets dans le postfixe
            int y = pile.size() - 1;
            while (!isPileEmpty(pile)) {
                int elementToRemove = pile.get(y);
                pile.remove(y);
                postfixe.add(elementToRemove);
                y--;
            }
        }
        return postfixe;
    }

    /**
     * Useful to see the process of the algorithm to convert list of token to a postfixe expression
     * @param pile The stack used all along to store operator
     * @param postfixe The postfixe expression to render at the end
     * @param element The int element that we are actually analysing
     */
    public static void printProcessPile(ArrayList<Integer> pile, ArrayList<Integer> postfixe, int element) {
        System.out.println("Element : " + element);
        System.out.print("pile : ");
        for (int p : pile) {
            System.out.print(p + " ");
        }
        System.out.print("\npostfixe : ");
        for (int p : postfixe) {
            System.out.print(p + " ");
        }
        System.out.println("\n-------------");
    }

    /**
     * Check if a stack is empty
     * @param pile the stack to analyse
     * @return true if empty, false if not
     */
    public static boolean isPileEmpty(ArrayList<Integer> pile) {
        return pile.size() == 0;
    }

    /**
     * Return the priority of the operator, 2 for multiplication and division, 1 for addition and substraction
     * @param nbASCII the ASCII code of the operator
     * @return 2 if * or /, 1 for + or -
     */
    public static int returnPriorityLevel(int nbASCII) {
        if (nbASCII == AsciiOperator.MULTIPLICATION || nbASCII == AsciiOperator.DIVISION) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Algorithm of evaluation of a postfixe expression
     * @param postfixe the list of token representing the postfixe expression
     * @return a double, the result of the evaluation
     */
    public static double evaluatePostfixe(ArrayList<Integer> postfixe) {
        ArrayList<Double> pile = new ArrayList<Double>();

        for (int nb : postfixe) {
            if (!isOperator(nb)) {
                pile.add((double) nb);
            } else {
                int lastIndex = pile.size() - 1;
                double val1 = pile.get(lastIndex);
                if (val1 == AsciiOperator.NEGATIVE)
                    val1 = -1;
                pile.remove(lastIndex);

                lastIndex = pile.size() - 1;
                double val2 = pile.get(lastIndex);
                if (val2 == AsciiOperator.NEGATIVE)
                    val2 = -1;
                pile.remove(lastIndex);

                switch (nb) {
                    case AsciiOperator.ADDITION:
                        pile.add(val2 + val1);
                        break;

                    case AsciiOperator.SUBSTRACTION:
                        pile.add(val2 - val1);
                        break;

                    case AsciiOperator.DIVISION:
                        pile.add(val2 / val1);
                        break;

                    case AsciiOperator.MULTIPLICATION:
                        pile.add(val2 * val1);
                        break;
                }
            }
        }
        int lastIndex = pile.size() - 1;
        return pile.get(lastIndex);
    }
}