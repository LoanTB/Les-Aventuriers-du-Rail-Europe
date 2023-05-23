package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.mecanique.data.CarteTransport;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Cette classe présente les éléments appartenant au joueur courant.
 *
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class VueJoueurCourant extends VBox {

    private Label nomJoueur;
    private VBox carteTransport;
    private VBox carteDestination;

    public VueJoueurCourant() {
        nomJoueur = new Label();
        carteTransport = new VBox();
        carteDestination = new VBox();
        getChildren().addAll(nomJoueur,carteTransport,carteDestination);
    }

    ChangeListener<IJoueur> JoueurCourantChange = (observableValue, ancien, courant) -> {
        nomJoueur.setText(courant.getNom());
        carteTransport.getChildren().clear();
        for(ICarteTransport ct : courant.getCartesTransport()){
            carteTransport.getChildren().add(new Label("Couleur: "+ct.getStringCouleur()+", Ancre: "+ct.getAncre()));
        }
        carteDestination.getChildren().clear();
        for (IDestination cd : courant.getDestinations()){
            carteDestination.getChildren().add(new Label(cd.getVilles().toString()));
        }
    };

    public void creerBindings() {
        ((VueDuJeu) getScene().getRoot()).getJeu().joueurCourantProperty().addListener(JoueurCourantChange);
    }

}
