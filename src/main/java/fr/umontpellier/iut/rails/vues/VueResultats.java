package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.RailsIHM;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Cette classe affiche les scores en fin de partie.
 * On peut éventuellement proposer de rejouer, et donc de revenir à la fenêtre principale
 *
 */
public class VueResultats extends Pane {

    private RailsIHM ihm;

    public VueResultats(RailsIHM ihm) {
        this.ihm = ihm;
    }

    ChangeListener<IJoueur> TableauDesScores = (observable, oldV, newV) -> {
        IJeu jeu = ((VueDuJeu) getScene().getRoot()).getJeu();
        ArrayList<IJoueur> classement = new ArrayList<>(jeu.getJoueurs());
        classement.sort(Comparator.comparingInt(IJoueur::getScore).reversed());
        for (int i = 0; i < classement.size(); i++) {
            IJoueur joueur = classement.get(i);

            ImageView avatarJoueur = new ImageView();
            String couleurAvatar = "avatar-" + joueur.getCouleur() + ".png";
            Image portrait = new Image("images/cartesWagons/" + couleurAvatar);
            avatarJoueur.setImage(portrait);
            avatarJoueur.setFitHeight(60);
            avatarJoueur.setFitWidth(47.43);

            Label nomJoueur = new Label();
            nomJoueur.setText(joueur.getNom());
            nomJoueur.setStyle("-fx-text-fill: white");

            Label score = new Label();
            int sc = joueur.getScore();
            score.setText("Scrore : " + sc);
            score.setStyle("-fx-text-fill: white");

            VBox avatarEtNom = new VBox();
            avatarEtNom.getChildren().addAll(avatarJoueur, nomJoueur);
            avatarEtNom.setAlignment(Pos.CENTER);

            Label top = new Label("#"+(i+1));
            top.setStyle("-fx-font-size: 30; -fx-text-fill: White");

            HBox infosJoueur = new HBox();
            infosJoueur.getChildren().addAll(avatarEtNom,score,top);
            infosJoueur.setSpacing(20);
            infosJoueur.setAlignment(Pos.CENTER);
            switch (joueur.getCouleur()) {//Utils.reverseString(Utils.reverseString(getScene().getRoot().getStyle()).substring(2,9))
                case BLEU ->
                        infosJoueur.setBackground(new Background(new BackgroundFill(Color.valueOf("#00036b"), new CornerRadii(10), null)));
                case ROSE ->
                        infosJoueur.setBackground(new Background(new BackgroundFill(Color.valueOf("#8e0078"), new CornerRadii(10), null)));
                case ROUGE ->
                        infosJoueur.setBackground(new Background(new BackgroundFill(Color.valueOf("#8e0000"), new CornerRadii(10), null)));
                case VERT ->
                        infosJoueur.setBackground(new Background(new BackgroundFill(Color.valueOf("#008e06"), new CornerRadii(10), null)));
                case JAUNE ->
                        infosJoueur.setBackground(new Background(new BackgroundFill(Color.valueOf("#8c8e00"), new CornerRadii(10), null)));
            }
            infosJoueur.setMaxWidth(200);
            infosJoueur.setPadding(new Insets(4));
        }
    };

    public void tableauDesScores() {
        ((VueDuJeu) getScene().getRoot()).getJeu().joueurCourantProperty().addListener(TableauDesScores);
    }

}
