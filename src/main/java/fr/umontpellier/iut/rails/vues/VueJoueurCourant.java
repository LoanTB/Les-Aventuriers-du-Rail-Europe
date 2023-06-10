package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.IDestination;
import fr.umontpellier.iut.rails.IJoueur;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
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
    // TODO mettre des symboles de port et mettre un nombre à côté indiquant le nombre d'éléments possédés (ports posés pour les ports)
    private Label nomJoueur;
    private VBox carteTransport;
    private ScrollPane spCarteDestination;
    private VBox carteDestination;
    private ImageView avatar;
    private ScrollPane spCarteTransport;
    private HBox infos;
    private Button nbPionsWagons;
    private Button nbPionsBateau;
    private Label nbPionsW;
    private Label nbPionsB;
    private HBox hbPionWagon;
    private HBox hbPionBateau;

    public VueJoueurCourant() {
        nomJoueur = new Label();

        carteTransport = new VBox();
        carteTransport.setStyle("-fx-alignment: center");

        carteDestination = new VBox();
        spCarteDestination = new ScrollPane();
        spCarteDestination.setContent(carteDestination);
        spCarteDestination.setPrefViewportHeight(50.0);
        spCarteDestination.setManaged(false);
        spCarteDestination.setVisible(false);

        avatar = new ImageView();
        avatar.setFitHeight(120.0);
        avatar.setFitWidth(94.86);

        spCarteTransport = new ScrollPane();
        spCarteTransport.setContent(carteTransport);
        spCarteTransport.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        spCarteTransport.setPrefViewportHeight(150.0);
        spCarteTransport.setManaged(false);
        spCarteTransport.setVisible(false);

        ImageView pionWagon = new ImageView("images/bouton-pions-wagon.png");
        pionWagon.setFitHeight(25);
        pionWagon.setFitWidth(25);
        nbPionsW = new Label("");
        hbPionWagon = new HBox();
        hbPionWagon.getChildren().addAll(pionWagon,nbPionsW);
        hbPionWagon.setSpacing(2.5);
        hbPionWagon.setAlignment(Pos.CENTER);
        nbPionsWagons = new Button();
        nbPionsWagons.setGraphic(hbPionWagon);
        nbPionsWagons.setManaged(false);
        nbPionsWagons.setVisible(false);

        ImageView pionBateau = new ImageView("images/bouton-pions-bateau.png");
        pionBateau.setFitHeight(25);
        pionBateau.setFitWidth(25);
        nbPionsB = new Label("");
        hbPionBateau = new HBox();
        hbPionBateau.getChildren().addAll(pionBateau,nbPionsB);
        hbPionBateau.setSpacing(2.5);
        hbPionBateau.setAlignment(Pos.CENTER);
        nbPionsBateau = new Button();
        nbPionsBateau.setGraphic(hbPionBateau);
        nbPionsBateau.setManaged(false);
        nbPionsBateau.setVisible(false);

        infos = new HBox();
        infos.getChildren().addAll(nbPionsWagons, nbPionsBateau);
        infos.setAlignment(Pos.CENTER);

        getChildren().addAll(avatar,nomJoueur, infos, spCarteTransport, spCarteDestination);
        this.setSpacing(10.0);
        this.setAlignment(Pos.CENTER);
    }

    ChangeListener<IJoueur> JoueurCourantChange = (observableValue, ancien, courant) -> {
        IntegerProperty nbPW = courant.nbPionsWagonsProperty();
        IntegerProperty nbPB = courant.nbPionsBateauxProperty();
        nbPionsW.setText("" + nbPW.getValue());
        nbPionsB.setText("" + nbPB.getValue());

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
            spCarteTransport.setManaged(true);
            spCarteTransport.setVisible(true);
        }

        carteDestination.getChildren().clear();
        for (IDestination cd : courant.getDestinations()){
            Label labelCarteDestination = new Label(cd.getVilles().toString());
            labelCarteDestination.setStyle("-fx-text-fill: black");
            carteDestination.getChildren().add(labelCarteDestination);
            spCarteDestination.setManaged(true);
            spCarteDestination.setVisible(true);
        }

        if (courant.getNbPionsWagon() > 0){
            nbPionsWagons.setManaged(true);
            nbPionsWagons.setVisible(true);
            nbPionsBateau.setManaged(true);
            nbPionsBateau.setVisible(true);
        }

    };

    public void creerBindings() {
        ((VueDuJeu) getScene().getRoot()).getJeu().joueurCourantProperty().addListener(JoueurCourantChange);
    }

}