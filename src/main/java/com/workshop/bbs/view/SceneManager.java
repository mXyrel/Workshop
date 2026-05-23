package com.workshop.bbs.view;

import com.workshop.bbs.controller.DashboardController;
import com.workshop.bbs.controller.LoginController;
import com.workshop.bbs.controller.RecordsController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages scene switching across the application.
 * Scenes: login → dashboard → records
 */
public class SceneManager {

    private static Stage primaryStage;
    private static final Map<String, Scene> scenes = new HashMap<>();

    public static void initialize(Stage stage) {
        primaryStage = stage;
        buildScenes();
    }

    private static void buildScenes() {
        scenes.put("login",     new Scene(new LoginController().getView(),     800, 600));
        scenes.put("dashboard", new Scene(new DashboardController().getView(), 900, 650));
        scenes.put("records",   new Scene(new RecordsController().getView(),   1000, 700));
    }

    /** Switch to a named scene. Rebuilds the scene to reset state. */
    public static void switchTo(String name) {
        switch (name) {
            case "login"     -> scenes.put("login",     new Scene(new LoginController().getView(),     800, 600));
            case "dashboard" -> scenes.put("dashboard", new Scene(new DashboardController().getView(), 900, 650));
            case "records"   -> scenes.put("records",   new Scene(new RecordsController().getView(),  1000, 700));
        }
        Scene scene = scenes.get(name);
        if (scene == null) throw new IllegalArgumentException("Unknown scene: " + name);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
}
