package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

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
    // TODO faire la pioche destination

    private final IJeu  jeu;
    private VuePlateau plateau;
    private Button passer;
    private Label instruction;
    private VBox destinationsInitiales;
    private TextField choixPionsInitiales;
    private VueJoueurCourant vueJoueurCourant;
    private VBox menuJoueur;
    private HBox menuJeu;
    private ImageView boutonPileWagon;
    private ImageView boutonPileBateau;
    private HBox carteVisiblePioche;

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
        choixPionsInitiales.setMaxWidth(40);
        choixPionsInitiales.setFont(new Font(30));
        choixPionsInitiales.setStyle("-fx-spacing: 5;-fx-padding: 0 0 0 0;");

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
        menuJoueur.setMinWidth(267);
        menuJoueur.setStyle("-fx-padding: 5; background: transparent;");
        menuJoueur.setAlignment(Pos.TOP_CENTER);

        VBox boxJeu = new VBox();
        menuJeu = new HBox();
        boxJeu.getChildren().addAll(
                plateau,
                menuJeu
        );
        getChildren().addAll(boxJeu, menuJoueur);

        boutonPileWagon = new ImageView("images/cartesWagons/dos-WAGON.png");
        boutonPileWagon.setFitHeight(145);
        boutonPileWagon.setFitWidth(90.625);

        boutonPileBateau = new ImageView("images/cartesWagons/dos-BATEAU.png");
        boutonPileBateau.setFitHeight(145);
        boutonPileBateau.setFitWidth(90.625);

        HBox pioches = new HBox();
        pioches.getChildren().addAll(boutonPileWagon, boutonPileBateau);
        pioches.setSpacing(10.0);
        pioches.setPadding(new Insets(5));

        carteVisiblePioche = new HBox();
        carteVisiblePioche.setSpacing(5.0);
        carteVisiblePioche.setAlignment(Pos.CENTER);

        menuJeu.getChildren().addAll(pioches, carteVisiblePioche);
        menuJeu.setSpacing(50.0);
        menuJoueur.setStyle("background: transparent;");
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

    private Animation animationCouleur(String couleur){
        final Animation animation = new Transition() {
            {setCycleDuration(Duration.millis(1000));}
            @Override
            protected void interpolate(double progress) {
                setStyle("-fx-background-color: linear-gradient(to right top,#590097 "+(int)(50+(1-progress)*50)+"%, "+couleur+");");
            }
        };
        return animation;
    }

    final ChangeListener<IJoueur> joueurCourantChange = (observable, oldValue, newValue) -> {

        switch (newValue.getCouleur()){
            case BLEU -> {
                animationCouleur("#00036b").playFromStart();
                //setStyle("-fx-background-color: linear-gradient(to right top,#590097, #00036b);");
            }
            case ROSE -> {
                animationCouleur("#8e0078").playFromStart();
                //setStyle("-fx-background-color: linear-gradient(to right top,#590097, #8e0078);");
            }
            case VERT -> {
                animationCouleur("#008e06").playFromStart();
                //setStyle("-fx-background-color: linear-gradient(to right top,#590097, #008e06);");
            }
            case ROUGE -> {
                animationCouleur("#8e0000").playFromStart();
                //setStyle("-fx-background-color: linear-gradient(to right top,#590097, #8e0000);");
            }
            case JAUNE -> {
                animationCouleur("#8c8e00").playFromStart();
                //setStyle("-fx-background-color: linear-gradient(to right top,#590097, #8c8e00);");
            }
        }
    };

    public void creerBindings() {
        plateau.prefWidthProperty().bind(getScene().widthProperty());
        plateau.prefHeightProperty().bind(getScene().heightProperty());
        jeu.destinationsInitialesProperty().addListener(destinationsInitialesChanges);
        passer.addEventHandler(MouseEvent.MOUSE_CLICKED, actionPasserParDefaut);
        choixPionsInitiales.setOnKeyPressed(key -> {if (key.getCode() == KeyCode.ENTER) {confirmChoixPionsInitiales();}});
        instruction.textProperty().bind(jeu.instructionProperty());
        vueJoueurCourant.creerBindings();
        plateau.creerBindings();
        jeu.joueurCourantProperty().addListener(joueurCourantChange);
        jeu.cartesTransportVisiblesProperty().addListener(CartesPiles);
        boutonPileWagon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {jeu.uneCarteWagonAEtePiochee();});
        boutonPileBateau.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {jeu.uneCarteBateauAEtePiochee();} );
    }

    public IJeu getJeu() {
        return jeu;
    }

    private boolean confirmChoixPionsInitiales(){
        if (choixPionsInitiales.getText().length() > 0){
            if (choixPionsInitiales.getText().length() < 2){
                return true;
            }
            String inst = instruction.getText();
            choixPionsInitiales.setText(choixPionsInitiales.getText().substring(choixPionsInitiales.getText().length()-2));
            getJeu().leNombreDePionsSouhaiteAEteRenseigne(choixPionsInitiales.getText());
            if (!inst.equals(instruction.getText())){// Si l'instruction à changer (Donc je jeu a accepté l'input)
                menuJoueur.getChildren().set(3,destinationsInitiales);
            }
            return true;
        } else {
            return false;
        }
    }

    EventHandler<? super MouseEvent> actionPasserParDefaut = (mouseEvent -> {
        if (menuJoueur.getChildren().contains(choixPionsInitiales)){
            if (!confirmChoixPionsInitiales()){
                menuJoueur.getChildren().set(3,destinationsInitiales);
                getJeu().passerAEteChoisi();
            }
        } else {
            getJeu().passerAEteChoisi();
        }
    });

    ListChangeListener<ICarteTransport> CartesPiles = new ListChangeListener<ICarteTransport>() {
        @Override
        public void onChanged(Change<? extends ICarteTransport> change) {
            carteVisiblePioche.getChildren().clear();
            for (ICarteTransport cartes : jeu.cartesTransportVisiblesProperty()) {
                ImageView carte = Utils.loadCarte(cartes, new double[]{78.125, 125});
                carte.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    jeu.uneCarteTransportAEteChoisie(cartes);
                });
                carteVisiblePioche.getChildren().add(carte);
            }
        }
    };

}
