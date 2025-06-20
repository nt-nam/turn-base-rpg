package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TileMapComponent implements Component {
    public TiledMap map;
    public OrthogonalTiledMapRenderer renderer;

    public TileMapComponent(TiledMap map, OrthogonalTiledMapRenderer renderer) {
        this.map = map;
        this.renderer = renderer;
    }
}
