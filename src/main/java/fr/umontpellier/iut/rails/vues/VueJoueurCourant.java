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
import javafx.scene.layout.VBox;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends VBox { // TODO Quentin afficher les avatars

    private Label nomJoueur;
    private VBox carteTransport;
    private VBox carteDestination;
    private ImageView avatar;
    private ScrollPane sp;

    public VueJoueurCourant() {
        nomJoueur = new Label();

        carteTransport = new VBox();

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

        getChildren().addAll(avatar,nomJoueur,sp,carteDestination);
        this.setSpacing(10.0);
        this.setAlignment(Pos.CENTER);
    }

    ChangeListener<IJoueur> JoueurCourantChange = (observableValue, ancien, courant) -> {
        String couleurAvatar = "avatar-" + courant.getCouleur() + ".png";
        Image portrait = new Image("images/cartesWagons/" + couleurAvatar);
        avatar.setImage(portrait);

        String carte = new String();

        nomJoueur.setText(courant.getNom());
        nomJoueur.setStyle("-fx-text-fill: white");
        carteTransport.getChildren().clear();

        for(ICarteTransport ct : courant.getCartesTransport()) {
            if (ct.estWagon()) {
                carte = "carte-WAGON-";
            } else if (ct.estBateau()) {
                carte = "carte-BATEAU-";
            } else if (ct.estDouble()) {
                carte = "carte-DOUBLE-";
            } else if (ct.estJoker()) {
                carte = "carte-JOKER-";
            }
            carte += ct.getStringCouleur();
            if (ct.getAncre()) {
                carte += "-A";
            }
            ImageView carteT = new ImageView("images/cartesWagons/" + carte + ".png");
            carteT.setFitHeight(90.625);
            carteT.setFitWidth(145.0);
            Button bct= new Button();
            bct.setGraphic(carteT);
            carteTransport.getChildren().add(bct);
            bct.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("Carte choisie");
                }
            });
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