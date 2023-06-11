package fr.umontpellier.iut.rails.vues;

import fr.umontpellier.iut.rails.ICarteTransport;
import fr.umontpellier.iut.rails.IJeu;
import fr.umontpellier.iut.rails.IJoueur;
import fr.umontpellier.iut.rails.IRoute;
import fr.umontpellier.iut.rails.mecanique.Route;
import fr.umontpellier.iut.rails.mecanique.data.CarteTransport;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cette classe présente les routes et les villes sur le plateau.
 *
 * On y définit les handlers à exécuter lorsque qu'un élément du plateau a été choisi par l'utilisateur
 * ainsi que les bindings qui mettront à jour le plateau après la prise d'une route ou d'un port par un joueur
 */
public class VuePlateau extends Pane {

    @FXML
    private ImageView mapMonde;
    private ImageView pionWagon;
    private ImageView pionGare;
    private Rectangle rectangleSegment;
    private Circle cerclePort;
    private Set<Rectangle> routesRECT;

    public VuePlateau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/plateau.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setMinSize(Screen.getPrimary().getBounds().getWidth()/3, Screen.getPrimary().getBounds().getHeight()/3);

        pionWagon = new ImageView();
        pionWagon.setFitHeight(160);
        pionWagon.setFitWidth(160);

        pionGare = new ImageView();
        pionGare.setFitHeight(206);
        pionGare.setFitWidth(268);

