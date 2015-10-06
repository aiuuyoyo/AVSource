package com.example;

import java.util.Calendar;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class ExapmleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.q.s.quicksearch.models");
        addThunderAVSource(schema);
        addNovel(schema);
        addJoke(schema);
        addImage(schema);
        new DaoGenerator().generateAll(schema, "../");
    }

    public static void addImage(Schema schema) {
        Entity entity = schema.addEntity("Image");
        entity.addIdProperty();
        entity.addStringProperty("title");
        entity.addStringProperty("content");
    }

    public static void addThunderAVSource(Schema schema) {
        Entity entity = schema.addEntity("ThunderAVSource");
        entity.addIdProperty();
        entity.addStringProperty("title");
        entity.addStringProperty("content");
        entity.addBooleanProperty("hasMosaic");
        entity.addBooleanProperty("marked");
    }

    public static void addJoke(Schema schema) {
        Entity entity = schema.addEntity("Joke");
        entity.addIdProperty();
        entity.addStringProperty("title");
        entity.addStringProperty("content");
    }

    public static void addNovel(Schema schema) {
        Entity entity = schema.addEntity("Novel");
        entity.addIdProperty();
        entity.addStringProperty("title");
        entity.addStringProperty("content");
    }

}
