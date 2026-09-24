package com.pxworld.client.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.utils.Disposable

class SpriteSet(private val animations: Map<String, Animation<TextureRegion>>) {

    val names: Set<String> get() = animations.keys

    fun animation(name: String): Animation<TextureRegion> =
        animations[name] ?: animations[IDLE] ?: animations.values.first()

    fun frame(name: String, time: Float): TextureRegion = animation(name).getKeyFrame(time)

    companion object {
        const val IDLE = "idle"
        const val RUN = "run"
        const val ATTACK = "attack"
        const val HURT = "hurt"
        const val DIE = "die"
    }
}

class AssetService(private val assetMap: Map<String, String>) : Disposable {

    private val manager = AssetManager()
    private val spriteSets = mutableMapOf<String, SpriteSet>()
    private val animations = mutableMapOf<String, Animation<TextureRegion>>()

    init {
        manager.setLoader(TiledMap::class.java, TmxMapLoader())
    }

    fun resolve(key: String): String = assetMap[key] ?: throw IllegalArgumentException("unmapped asset key $key")

    fun has(key: String): Boolean = key in assetMap

    fun atlas(path: String): TextureAtlas {
        if (!manager.isLoaded(path, TextureAtlas::class.java)) {
            manager.load(path, TextureAtlas::class.java)
            manager.finishLoadingAsset<TextureAtlas>(path)
        }
        return manager.get(path, TextureAtlas::class.java)
    }

    fun sprite(key: String): SpriteSet = spriteSets.getOrPut(key) {
        val reference = resolve(key)
        if (reference.endsWith(".png")) {
            if (!manager.isLoaded(reference, Texture::class.java)) {
                manager.load(reference, Texture::class.java)
                manager.finishLoadingAsset<Texture>(reference)
            }
            val still = TextureRegion(manager.get(reference, Texture::class.java))
            return@getOrPut SpriteSet(mapOf(SpriteSet.IDLE to Animation<TextureRegion>(FRAME_SECONDS, still)))
        }
        val atlas = atlas(reference.substringBefore("#"))
        val grouped = atlas.regions.groupBy { it.name }
        SpriteSet(grouped.mapValues { (_, regions) ->
            Animation<TextureRegion>(FRAME_SECONDS, GdxArray(regions.sortedBy { it.index }.toTypedArray<TextureRegion>()), Animation.PlayMode.LOOP)
        })
    }

    fun region(key: String): TextureRegion {
        val reference = resolve(key)
        val atlas = atlas(reference.substringBefore("#"))
        val name = reference.substringAfter("#", missingDelimiterValue = "")
        return atlas.findRegion(name) ?: throw IllegalArgumentException("region $name missing for $key")
    }

    fun effect(key: String): Animation<TextureRegion> = animations.getOrPut(key) {
        val reference = resolve(key)
        val atlas = atlas(reference.substringBefore("#"))
        val frames = atlas.findRegions(reference.substringAfter("#")).toList().sortedBy { it.index }
        Animation<TextureRegion>(FRAME_SECONDS * 0.6f, GdxArray(frames.toTypedArray<TextureRegion>()), Animation.PlayMode.NORMAL)
    }

    fun texture(key: String): Texture {
        val path = resolve(key)
        if (!manager.isLoaded(path, Texture::class.java)) {
            manager.load(path, Texture::class.java)
            manager.finishLoadingAsset<Texture>(path)
        }
        return manager.get(path, Texture::class.java)
    }

    fun map(key: String): TiledMap {
        val path = resolve(key)
        if (!manager.isLoaded(path, TiledMap::class.java)) {
            manager.load(path, TiledMap::class.java)
            manager.finishLoadingAsset<TiledMap>(path)
        }
        return manager.get(path, TiledMap::class.java)
    }

    fun atlasPaths(): List<String> = assetMap.values.map { it.substringBefore("#") }.filter { it.endsWith(".atlas") }.distinct().sorted()

    override fun dispose() {
        manager.dispose()
    }

    companion object {
        const val FRAME_SECONDS: Float = 0.1f
    }
}
