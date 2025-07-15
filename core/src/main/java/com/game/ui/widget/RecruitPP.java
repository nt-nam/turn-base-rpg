package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.PARTY_FULL;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.hud.NotificationPP;
import com.game.utils.GameSession;
import com.game.utils.JsonHelper;
import com.game.utils.JsonSaver;
import com.game.utils.json.CharacterBase;
import com.game.utils.json.Hero;

import java.util.HashMap;
import java.util.List;

public class RecruitPP {
    private static UIGroup hiddenCard;

    public static UIGroup pp(float w, float h) {
        UIGroup popup = new UIGroup().name("recruit");
        NinePatch ninePatch = MainGame.getAsM().get9p();
        UILabel gemUI = new UILabel(GameSession.gem + "", BMF).name("number").pos(h * 0.15f, 0).size(w * 0.15f, h * 0.12f);
        new UIGroup().name("gem").pos(w * 0.07f, h * 0.1f).size(w * 0.2f, h * 0.15f).child(
            new UIImage(ninePatch).size(w * 0.15f, h * 0.12f),
            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "gem_pink")).pos(h * 0.01f, h * 0.01f).size(h * 0.1f, h * 0.10f),
            gemUI
        ).index(Integer.MAX_VALUE).parent(popup);

        hiddenCard = new UIGroup().name("card").pos(w * 0.4f, h * 0.2f).size(w * 0.2f, h * 0.6f).parent(popup).origin(Align.center);
        new UIImage(ninePatch).size(w * 0.2f, h * 0.6f).parent(hiddenCard);
        new UILabel("?", BMF).pos(w * 0.03f, h * 0.3f).fontScale(12).parent(hiddenCard);
        hiddenCard.onClick(() -> {
            if (GameSession.isRecruit()) {
                hiddenCard.action(Actions.sequence(
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            gemUI.setText(GameSession.gem);
                            addNewMenber();
                        }
                    }),
                    Actions.parallel(
                        Actions.rotateBy(2880f, 2f),
                        Actions.scaleTo(0.1f, 0.1f, 2f)
                    ),
                    Actions.scaleTo(1f, 1f, 0.5f)
                ));
            } else {
                NotificationPP.pprRecruit(w, h, "Cần 5 Gem để có thể chiêu mộ thành viên mới").parent(popup);
            }
        });
        return popup;
    }

    private static void addNewMenber() {
        List<CharacterBase> baseHero = JsonHelper.baseHero;
        List<Hero> fullHero = JsonHelper.fullHero;
        int random = MathUtils.random(baseHero.size());
        CharacterBase characterBase = baseHero.get(random);
        Hero hero = new Hero();
        hero.characterId = characterBase.characterId;
        hero.characterBaseId = characterBase.characterBaseId;
        hero.grid = "empty";
        hero.star = 0;
        hero.level = 1;

        hero.characterBase = new CharacterBase();
        hero.characterBase.characterId = characterBase.characterId;
        hero.characterBase.characterBaseId = characterBase.characterBaseId;
        hero.characterBase.classType = characterBase.classType;
        hero.characterBase.role = characterBase.role;
        hero.characterBase.name = characterBase.name;
        hero.characterBase.desc = characterBase.desc;
        hero.characterBase.hp = characterBase.hp;
        hero.characterBase.mp = characterBase.mp;
        hero.characterBase.atk = characterBase.atk;
        hero.characterBase.def = characterBase.def;
        hero.characterBase.agi = characterBase.agi;
        hero.characterBase.crit = characterBase.crit;
        hero.characterBase.skills = characterBase.skills;
        hero.characterBase.counters = characterBase.counters;
        hero.characterBase.weakAgainst = characterBase.weakAgainst;

        hero.equip = new HashMap<>();

        fullHero.add(hero);
        JsonSaver.saveObject(PARTY_FULL,fullHero);
    }
}
