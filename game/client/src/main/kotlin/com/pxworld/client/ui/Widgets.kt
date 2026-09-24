package com.pxworld.client.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.pxworld.client.core.Localization

class Widgets(private val kit: UiKit, private val text: Localization, private val onClickFeedback: () -> Unit = {}) {

    val skin get() = kit.skin

    fun label(value: String, style: String = "body", testId: String? = null, wrap: Boolean = false): Label =
        Label(value, kit.skin, style).apply {
            testId?.let { name = it }
            this.wrap = wrap
        }

    fun button(testId: String, caption: String, style: String = "primary", enabled: Boolean = true, onClick: () -> Unit): TextButton =
        TextButton(caption, kit.skin, style).apply {
            name = testId
            isDisabled = !enabled
            label.setWrap(false)
            pad(Tokens.SPACE_S, Tokens.SPACE_M, Tokens.SPACE_S, Tokens.SPACE_M)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent, actor: Actor) {
                    if (!isDisabled) {
                        onClickFeedback()
                        onClick()
                    }
                }
            })
        }

    fun textField(testId: String, value: String, hint: String): TextField =
        TextField(value, kit.skin).apply {
            name = testId
            messageText = hint
        }

    fun panel(padding: Float = Tokens.SPACE_M, color: Color = Tokens.surface): Table =
        Table().apply {
            background = kit.tinted(color, rounded = true)
            pad(padding)
        }

    fun scroll(content: Table, testId: String): ScrollPane =
        ScrollPane(content, kit.skin).apply {
            name = testId
            setFadeScrollBars(false)
            setScrollingDisabled(true, false)
        }

    fun image(region: TextureRegion, size: Float, testId: String? = null): Image =
        Image(region).apply {
            setScaling(Scaling.fit)
            testId?.let { name = it }
            setSize(size, size)
        }

    fun header(screenId: String, title: String, onBack: (() -> Unit)?): Table =
        Table().apply {
            pad(Tokens.SPACE_S, Tokens.SPACE_M, Tokens.SPACE_S, Tokens.SPACE_M)
            background = kit.tinted(Tokens.surface)
            if (onBack != null) add(button("$screenId/back", text("ui.common.back"), "secondary", onClick = onBack)).padRight(Tokens.SPACE_M)
            add(label(title, "title", "$screenId/title")).expandX().left()
        }

    fun bar(fraction: () -> Float, color: Color, width: Float, height: Float): Actor = ValueBar(kit.whiteRegion, fraction, color).apply {
        setSize(width, height)
    }

    fun centered(value: String, style: String = "body"): Label = label(value, style).apply { setAlignment(Align.center) }
}

class ValueBar(private val white: TextureRegion, private val fraction: () -> Float, private val fill: Color) : Actor() {

    override fun draw(batch: Batch, parentAlpha: Float) {
        val previous = batch.color.cpy()
        batch.setColor(0f, 0f, 0f, 0.6f * parentAlpha)
        batch.draw(white, x, y, width, height)
        batch.setColor(fill.r, fill.g, fill.b, fill.a * parentAlpha)
        batch.draw(white, x + 1, y + 1, (width - 2) * fraction().coerceIn(0f, 1f), height - 2)
        batch.color = previous
    }
}
