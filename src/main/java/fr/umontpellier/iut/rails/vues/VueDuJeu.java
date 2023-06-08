package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.animation.*;
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
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private VueAutresJoueurs vueAutresJoueurs;
    private VBox menuJoueur;
    private VBox infosAutresJoueurs;
    private HBox menuJeu;
    private StackPane piocheWagon;
    private StackPane piocheBateau;
    private HBox cartesVisiblePioche;
    private VBox menuDesJoueurs;

    public VueDuJeu(IJeu jeu) {
        this.jeu = jeu;
        setStyle("-fx-background-color: linear-gradient(to right top, #590097 100%, #590097);");

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

        vueAutresJoueurs = new VueAutresJoueurs();

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

        infosAutresJoueurs = new VBox();
        infosAutresJoueurs.getChildren().add(vueAutresJoueurs);

        menuDesJoueurs = new VBox();
        menuDesJoueurs.getChildren().addAll(menuJoueur, infosAutresJoueurs);
        menuDesJoueurs.setSpacing(30);

        VBox boxJeu = new VBox();
        menuJeu = new HBox();
        boxJeu.getChildren().addAll(
                plateau,
                menuJeu
        );
        getChildren().addAll(boxJeu,menuDesJoueurs);

        int nbCartesDansPioche = 3;
        piocheWagon = new StackPane();
        piocheBateau = new StackPane();
        for (int i=0;i<nbCartesDansPioche;i++){
            piocheWagon.getChildren().add(Utils.loadImage("images/cartesWagons/dos-WAGON.png", new double[]{145,90.625}));
            StackPane.setMargin(piocheWagon.getChildren().get(piocheWagon.getChildren().size()-1),new Insets(0, 0, 0, -(double)i/nbCartesDansPioche*15));

            piocheBateau.getChildren().add(Utils.loadImage("images/cartesWagons/dos-BATEAU.png", new double[]{145,90.625}));
            StackPane.setMargin(piocheBateau.getChildren().get(piocheBateau.getChildren().size()-1),new Insets(0, 0, 0, -(double)i/nbCartesDansPioche*15));
        }

        HBox pioches = new HBox();
        pioches.getChildren().addAll(piocheWagon, piocheBateau);
        pioches.setSpacing(10);
        pioches.setPadding(new Insets(5,10,5,10));

        cartesVisiblePioche = new HBox();
        cartesVisiblePioche.setSpacing(5.0);
        cartesVisiblePioche.setAlignment(Pos.CENTER);

        menuJeu.getChildren().addAll(pioches, cartesVisiblePioche);
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
        String ancienne = Utils.reverseString(Utils.reverseString(getStyle()).substring(2,9));

        final Animation animation = new Transition() {
            {setCycleDuration(Duration.millis(1000));}
            @Override
            protected void interpolate(double progress) {
                String r = Integer.toHexString((int) (Integer.parseInt(ancienne.substring(1, 3), 16) + (Integer.parseInt("59", 16) - Integer.parseInt(ancienne.substring(1,3), 16)) * progress));
                while (r.length() < 2){
                    r = "0"+r;
                }
                String g = Integer.toHexString((int)(Integer.parseInt(ancienne.substring(3,5),16)+(Integer.parseInt("00",16)-Integer.parseInt(ancienne.substring(3,5),16))*progress));
                while (g.length() < 2){
                    g = "0"+g;
                }
                String b = Integer.toHexString((int)(Integer.parseInt(ancienne.substring(5,7),16)+(Integer.parseInt("97",16)-Integer.parseInt(ancienne.substring(5,7),16))*progress));
                while (b.length() < 2){
                    b = "0"+b;
                }
                String transitionColor = "#"+r+g+b;
                if (progress == 1){
                    setStyle("-fx-background-color: linear-gradient(to right top, #590097 "+(int)(50+(1-progress)*50)+"%, "+couleur+");");
                } else {
                    setStyle("-fx-background-color: linear-gradient(to right top, #590097 "+(int)(50-(progress)*50)+"%, "+transitionColor+" "+(int)(50+(1-progress)*50)+"%, "+couleur+");");
                }
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
        piocheWagon.addEventHandler(MouseEvent.MOUSE_CLICKED,piocherWagon);
        piocheBateau.addEventHandler(MouseEvent.MOUSE_CLICKED,piocherBateau);
        vueAutresJoueurs.creerBindings();
    }

    EventHandler<? super MouseEvent> piocherBateau = (mouseEvent -> {
        IJoueur ancienJ = getJeu().joueurCourantProperty().get();
        ArrayList<ICarteTransport> ancienC = new ArrayList<>();
        ancienC.addAll(getJeu().cartesTransportVisiblesProperty());
        ancienC.addAll(getJeu().joueurCourantProperty().get().getCartesTransport());
        getJeu().uneCarteBateauAEtePiochee();
        ArrayList<ICarteTransport> nouveauC = new ArrayList<>();
        nouveauC.addAll(getJeu().cartesTransportVisiblesProperty());
        nouveauC.addAll(ancienJ.getCartesTransport());
        nouveauC.removeAll(ancienC);
        ParallelTransition parallelTransition = new ParallelTransition();
        for (ICarteTransport carte : nouveauC) {
            ImageView img = Utils.loadCarte(carte, new double[]{78.125, 125});
            img.setRotate(-90);
            ImageView pio = Utils.loadImage("images/cartesWagons/dos-BATEAU.png", new double[]{145,90.625});
            piocheBateau.getChildren().add(img);
            piocheBateau.getChildren().add(pio);

            TranslateTransition translateTransitionP = new TranslateTransition(Duration.millis(1000),pio);
            translateTransitionP.setFromY(0);
            translateTransitionP.setToY(-300);
            TranslateTransition translateTransitionI = new TranslateTransition(Duration.millis(1000),img);
            translateTransitionI.setFromY(0);
            translateTransitionI.setToY(-300);
            ParallelTransition parallelTransitionT = new ParallelTransition();
            parallelTransitionT.getChildren().addAll(/*fadeTransitionP,fadeTransitionI,*/translateTransitionP,translateTransitionI);

            ScaleTransition scaleTransition0P = new ScaleTransition(Duration.millis(250), pio);
            scaleTransition0P.setFromX(1);
            scaleTransition0P.setToX(0);
            ScaleTransition scaleTransition0I = new ScaleTransition(Duration.millis(250), img);
            scaleTransition0I.setFromY(1);
            scaleTransition0I.setToY(0);
            ParallelTransition parallelTransition0 = new ParallelTransition();
            parallelTransition0.getChildren().addAll(scaleTransition0P,scaleTransition0I);

            ScaleTransition scaleTransition1I = new ScaleTransition(Duration.millis(500), img);
            scaleTransition1I.setFromY(0);
            scaleTransition1I.setToY(1);
            ParallelTransition parallelTransition1 = new ParallelTransition();
            parallelTransition1.getChildren().addAll(scaleTransition1I);

            FadeTransition fadeTransitionI = new FadeTransition(Duration.millis(250),img);
            fadeTransitionI.setFromValue(1);
            fadeTransitionI.setToValue(0);

            SequentialTransition sequentialTransition = new SequentialTransition (parallelTransition0,parallelTransition1,fadeTransitionI);
            sequentialTransition.play();

            parallelTransition.getChildren().addAll(sequentialTransition,parallelTransitionT);
        }
        parallelTransition.play();
    });

    EventHandler<? super MouseEvent> piocherWagon = (mouseEvent -> {
        IJoueur ancienJ = getJeu().joueurCourantProperty().get();
        ArrayList<ICarteTransport> ancienC = new ArrayList<>();
        ancienC.addAll(getJeu().cartesTransportVisiblesProperty());
        ancienC.addAll(getJeu().joueurCourantProperty().get().getCartesTransport());
        getJeu().uneCarteWagonAEtePiochee();
        ArrayList<ICarteTransport> nouveauC = new ArrayList<>();
        nouveauC.addAll(getJeu().cartesTransportVisiblesProperty());
        nouveauC.addAll(ancienJ.getCartesTransport());
        nouveauC.removeAll(ancienC);
        ParallelTransition parallelTransition = new ParallelTransition();
        for (ICarteTransport carte : nouveauC) {
            ImageView img = Utils.loadCarte(carte, new double[]{78.125, 125});
            img.setRotate(-90);
            ImageView pio = Utils.loadImage("images/cartesWagons/dos-WAGON.png", new double[]{145,90.625});
            piocheWagon.getChildren().add(img);
            piocheWagon.getChildren().add(pio);

            TranslateTransition translateTransitionP = new TranslateTransition(Duration.millis(1000),pio);
            translateTransitionP.setFromY(0);
            translateTransitionP.setToY(-300);
            TranslateTransition translateTransitionI = new TranslateTransition(Duration.millis(1000),img);
            translateTransitionI.setFromY(0);
            translateTransitionI.setToY(-300);
            ParallelTransition parallelTransitionT = new ParallelTransition();
            parallelTransitionT.getChildren().addAll(/*fadeTransitionP,fadeTransitionI,*/translateTransitionP,translateTransitionI);

            ScaleTransition scaleTransition0P = new ScaleTransition(Duration.millis(250), pio);
            scaleTransition0P.setFromX(1);
            scaleTransition0P.setToX(0);
            ScaleTransition scaleTransition0I = new ScaleTransition(Duration.millis(250), img);
            scaleTransition0I.setFromY(1);
            scaleTransition0I.setToY(0);
            ParallelTransition parallelTransition0 = new ParallelTransition();
            parallelTransition0.getChildren().addAll(scaleTransition0P,scaleTransition0I);

            ScaleTransition scaleTransition1I = new ScaleTransition(Duration.millis(500), img);
            scaleTransition1I.setFromY(0);
            scaleTransition1I.setToY(1);
            ParallelTransition parallelTransition1 = new ParallelTransition();
            parallelTransition1.getChildren().addAll(scaleTransition1I);

            FadeTransition fadeTransitionI = new FadeTransition(Duration.millis(250),img);
            fadeTransitionI.setFromValue(1);
            fadeTransitionI.setToValue(0);

            SequentialTransition sequentialTransition = new SequentialTransition (parallelTransition0,parallelTransition1,fadeTransitionI);
            sequentialTransition.play();

            parallelTransition.getChildren().addAll(sequentialTransition,parallelTransitionT);
        }
        parallelTransition.play();
    });

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

//    private void resetCartesVisibles(){
//        cartesVisiblePioche.getChildren().clear();
//        cartesVisibleImages.clear();
//        for (ICarteTransport carte : jeu.cartesTransportVisiblesProperty()) {
//            ImageView img = Utils.loadCarte(carte, new double[]{78.125, 125});
//            cartesVisibleImages.put(carte,img);
//            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                jeu.uneCarteTransportAEteChoisie(carte);
//            });
//            cartesVisiblePioche.getChildren().add(img);
//        }
//    }

    private final Map<ICarteTransport,ImageView> cartesVisibleImages = new HashMap<>();

    ListChangeListener<ICarteTransport> CartesPiles = new ListChangeListener<ICarteTransport>() {
        @Override
        public void onChanged(Change<? extends ICarteTransport> change) {
            while (change.next()) {
                if (change.wasAdded()) {
                    cartesVisiblePioche.getChildren().clear();
                    cartesVisibleImages.clear();
                    for (ICarteTransport carte : jeu.cartesTransportVisiblesProperty()) {
                        if (!change.getAddedSubList().contains(carte)){
                            ImageView img = Utils.loadCarte(carte, new double[]{78.125, 125});
                            cartesVisibleImages.put(carte,img);
                            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                jeu.uneCarteTransportAEteChoisie(carte);
                            });
                            cartesVisiblePioche.getChildren().add(img);
                        }
                    }
                    ParallelTransition parallelTransition = new ParallelTransition();
                    for (ICarteTransport carte : change.getAddedSubList()) {
                        ImageView img = Utils.loadCarte(carte, new double[]{78.125, 125});
                        cartesVisibleImages.put(carte,img);
                        img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            jeu.uneCarteTransportAEteChoisie(carte);
                        });
                        cartesVisiblePioche.getChildren().add(img);
                        FadeTransition ft = new FadeTransition(Duration.millis(500),img);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        TranslateTransition tt = new TranslateTransition(Duration.millis(500),img);
                        tt.setFromY(-300);
                        tt.setToY(0);
                        parallelTransition.getChildren().addAll(ft,tt);
                    }
                    parallelTransition.play();
                } else if (change.wasRemoved()) {
                    for (ICarteTransport carte : change.getRemoved()){
                        RotateTransition rotateTransition =
                                new RotateTransition(Duration.millis(200), cartesVisibleImages.get(carte));
                        rotateTransition.setByAngle(-90);
                        FadeTransition fadeTransition =
                                new FadeTransition(Duration.millis(1000), cartesVisibleImages.get(carte));
                        fadeTransition.setFromValue(1.0f);
                        fadeTransition.setToValue(0.0f);
                        Path path = new Path();
                        path.getElements().add(new MoveTo(cartesVisibleImages.get(carte).getFitHeight()*.75,cartesVisibleImages.get(carte).getFitWidth()/2*0.65));
                        path.getElements().add(new CubicCurveTo(100, -300, 200, -200, 400, -300));
                        PathTransition pathTransition = new PathTransition();
                        pathTransition.setDuration(Duration.millis(1000));
                        pathTransition.setPath(path);
                        pathTransition.setNode(cartesVisibleImages.get(carte));
                        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                        ParallelTransition parallelTransition = new ParallelTransition();
                        parallelTransition.getChildren().addAll(
                                fadeTransition,
                                pathTransition
                        );
                        SequentialTransition sequentialTransition = new SequentialTransition (rotateTransition,parallelTransition);
                        sequentialTransition.play();
                    }

                }
            }
        }
    };

}
