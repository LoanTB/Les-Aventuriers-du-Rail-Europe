package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.mecanique.Joueur;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    private TextField choixPionsInitiales;
    private VueJoueurCourant vueJoueurCourant;
    private VBox menuJoueur;
    private HBox menuJeu;

    private VBox boxJeu;

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
        choixPionsInitiales = new TextField();
        choixPionsInitiales.setMaxWidth(50);
        choixPionsInitiales.setBorder(Border.EMPTY);
        choixPionsInitiales.setFont(new Font(30));
        choixPionsInitiales.setStyle("-fx-spacing: 5;-fx-padding: 0 0 25 0;");
        vueJoueurCourant = new VueJoueurCourant();
        vueJoueurCourant.setStyle("-fx-padding: 0 0 15 0");
        Label titre = new Label("Aventuriers du Rail");
        titre.setStyle("-fx-padding: 25 0 25 0; -fx-text-alignment: center; -fx-alignment: center; -fx-font-size: 17; -fx-text-fill: white");
        menuJoueur = new VBox();
        //menuJoueur.setStyle("-fx-spacing: 200");
        menuJoueur.getChildren().addAll(
                titre,
                vueJoueurCourant,
                instruction,
                //choixPionsInitiales,
                destinationsInitiales,
                passer
        );
        menuJoueur.setMinWidth(200);
        menuJoueur.setStyle("-fx-border-width: 5; -fx-border-color: linear-gradient(#0014bd, #4d00bd); -fx-padding: 5; -fx-background-color: linear-gradient(#007693, #4d00bd)");
        menuJoueur.setAlignment(Pos.TOP_CENTER);
        /*VBox logs = new VBox();
        Label logTitle = new Label("Logs du jeu :");
        logTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20");
        ScrollPane logsBox = new ScrollPane();
        logs.setStyle("-fx-background-color: linear-gradient(to right, #005063, #4d00bd); -fx-spacing: 2;-fx-padding: 5");
        logs.setMinHeight(118);
        logs.setMinWidth(1320);
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
        logs.getChildren().add(logTitle);
        String[] Lorem = new String[]{"lorem","ipsum","dolor","sit","amet","consectetur","adipiscing","elit","vitae","blandit","libero","luctus","dapibus","est","nullam","vel","mattis"};
        for (int i=0;i<20;i++){
            Label logLoremIpsum = new Label("[INFO] - ");
            for (int j=0;j<20;j++){
                logLoremIpsum.setText(logLoremIpsum.getText()+" "+Lorem[(int)(Math.random()*(Lorem.length))]);
            }
            logLoremIpsum.setText(logLoremIpsum.getText()+".");
            logLoremIpsum.setStyle("-fx-text-fill: white");
            logs.getChildren().add(logLoremIpsum);
        }PlateauEtLog*/
        VBox boxJeu = new VBox();
        menuJeu = new HBox();
        boxJeu.getChildren().addAll(
                plateau,
                menuJeu
        );
        getChildren().addAll(boxJeu, menuJoueur);
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
                if (destinationsInitiales.getChildren().size() == 0){
                    menuJoueur.getChildren().set(3,choixPionsInitiales);
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

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        if (menuJoueur.getChildren().contains(choixPionsInitiales)){
            String inst = instruction.getText();
            getJeu().leNombreDePionsSouhaiteAEteRenseigne(choixPionsInitiales.getText());
            if (!inst.equals(instruction.getText())){// Si l'instruction à changer (Donc je jeu a accepté l'input)
                menuJoueur.getChildren().set(3,destinationsInitiales);
            }
        } else { // TODO Loan doit finir ça, pour choisir les pions
            getJeu().passerAEteChoisi();
        }

    });

}
