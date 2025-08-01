package com.game.ui.hud;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.json.Hero;

public class HeroFact {

    public static UIGroup getHero(Hero hero, float sizeTile) {
        return new UIGroup().name(hero.characterId).child(
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "tile_rarity" +hero.star, 20))
                .name("bg")
                .size(sizeTile, sizeTile),
            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "select", 20))
                .name("imageSelect")
                .size(sizeTile, sizeTile).visible(false).color(Color.YELLOW)
                .action(Actions.forever(Actions.sequence(Actions.fadeOut(0.5f), Actions.fadeIn(0.5f)))),
            new UIImage(MainGame.getAsM().getRegionCharacter(hero.nameRegion, "idle"))
                .name("frame")
                .size(sizeTile, sizeTile)
                .align(Align.center).scale(0.8f).visible(false),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "icon_battle"))
                .name("lineup")
                .size(sizeTile * 0.25f, sizeTile * 0.25f)
                .pos(sizeTile * 0.7f, sizeTile * 0.7f).visible(false),
            new UILabel("", BMF)
                .name("textLevel")
                .pos(sizeTile * 0.2f, sizeTile * 0.2f).align(Align.center).fontScale(1f)
        );
    }
}
