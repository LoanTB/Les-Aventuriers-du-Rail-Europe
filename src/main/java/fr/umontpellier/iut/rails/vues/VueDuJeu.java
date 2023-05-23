package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Cette classe correspond à la fenêtre principale de l'application.
 *
 * Elle est initialisée avec une référence sur la partie en cours (Jeu).
 *
 * On y définit les bindings sur les éléments internes qui peuvent changer
 * (le joueur courant, les cartes Transport visibles, les destinations lors de l'étape d'initialisation de la partie, ...)
 * ainsi que les listeners à exécuter lorsque ces éléments changent
 */
public class VueDuJeu extends VBox {

    private final IJeu jeu;
    private VuePlateau plateau;
    private Button passer;
    private Label instruction;
    private VBox destinationsInitiales;
    private VueJoueurCourant vueJoueurCourant;

    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        plateau = new VuePlateau();
        passer = new Button("Passer");
        instruction = new Label();
        destinationsInitiales = new VBox();
        vueJoueurCourant = new VueJoueurCourant();
        getChildren().addAll(vueJoueurCourant,instruction,destinationsInitiales,passer);
        //getChildren().add(plateau);
    }

    final ListChangeListener<IDestination> destinationsInitialesChanges = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (IDestination destination : change.getAddedSubList()) {
                    Button buttonDestination = new Button(destination.getVilles().toString());
                    buttonDestination.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> destinationsInitiales.getChildren().remove(buttonDestination));
                    destinationsInitiales.getChildren().add(buttonDestination);
                }
            }
            if (change.wasRemoved()) {
                for (IDestination destination : change.getRemoved()) {
                    for (Node n : destinationsInitiales.getChildren()){
                        if (((Button) n).getText().equals(destination.getVilles().toString())){
                            destinationsInitiales.getChildren().remove(n);
                            break;
                        }
                    }
                }
            }
        }
    };

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        jeu.destinationsInitialesProperty().addListener(destinationsInitialesChanges);
        passer.addEventHandler(MouseEvent.MOUSE_CLICKED, actionPasserParDefaut);
        instruction.textProperty().bind(jeu.instructionProperty());
        vueJoueurCourant.creerBindings();
        //plateau.creerBindings();
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> getJeu().passerAEteChoisi());

}
