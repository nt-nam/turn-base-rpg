package com.game.utils.data;

import com.badlogic.gdx.utils.Array;

public class SaveGameECS {
    public Array<EntitySaveData> entities; // mỗi entity là 1 nhân vật, quái, v.v.
    public String currentMap;
    public long saveTime;
}
