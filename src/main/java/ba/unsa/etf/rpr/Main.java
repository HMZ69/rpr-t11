package ba.unsa.etf.rpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static String ispisiGradove() throws SQLException {
        GeografijaDAO geografija = GeografijaDAO.getInstance();

        String rvalue = "";
        for (Grad grad : geografija.gradovi()) {
            rvalue += grad.getNaziv() + " (" + grad.getDrzava().getNaziv() + ") - " + grad.getBroj_stanovnika() + "\n";
        }

        return rvalue;
    }

    public static void glavniGrad() throws SQLException {
        Scanner ulaz = new Scanner(System.in);
        System.out.println("Unesite naziv države: ");
        String input = ulaz.nextLine();
        Grad grad = GeografijaDAO.getInstance().glavniGrad(input);
        if (grad != null)
            System.out.println("Glavni grad drzžave " + input + " je " + grad.getNaziv());
        else
            System.out.println("Nepostojeća država");
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
    }
}
