package appli;

import parser.StringParser;

import java.util.Scanner;

public class AppliCalculator {
    private static final Scanner clavier = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Lancement de la calculatrice... \nVeuillez entrer votre calcul : (écrivez exit pour quitter) \n");
        String calculString;
        try {
            while (!(calculString = clavier.nextLine()).equals("exit")) {
                double res = calculator(calculString);
                System.out.println("Votre résultat = " + res);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        clavier.close();
        System.out.println("Fermeture...");
    }

    public static double calculator(String calculString) throws Exception {
        if (!StringParser.verifyStringEntry(calculString)) {
            throw new Exception("Erreur au niveau de votre entrée ! Vérifier que toutes vos parenthèses sont fermées...");
        }
        return StringParser.evaluatePostfixe(StringParser.returnPostfixe(StringParser.convertStrToInt(calculString)));
    }

}