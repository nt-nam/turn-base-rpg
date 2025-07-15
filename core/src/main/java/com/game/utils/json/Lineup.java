package com.game.utils.json;

import com.badlogic.gdx.utils.JsonValue;

public class Lineup {
    public String grid;
    public Hero hero;

    public Lineup(JsonValue jsonValue) {
        grid = jsonValue.getString("grid", "empty");
        hero = new Hero(jsonValue.get("hero"));
    }

    public Lineup() {
    }
}
