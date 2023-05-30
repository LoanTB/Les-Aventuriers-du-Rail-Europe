package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

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
public class VueDuJeu extends HBox {

    private final IJeu jeu;
    private VuePlateau plateau;
    private Button passer;
    private Label instruction;
    private VBox destinationsInitiales;
    private Label choixPionsInitiales;
    private VueJoueurCourant vueJoueurCourant;

    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        plateau = new VuePlateau();
        passer = new Button("Passer");
        passer.setStyle("-fx-background-color: #460101; -fx-text-fill: white");
        instruction = new Label();
        instruction.setStyle("-fx-padding: 0 0 10 0; -fx-text-fill: white");
        instruction.setWrapText(true);
        destinationsInitiales = new VBox();
        destinationsInitiales.setStyle("-fx-alignment: center;-fx-padding: 0 0 25 0");
        choixPionsInitiales = new Label();
        vueJoueurCourant = new VueJoueurCourant();
        vueJoueurCourant.setStyle("-fx-padding: 0 0 15 0");
        Label titre = new Label("Aventuriers du Rail");
        titre.setStyle("-fx-padding: 25 0 25 0; -fx-text-alignment: center; -fx-alignment: center; -fx-font-size: 17; -fx-text-fill: white");
        VBox menu = new VBox();
        menu.getChildren().addAll(
                titre,
                vueJoueurCourant,
                instruction,
                destinationsInitiales,
                passer);
        menu.setMinWidth(200);
        menu.setStyle("-fx-border-width: 5; -fx-border-color: linear-gradient(#0014bd, #4d00bd); -fx-padding: 5; -fx-background-color: linear-gradient(#007693, #4d00bd)");
        menu.setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(plateau,menu);
    }

    final ListChangeListener<IDestination> destinationsInitialesChanges = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (IDestination destination : change.getAddedSubList()) {
                    Button buttonDestination = new Button(destination.getVilles().toString());
                    buttonDestination.setStyle("-fx-text-fill: white;-fx-background-color: #26376a; -fx-alignment: center");
                    buttonDestination.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        destinationsInitiales.getChildren().remove(buttonDestination);
                        getJeu().uneDestinationAEteChoisie(destination);
                    });
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
        plateau.creerBindings();
    }

    public IJeu getJeu() {
        return jeu;
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> getJeu().passerAEteChoisi());

}
