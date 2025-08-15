package com.arektaecomm.util;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ImageCropper {
    public static WritableImage cropInteractive(Window owner, Image source, int frameSize) {
        if (source == null)
            return null;
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Crop Image");
        StackPane frame = new StackPane();
        frame.setPrefSize(frameSize, frameSize);
        frame.setMinSize(frameSize, frameSize);
        frame.setMaxSize(frameSize, frameSize);
        frame.setStyle("-fx-background-color: #222;");
        ImageView iv = new ImageView(source);
        iv.setPreserveRatio(true);
        Group imageGroup = new Group(iv);
        final double[] last = new double[2];
        imageGroup.setOnMousePressed(e -> {
            last[0] = e.getSceneX();
            last[1] = e.getSceneY();
        });
        imageGroup.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - last[0];
            double dy = e.getSceneY() - last[1];
            imageGroup.setTranslateX(imageGroup.getTranslateX() + dx);
            imageGroup.setTranslateY(imageGroup.getTranslateY() + dy);
            last[0] = e.getSceneX();
            last[1] = e.getSceneY();
        });
        Slider zoom = new Slider(0.5, 3.0, 1.0);
        zoom.valueProperty().addListener((obs, o, n) -> {
            imageGroup.setScaleX(n.doubleValue());
            imageGroup.setScaleY(n.doubleValue());
        });
        Rectangle border = new Rectangle(frameSize - 2, frameSize - 2);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);
        StackPane cropStack = new StackPane(imageGroup, border);
        frame.getChildren().add(cropStack);
        Button cropBtn = new Button("Crop");
        Button cancelBtn = new Button("Cancel");
        HBox controls = new HBox(10, new Label("Zoom:"), zoom, cropBtn, cancelBtn);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.setStyle("-fx-padding: 8 12;");
        BorderPane root = new BorderPane();
        root.setTop(new Label("Drag image to position. Use slider to zoom. Crop area is the square."));
        BorderPane.setAlignment(root.getTop(), Pos.CENTER);
        root.setCenter(frame);
        root.setBottom(controls);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        final WritableImage[] result = new WritableImage[1];
        cropBtn.setOnAction(e -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Rectangle clip = new Rectangle(frameSize, frameSize);
            frame.setClip(clip);
            WritableImage snapshot = frame.snapshot(params, new WritableImage(frameSize, frameSize));
            frame.setClip(null);
            result[0] = snapshot;
            stage.close();
        });
        cancelBtn.setOnAction(e -> {
            result[0] = null;
            stage.close();
        });
        stage.showAndWait();
        return result[0];
    }
}
