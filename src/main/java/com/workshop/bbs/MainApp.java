package com.workshop.bbs;

import com.workshop.bbs.util.DatabaseConnection;
import com.workshop.bbs.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * PUP Book Borrowing System
 * Workshop Project — Might Malinay, BSIT 2-2
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PUP Book Borrowing System — Workshop");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);

        SceneManager.initialize(primaryStage);
        SceneManager.switchTo("login");

        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseConnection.closePool();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
