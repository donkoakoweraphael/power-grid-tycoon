package service.impl;

import model.GameModel;
import service.PersistenceService;

import java.io.*;

/**
 * Real implementation of persistence using Java Object Serialization.
 */
public class PersistenceServiceImpl implements PersistenceService {

    private static final String SAVE_DIR = "saves/";

    public PersistenceServiceImpl() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void save(GameModel model, String fileName) {
        String path = SAVE_DIR + fileName + ".tycoon";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(model);
            System.out.println("Game saved to: " + path);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    @Override
    public GameModel load(String fileName) {
        String path = SAVE_DIR + fileName + ".tycoon";
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            GameModel model = (GameModel) ois.readObject();
            System.out.println("Game loaded from: " + path);
            return model;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to load game: " + e.getMessage());
            return null;
        }
    }
}
