package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends VBox {
    // TODO mettre des symboles de wagon, bateau et port et mettre un nombre à côté de chacun indiquant le nombre d'éléments possédés (pions pour les wagons et bateau) (ports posés pour les ports)
    private Label nomJoueur;
    private VBox carteTransport;
    private VBox carteDestination;
    private ImageView avatar;
    private ScrollPane sp;
    private HBox infos;
    private Button nbPionsWagons;
    private Button nbPionsBateau;

    public VueJoueurCourant() {
        nomJoueur = new Label();

        carteTransport = new VBox();
        carteTransport.setStyle("-fx-alignment: center");

        carteDestination = new VBox();

        avatar = new ImageView();
        avatar.setFitHeight(120.0);
        avatar.setFitWidth(94.86);

        sp = new ScrollPane();
        sp.setContent(carteTransport);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setPrefViewportHeight(350.0);
        sp.setManaged(false);
        sp.setVisible(false);

        ImageView pionWagon = new ImageView("images/bouton-pions-wagon.png");
        pionWagon.setFitHeight(25);
        pionWagon.setFitWidth(25);

        nbPionsWagons = new Button();
        nbPionsWagons.setGraphic(pionWagon); //mettre l'image dans une HBox avec l'appelle à la fonction du nombre de wagons

        ImageView pionBateau = new ImageView("images/bouton-pions-bateau.png");
        pionBateau.setFitHeight(25);
        pionBateau.setFitWidth(25);

        nbPionsBateau = new Button();
        nbPionsBateau.setGraphic(pionBateau); //mettre l'image dans une HBox avec l'appelle à la fonction du nombre de bateaux

        infos = new HBox();
        infos.getChildren().addAll(nbPionsWagons, nbPionsBateau);
        infos.setAlignment(Pos.CENTER);

        getChildren().addAll(avatar,nomJoueur,sp,carteDestination,infos);
        this.setSpacing(10.0);
        this.setAlignment(Pos.CENTER);
    }

    ChangeListener<IJoueur> JoueurCourantChange = (observableValue, ancien, courant) -> {
        String couleurAvatar = "avatar-" + courant.getCouleur() + ".png";
        Image portrait = new Image("images/cartesWagons/" + couleurAvatar);
        avatar.setImage(portrait);

        String carte = "";

        nomJoueur.setText(courant.getNom());
        nomJoueur.setStyle("-fx-text-fill: white");
        carteTransport.getChildren().clear();

        for(int i=0;i<courant.getCartesTransport().size();i+=2) {
            if (i==courant.getCartesTransport().size()-1){
                carteTransport.getChildren().add(Utils.loadCarte(courant.getCartesTransport().get(i), new double[]{78.125,125}));
            } else {
                carteTransport.getChildren().add(new HBox(Utils.loadCarte(courant.getCartesTransport().get(i), new double[]{78.125,125}),Utils.loadCarte(courant.getCartesTransport().get(i+1), new double[]{78.125,125})));
            }
            sp.setManaged(true);
            sp.setVisible(true);
        }

        carteDestination.getChildren().clear();
        for (IDestination cd : courant.getDestinations()){
            Label labelCarteDestination = new Label(cd.getVilles().toString());
            labelCarteDestination.setStyle("-fx-text-fill: white");
            carteDestination.getChildren().add(labelCarteDestination);
        }

    };

    public void creerBindings() {
        ((VueDuJeu) getScene().getRoot()).getJeu().joueurCourantProperty().addListener(JoueurCourantChange);
    }

}