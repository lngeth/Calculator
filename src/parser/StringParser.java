package parser;

import java.util.ArrayList;

public class StringParser {
    public static int getAsciiValueOfChar(char c) {
        return ((int) c) - 48;
    }

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

    public static boolean isOperator(int nbASCII) {
        return nbASCII == AsciiOperator.MULTIPLICATION || nbASCII == AsciiOperator.DIVISION || nbASCII == AsciiOperator.ADDITION || nbASCII == AsciiOperator.SUBSTRACTION;
    }

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

    public static boolean isPileEmpty(ArrayList<Integer> pile) {
        return pile.size() == 0;
    }

    public static int returnPriorityLevel(int nbASCII) {
        if (nbASCII == AsciiOperator.MULTIPLICATION || nbASCII == AsciiOperator.DIVISION) {
            return 2;
        } else {
            return 1;
        }
    }

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