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
            System.out.println("Partie sauvegardee: " + path);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Echec de la sauvegarde: " + e.getMessage());
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
            System.out.println("Partie chargee depuis: " + path);
            return model;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Echec du chargement: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean exists(String fileName) {
        String path = SAVE_DIR + fileName + ".tycoon";
        return new File(path).exists();
    }
}
