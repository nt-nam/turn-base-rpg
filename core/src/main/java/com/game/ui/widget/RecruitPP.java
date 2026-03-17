package com.game.ui.widget;

import com.game.utils.Constants;

import static com.game.utils.Constants.BMF;


import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.hud.NotificationPP;
import com.game.utils.GameSession;
import com.game.utils.JsonSaver;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.Hero;

import java.util.List;

public class RecruitPP {
    private static UIGroup hiddenCard;
    private static UIGroup newHeroCard;
    private static Hero newHero;

    public static void show(boolean b) {
        hiddenCard.setVisible(b);
    }

    public static UIGroup pp(float w, float h) {
        UIGroup popup = new UIGroup().name("recruit");
        NinePatch ninePatch = MainGame.getAsM().get9p();
        UILabel gemUI = new UILabel(GameSession.profile.gem + "", BMF).name("number").pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f);
        new UIGroup().name("gem").pos(w * 0.07f, h * 0.1f).size(w * 0.2f, h * 0.15f).child(
            new UIImage(ninePatch).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.10f),
            gemUI
        ).index(Integer.MAX_VALUE).parent(popup);

        hiddenCard = new UIGroup().name("card").pos(w * 0.4f, h * 0.2f).size(w * 0.2f, h * 0.6f).parent(popup).origin(Align.center);
        new UIImage(ninePatch).size(w * 0.2f, h * 0.6f).parent(hiddenCard);
        new UILabel("?", BMF).pos(w * 0.03f, h * 0.3f).fontScale(12).parent(hiddenCard);

        newHeroCard = new UIGroup().name("newHeroCard").pos(w * 0.4f, h * 0.2f).size(w * 0.2f, h * 0.6f).parent(popup).origin(Align.center);
        new UIImage(ninePatch).size(w * 0.2f, h * 0.6f).parent(newHeroCard);
        new UIImage(ninePatch).name("frame").bounds(0, h * 0.2f, w * 0.2f, h * 0.35f).origin(Align.center).scale(1f).parent(newHeroCard);
        new UILabel("Đồng đội \nmới", BMF).name("nameCharacter").bounds(0, 0, w * 0.2f, h * 0.2f).align(Align.center).fontScale(1.8f).parent(newHeroCard);
        newHeroCard.setVisible(false);
        newHeroCard.onClick(() -> {
            newHeroCard.clearActions();
            newHeroCard.action(Actions.parallel(
                Actions.sequence(
                    Actions.moveBy(1000, 0, 1),
                    Actions.visible(false),
                    Actions.moveBy(-1000, 0)
                ),
                Actions.run(() -> {
                    hiddenCard.action(Actions.sequence(
                        Actions.moveBy(-1000, 0),
                        Actions.visible(true),
                        Actions.moveBy(1000, 0, 1)
                    ));
                })
            ));
            hiddenCard.setVisible(true);
            hiddenCard.scale(1);
        });
        hiddenCard.onClick(() -> {
            if (GameSession.isRecruit()) {
                hiddenCard.clearActions();
                hiddenCard.action(Actions.sequence(
                    Actions.scaleTo(0.1f, 0.1f, 1f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            gemUI.setText(GameSession.profile.gem);
                            addNewMenber();
                            showNewHeroCard();
                        }
                    })
                ));
            } else {
                NotificationPP.pprRecruit(w, h, "Cần 5 Gem để có thể chiêu mộ thành viên mới").parent(popup);
            }
        });
        return popup;
    }

    private static void showNewHeroCard() {
        ((UIImage) newHeroCard.findActor("frame")).setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegionCharacter(newHero.nameRegion, "idle")));
//        ((UILabel) newHeroCard.findActor("nameCharacter")).setText(newHero.nameRegion);
        newHeroCard.setVisible(true);
        newHeroCard.action(Actions.sequence(
            Actions.scaleTo(0.1f, 0.1f),
            Actions.scaleTo(1f, 1f, 1f, Interpolation.elasticOut))
        );
    }

    private static void addNewMenber() {
        List<CharacterBase> baseHero = GameSession.characterBaseList;
        List<Hero> fullHero = GameSession.heroList;
        int random = MathUtils.random(0, baseHero.size() - 1);
        CharacterBase characterBase = baseHero.get(random);
        Hero hero = new Hero();
        hero.characterId = "character" + (++GameSession.profile.numberOfTeammatesRecruited);
        hero.nameRegion = characterBase.nameRegion;
        hero.grid = "empty";
        hero.star = 0;
        hero.level = 1;

        hero.equip = new Hero.Equip();
        hero.equip.weapon = "empty";
        hero.equip.armor = "empty";
        hero.equip.jewelry = "empty";
        hero.equip.support = "empty";

        fullHero.add(hero);
        JsonSaver.saveObject(Constants.playerPath("hero_full.json"), fullHero);
        JsonSaver.saveObject(Constants.playerPath("info.json"), GameSession.profile);
        newHero = hero;

    }
}