        routesRECT = new HashSet<>();
    }

    EventHandler<MouseEvent> choixRoute = event -> {
        IJeu jeu = ((VueDuJeu) getScene().getRoot()).getJeu();
        Rectangle route = (Rectangle) event.getSource();
        jeu.uneRouteAEteChoisie(route.getId());
//        for (IRoute r : routes){
//            if (r.getNom().equals(route.getId())){
//                System.out.println(r.proprietaireProperty().get());
//                if (r.proprietaireProperty().get() != null){
//                    route.setVisible(true);
//                    switch (r.proprietaireProperty().get().getCouleur()){
//                        case BLEU -> route.setStroke(Paint.valueOf("#00036b"));
//                        case ROSE -> route.setStroke(Paint.valueOf("#8e0078"));
//                        case VERT -> route.setStroke(Paint.valueOf("#008e06"));
//                        case ROUGE -> route.setStroke(Paint.valueOf("#8e0000"));
//                        case JAUNE -> route.setStroke(Paint.valueOf("#8c8e00"));
//                    }
//
//                }
//            }
//        }
//
//        System.out.println("DEBUG : Route cliqué ("+route.toString()+")");
    };

    EventHandler<MouseEvent> choixPort = event -> {
        IJeu jeu = ((VueDuJeu) getScene().getRoot()).getJeu();

        Circle port = (Circle) event.getSource();
        jeu.unPortAEteChoisi(port.getId());

        System.out.println("DEBUG : Port cliqué ("+port.toString()+")");
    };

    public void creerBindings() {
        ajouterVilles();
        ajouterPorts();
        ajouterRoutes();
        bindRedimensionEtCentragePlateau();
    }

    private void ajouterPorts() {
        for (String nomPort : DonneesGraphiques.ports.keySet()) {
            DonneesGraphiques.DonneesCerclesPorts positionPortSurPlateau = DonneesGraphiques.ports.get(nomPort);
            cerclePort = new Circle(positionPortSurPlateau.centreX(), positionPortSurPlateau.centreY(), DonneesGraphiques.rayonInitial);
            cerclePort.setId(nomPort);
            getChildren().add(cerclePort);
            bindCerclePortAuPlateau(positionPortSurPlateau, cerclePort);
            cerclePort.setOnMouseClicked(choixPort);
        }
    }

    private void ajouterRoutes() {
        List<? extends IRoute> listeRoutes = ((VueDuJeu) getScene().getRoot()).getJeu().getRoutes();
        for (String nomRoute : DonneesGraphiques.routes.keySet()) {
            ArrayList<DonneesGraphiques.DonneesSegments> segmentsRoute = DonneesGraphiques.routes.get(nomRoute);
            IRoute route = listeRoutes.stream().filter(r -> r.getNom().equals(nomRoute)).findAny().orElse(null);

            for (DonneesGraphiques.DonneesSegments unSegment : segmentsRoute) {
                rectangleSegment = new Rectangle(unSegment.getXHautGauche(), unSegment.getYHautGauche(), DonneesGraphiques.largeurRectangle, DonneesGraphiques.hauteurRectangle);
                rectangleSegment.setFill(new ImagePattern(new Image("images/wagons/image-wagon-BLEU.png")));
                rectangleSegment.setId(nomRoute);
                rectangleSegment.setRotate(unSegment.getAngle());
                getChildren().add(rectangleSegment);
                rectangleSegment.setOnMouseClicked(choixRoute);
                routesRECT.add(rectangleSegment);
                bindRectangle(rectangleSegment, unSegment.getXHautGauche(), unSegment.getYHautGauche());
            }
            if (route != null){
                final ChangeListener<IJoueur> proprietaireChange = (observable, oldValue, newValue) -> {
                    for (Rectangle routeRECT : routesRECT){
                        if (route.getNom().equals(routeRECT.getId())){
                            if (newValue != null){
                                routeRECT.setVisible(true);
                                routeRECT.setFill(new ImagePattern(new Image("images/wagons/image-wagon-BLEU.png")));//"images/wagons/image-wagon-" + newValue.getCouleur() + ".png"
                                System.out.println("Colorie le rectangle "+routeRECT+" de route "+route+" en la couleur "+newValue.getCouleur());
                            }
                        }
                    }
                };
                route.proprietaireProperty().addListener(proprietaireChange);
            }
        }
    }


    private void bindRedimensionEtCentragePlateau() {
        mapMonde.fitWidthProperty().bind(widthProperty());
        mapMonde.fitHeightProperty().bind(heightProperty());
        mapMonde.layoutXProperty().bind(new DoubleBinding() { // Pour maintenir le plateau au centre
            {
                super.bind(widthProperty(),heightProperty());
            }
            @Override
            protected double computeValue() {
                double imageViewWidth = mapMonde.getLayoutBounds().getWidth();
                return (getWidth() - imageViewWidth) / 2;
            }
        });
    }

    private void bindCerclePortAuPlateau(DonneesGraphiques.DonneesCerclesPorts port, Circle cerclePort) {
        cerclePort.centerXProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }
            @Override
            protected double computeValue() {
                return mapMonde.getLayoutX() + port.centreX() * mapMonde.getLayoutBounds().getWidth()/ DonneesGraphiques.largeurInitialePlateau;
            }
        });
        cerclePort.centerYProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }
            @Override
            protected double computeValue() {
                return mapMonde.getLayoutY() + port.centreY() * mapMonde.getLayoutBounds().getHeight()/ DonneesGraphiques.hauteurInitialePlateau;
            }
        });
        cerclePort.radiusProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }
            @Override
            protected double computeValue() {
                return DonneesGraphiques.rayonInitial * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
    }

    private void bindRectangle(Rectangle rect, double layoutX, double layoutY) {
        rect.widthProperty().bind(new DoubleBinding() {
            { super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());}
            @Override
            protected double computeValue() {
                return DonneesGraphiques.largeurRectangle * mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.heightProperty().bind(new DoubleBinding() {
            { super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());}
            @Override
            protected double computeValue() {
                return DonneesGraphiques.hauteurRectangle * mapMonde.getLayoutBounds().getWidth()/ DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.layoutXProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty(), mapMonde.xProperty());
            }
            @Override
            protected double computeValue() {
                return mapMonde.getLayoutX() + layoutX * mapMonde.getLayoutBounds().getWidth()/ DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.xProperty().bind(new DoubleBinding() {
            { super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty(), mapMonde.xProperty());}
            @Override
            protected double computeValue() {
                return mapMonde.getLayoutBounds().getWidth() / DonneesGraphiques.largeurInitialePlateau;
            }
        });
        rect.layoutYProperty().bind(new DoubleBinding() {
            {
                super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());
            }
            @Override
            protected double computeValue() {
                return layoutY * mapMonde.getLayoutBounds().getHeight()/ DonneesGraphiques.hauteurInitialePlateau;
            }
        });
        rect.yProperty().bind(new DoubleBinding() {
            { super.bind(mapMonde.fitWidthProperty(), mapMonde.fitHeightProperty());}
            @Override
            protected double computeValue() {
                return mapMonde.getLayoutBounds().getHeight()/ DonneesGraphiques.hauteurInitialePlateau;
            }
        });
    }



    private void ajouterVilles() {
        for (String nomVille : DonneesGraphiques.villes.keySet()) {
            DonneesGraphiques.DonneesCerclesPorts positionVilleSurPlateau = DonneesGraphiques.villes.get(nomVille);
            Circle cercleVille = new Circle(positionVilleSurPlateau.centreX(), positionVilleSurPlateau.centreY(), DonneesGraphiques.rayonInitial);
            cercleVille.setId(nomVille);
            getChildren().add(cercleVille);
            bindCerclePortAuPlateau(positionVilleSurPlateau, cercleVille);
            cercleVille.setOnMouseClicked(choixPort);
        }
    }
}
