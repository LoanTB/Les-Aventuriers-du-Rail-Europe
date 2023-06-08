package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import javafx.scene.image.ImageView;

public abstract class Utils {
    public static ImageView loadCarte(ICarteTransport carte, double[] taille){
        String s = "";
        if (carte.estWagon()) {
            s = "carte-WAGON-";
        } else if (carte.estBateau()) {
            s = "carte-BATEAU-";
        } else if (carte.estDouble()) {
            s = "carte-DOUBLE-";
        } else if (carte.estJoker()) {
            s = "carte-JOKER-";
        }
        s += carte.getStringCouleur();
        if (carte.getAncre()) {
            s += "-A";
        }
        return loadImage("images/cartesWagons/" + s + ".png",taille);
    }

    public static ImageView loadImage(String path, double[] taille){
        ImageView img = new ImageView(path);
        img.setFitHeight(taille[0]);
        img.setFitWidth(taille[1]);
        return img;
    }

    public static String reverseString(String s){
        StringBuilder r = new StringBuilder(s);
        r.reverse();
        return r.toString();
    }
}
