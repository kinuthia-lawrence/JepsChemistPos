package com.larrykin.jepschemistpos.UTILITIES;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static final List<Scene> scenes = new ArrayList<>();

    //? Adding the scene to the list
    public static void addScene(Scene scene) {
        scenes.add(scene);
    }

    //? Getting all the scenes
    public static List<Scene> getAllScenes() {
        return new ArrayList<>(scenes);
    }
}
