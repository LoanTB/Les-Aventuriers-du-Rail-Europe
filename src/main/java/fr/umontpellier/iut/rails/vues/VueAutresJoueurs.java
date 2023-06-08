package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Cette classe présente les éléments des joueurs autres que le joueur courant,
 * en cachant ceux que le joueur courant n'a pas à connaitre.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueAutresJoueurs extends VBox {

    private VBox menuAutreJoueur;


    public VueAutresJoueurs() {
        menuAutreJoueur = new VBox();
    }

    ChangeListener<IJoueur> joueurCourantChange = (observable, oldV, newV) -> {
        IJeu jeu = ((VueDuJeu) getScene().getRoot()).getJeu();
        this.getChildren().clear();
        for (IJoueur joueur : jeu.getJoueurs()) {
            if (joueur != jeu.joueurCourantProperty()) {
                ImageView avatarJoueur = new ImageView();
                String couleurAvatar = "avatar-" + joueur.getCouleur() + ".png";
                Image portrait = new Image("images/cartesWagons/" + couleurAvatar);
                avatarJoueur.setImage(portrait);
                avatarJoueur.setFitHeight(60);
                avatarJoueur.setFitWidth(47.43);

                Label nomJoueur = new Label();
                nomJoueur.setText(joueur.getNom());
                nomJoueur.setStyle("-fx-text-fill: white");

                ImageView iconeWagon = new ImageView();
                Image wagon = new Image("images/bouton-pions-wagon.png");
                iconeWagon.setImage(wagon);
                iconeWagon.setFitHeight(25);
                iconeWagon.setFitWidth(25);

                Label nbWagons = new Label();
                IntegerProperty nbPW = joueur.nbPionsWagonsProperty();
                nbWagons.setText("" + nbPW.getValue());
                nbWagons.setStyle("-fx-text-fill: white");

                ImageView iconeBateau = new ImageView();
                Image bateau = new Image("images/bouton-pions-bateau.png");
                iconeBateau.setImage(bateau);
                iconeBateau.setFitHeight(25);
                iconeBateau.setFitWidth(25);

                Label nbBateau = new Label();
                IntegerProperty nbPB = joueur.nbPionsBateauxProperty();
                nbBateau.setText("" + nbPB.getValue());
                nbBateau.setStyle("-fx-text-fill: white");

                HBox nbPionsWagon = new HBox();
                HBox nbPionsBateau = new HBox();
                nbPionsWagon.getChildren().addAll(iconeWagon,nbWagons);
                nbPionsWagon.setSpacing(5);
                nbPionsWagon.setAlignment(Pos.CENTER);
                nbPionsBateau.getChildren().addAll(iconeBateau,nbBateau);
                nbPionsBateau.setSpacing(5);
                nbPionsBateau.setAlignment(Pos.CENTER);

                Label score = new Label();
                int sc = joueur.getScore();
                score.setText("Scrore : " + sc);
                score.setStyle("-fx-text-fill: white");

                VBox avatarEtNom = new VBox();
                VBox scoreEtPions = new VBox();
                avatarEtNom.getChildren().addAll(avatarJoueur,nomJoueur);
                avatarEtNom.setAlignment(Pos.CENTER);
                scoreEtPions.getChildren().addAll(nbPionsWagon,nbPionsBateau,score);

                HBox infosJoueur = new HBox();
                infosJoueur.getChildren().addAll(avatarEtNom,scoreEtPions);
                infosJoueur.setSpacing(50);

                this.getChildren().add(infosJoueur);
                this.setSpacing(10);
            }
        }
    };

    public void creerBindings() {
        ((VueDuJeu) getScene().getRoot()).getJeu().joueurCourantProperty().addListener(joueurCourantChange);
    }
}
