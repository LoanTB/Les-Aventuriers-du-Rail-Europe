package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.mecanique.data.Destination;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private VBox choixPionsInitiales;
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
        destinationsInitiales.setStyle("-fx-alignment: center;-fx-padding: 0 0 25 0;-fx-spacing: 5");
        choixPionsInitiales = new VBox();
        Label choixPionsInitialesTitre = new Label("Entrez le nombre de wagon que vous voulez prendre (Minimum X, maximum Y) :");
        choixPionsInitialesTitre.setStyle("-fx-text-fill: white");
        TextField choixPionsInitialesField = new TextField();
        choixPionsInitiales.getChildren().addAll(
                choixPionsInitialesTitre,
                choixPionsInitialesField
        );
        choixPionsInitiales.setStyle("-fx-spacing: 5;-fx-padding: 0 0 25 0;");
        vueJoueurCourant = new VueJoueurCourant();
        vueJoueurCourant.setStyle("-fx-padding: 0 0 15 0");
        Label titre = new Label("Aventuriers du Rail");
        titre.setStyle("-fx-padding: 25 0 25 0; -fx-text-alignment: center; -fx-alignment: center; -fx-font-size: 17; -fx-text-fill: white");
        VBox menu = new VBox();
        menu.getChildren().addAll(
                titre,
                vueJoueurCourant,
                instruction,
                choixPionsInitiales,
                destinationsInitiales,
                passer);
        menu.setMinWidth(200);
        menu.setStyle("-fx-border-width: 5; -fx-border-color: linear-gradient(#0014bd, #4d00bd); -fx-padding: 5; -fx-background-color: linear-gradient(#007693, #4d00bd)");
        menu.setAlignment(Pos.TOP_CENTER);
        VBox logs = new VBox();
        Label logStartDisplay = new Label("Logs de jeu :");
        logStartDisplay.setStyle("-fx-text-fill: white; -fx-font-size: 20");
        ScrollPane logsBox = new ScrollPane();
        logs.setStyle("-fx-background-color: linear-gradient(to right, #005063, #4d00bd); -fx-spacing: 2;-fx-padding: 5");
        logs.setMinHeight(224);
        logs.setMinWidth(1336);
        logsBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logsBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logsBox.setContent(logs);
        logsBox.setPrefViewportHeight(224);
        logsBox.setPrefViewportWidth(1720);
        VBox PlateauEtLog = new VBox();
        PlateauEtLog.getChildren().addAll(
                plateau,
                logsBox
        );
        logs.getChildren().add(logStartDisplay);
        getChildren().addAll(PlateauEtLog,menu);
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
