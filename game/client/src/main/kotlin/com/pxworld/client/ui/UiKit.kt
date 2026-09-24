package com.pxworld.client.ui

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable

object Tokens {
    val background: Color = Color.valueOf("14161fff")
    val surface: Color = Color.valueOf("1f2230ff")
    val surfaceRaised: Color = Color.valueOf("2a2e40ff")
    val border: Color = Color.valueOf("3a3f57ff")
    val text: Color = Color.valueOf("ecedf3ff")
    val muted: Color = Color.valueOf("9aa0b8ff")
    val accent: Color = Color.valueOf("f2b544ff")
    val accentPressed: Color = Color.valueOf("c98f25ff")
    val danger: Color = Color.valueOf("e5534bff")
    val positive: Color = Color.valueOf("57c27aff")
    val energy: Color = Color.valueOf("4aa3ffff")
    val scrim: Color = Color.valueOf("000000b0")
    val ally: Color = Color.valueOf("57c27aff")
    val enemy: Color = Color.valueOf("e5534bff")

    const val SPACE_XS: Float = 4f
    const val SPACE_S: Float = 8f
    const val SPACE_M: Float = 16f
    const val SPACE_L: Float = 24f
    const val BUTTON_HEIGHT: Float = 48f
    const val VIRTUAL_WIDTH: Float = 1280f
    const val VIRTUAL_HEIGHT: Float = 720f
}

class UiKit(fontFile: FileHandle) : Disposable {

    val skin = Skin()
    private val textures = mutableListOf<Texture>()
    private val fonts = mutableListOf<BitmapFont>()

    private val white: TextureRegion = run {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(Color.WHITE); fill() }
        TextureRegion(Texture(pixmap).also { textures += it; pixmap.dispose() })
    }
    private val rounded: NinePatch = run {
        val size = 24
        val radius = 8
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fillRectangle(radius, 0, size - 2 * radius, size)
        pixmap.fillRectangle(0, radius, size, size - 2 * radius)
        listOf(radius to radius, size - radius - 1 to radius, radius to size - radius - 1, size - radius - 1 to size - radius - 1)
            .forEach { (x, y) -> pixmap.fillCircle(x, y, radius) }
        NinePatch(Texture(pixmap).also { textures += it; pixmap.dispose() }, radius, radius, radius, radius)
    }

    val titleFont: BitmapFont = font(fontFile, 1.0f)
    val bodyFont: BitmapFont = font(fontFile, 0.68f)
    val smallFont: BitmapFont = font(fontFile, 0.54f)

    init {
        labelStyle("title", titleFont, Tokens.text)
        labelStyle("heading", bodyFont, Tokens.accent)
        labelStyle("body", bodyFont, Tokens.text)
        labelStyle("muted", smallFont, Tokens.muted)
        labelStyle("small", smallFont, Tokens.text)
        labelStyle("positive", bodyFont, Tokens.positive)
        labelStyle("negative", bodyFont, Tokens.danger)
        skin.add("default", skin.get("body", Label.LabelStyle::class.java))

        buttonStyle("primary", Tokens.accent, Tokens.accentPressed, Color.valueOf("1a1300ff"))
        buttonStyle("secondary", Tokens.surfaceRaised, Tokens.border, Tokens.text)
        buttonStyle("danger", Tokens.danger, Color.valueOf("a8342eff"), Tokens.text)
        buttonStyle("ghost", Color.CLEAR, Tokens.surfaceRaised, Tokens.muted)
        buttonStyle("tab", Tokens.surface, Tokens.surfaceRaised, Tokens.muted)
        buttonStyle("tab-active", Tokens.surfaceRaised, Tokens.surfaceRaised, Tokens.accent)
        skin.add("default", skin.get("primary", TextButton.TextButtonStyle::class.java))

        skin.add("default", ScrollPane.ScrollPaneStyle().apply {
            vScrollKnob = tinted(Tokens.border, rounded = true)
        })
        skin.add("default", TextField.TextFieldStyle().apply {
            font = bodyFont
            fontColor = Tokens.text
            messageFontColor = Tokens.muted
            background = tinted(Tokens.surfaceRaised, rounded = true).also { it.leftWidth = 12f; it.rightWidth = 12f }
            focusedBackground = tinted(Tokens.border, rounded = true).also { it.leftWidth = 12f; it.rightWidth = 12f }
            cursor = tinted(Tokens.accent).also { it.minWidth = 2f }
            selection = tinted(Tokens.energy)
        })
    }

    fun tinted(color: Color, rounded: Boolean = false): Drawable =
        if (rounded) NinePatchDrawable(this.rounded).tint(color) else TextureRegionDrawable(white).tint(color)

    val whiteRegion: TextureRegion get() = white

    private fun font(file: FileHandle, scale: Float): BitmapFont =
        BitmapFont(file).apply {
            data.setScale(scale)
            setUseIntegerPositions(false)
            region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            fonts += this
        }

    private fun labelStyle(name: String, font: BitmapFont, color: Color) {
        skin.add(name, Label.LabelStyle(font, color))
    }

    private fun buttonStyle(name: String, up: Color, down: Color, fontColor: Color) {
        skin.add(name, TextButton.TextButtonStyle().apply {
            this.up = tinted(up, rounded = true)
            this.down = tinted(down, rounded = true)
            this.over = tinted(up.cpy().lerp(Color.WHITE, 0.08f), rounded = true)
            this.disabled = tinted(Tokens.surface, rounded = true)
            this.font = bodyFont
            this.fontColor = fontColor
            this.disabledFontColor = Tokens.muted
        })
    }

    override fun dispose() {
        skin.dispose()
        fonts.forEach { it.dispose() }
        textures.forEach { it.dispose() }
    }
}
