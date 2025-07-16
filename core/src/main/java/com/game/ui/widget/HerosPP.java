package com.game.ui.widget;

import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.LINEUP_ATTACK;
import static com.game.utils.Constants.PARTY_FULL;
import static com.game.utils.Constants.UI_POPUP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.game.MainGame;
import com.game.ui.base.UIButton;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UITable;
import com.game.utils.GameSession;
import com.game.utils.JsonHelper;
import com.game.utils.json.Hero;
import com.game.utils.json.Lineup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HerosPP {
    private static List<Hero> heroList = null;
    private static List<Lineup> lineupList = null;

    private static Hero heroSelect;

    private static UIGroup popup;
    private static UITable table;
    private static UIGroup grid;
    private static UIGroup detail;
    private static int page = 0;
    private static float size;
    private static float margin;

    public static Group pp(float w, float h) {
        size = h * 0.2f;
        margin = size * 0.2f;
        popup = new UIGroup().name("hero").size(w, h);

        heroList = JsonHelper.loadHeroList(PARTY_FULL, true);
        lineupList = JsonHelper.loadLineupList(LINEUP_ATTACK, true);
        heroSelect = new Hero();

        float xRootGrid = w * 0.05f;
        float yRootGrid = h * 0.12f;
        if (heroList.size() > 1) sortHero(heroList);

        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.4f, h * 0.05f, w * 0.6f, h * 0.9f);

        TextureRegion green = MainGame.getAsM().getRegion(UI_POPUP, "btn_green");
        TextureRegion gray = MainGame.getAsM().getRegion(UI_POPUP, "btn_gray");
        UIButton btnLineup = new UIButton("Đội hình", gray, green)
            .bounds((int) (w * 0.45f), (int) (h * 0.79f), w * 0.12f, h * 0.12f)
            .name("btnLineup")
            .check(() -> {
                ((UIButton) popup.findActor("btnDetail")).setChecked(false);
                ((UIButton) popup.findActor("btnLineup")).setChecked(true);
                page = 0;
            })
            .check(true)
            .fontScale(1.2f).parent(popup);

        UIButton btnDetail = new UIButton("Chi tiết", gray, green)
            .bounds((int) (w * 0.6f), (int) (h * 0.79f), w * 0.12f, h * 0.12f)
            .name("btnDetail")
            .check(() -> {
                ((UIButton) popup.findActor("btnDetail")).setChecked(true);
                ((UIButton) popup.findActor("btnLineup")).setChecked(false);
                page = 0;
            })
            .check(false)
            .fontScale(1.2f).parent(popup);


        new UILabel("Team", BMF).pos(w * 0.05f, h * 0.8f).fontScale(2.5f).parent(popup);

        btnRedirect(w, h, h * 0.1f);

        createTable(w, h, size, margin);
        createGrid(xRootGrid, yRootGrid);
        createDetail();

        return popup;
    }

    private static void createTable(float w, float h, float size, float margin) {
        table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);
        updateTable(size, margin);
        popup.addActor(table);
    }

    private static void updateTable(float size, float margin) {
        table.clearChildren();

        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup;
            if (heroList != null && i < heroList.size()) {
                final int index = i + i * page;
                Hero hero = heroList.get(index);
                uiGroup = new UIGroup().name(hero.characterId).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .name("bg")
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "rarity0", 20))
                        .name("border_" + hero.characterId)
                        .size(size, size).visible(false),
                    new UIImage(MainGame.getAsM().getRegionCharacter(hero.characterBaseId, "idle"))
                        .name("frame")
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f),
                    new UILabel(hero.level + "", BMF).pos(size * 0.1f, size * 0.1f).align(Align.center).fontScale(1.5f)
                );

                uiGroup.onClick(() -> {
                    setCharaterIdTarget(hero);
                });
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .name("bg")
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "rarity0", 20))
                        .name("border_empty")
                        .size(size, size).visible(false)
                );
            }

            table.add(uiGroup).size(size, size);
            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
    }


    private static void createGrid(float x, float y) {
        grid = new UIGroup().name("grid").pos(x, y);
        updateGrid(x, y);
        popup.addActor(grid);
    }

    private static void updateGrid(float x, float y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup item;
                if (grid.findActor(i + "," + j) == null) {
                    item = new UIGroup()
                        .name(i + "," + j)
                        .pos(size * i, size * j)
                        .size(size, size).child(
                            new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                                .size(size, size),
                            new UIImage(MainGame.getAsM().getRegion9patch(UI_POPUP, "rarity0", 20))
                                .name("select")
                                .size(size, size)
                                .visible(false),
                            new UIImage((TextureRegion) null)
                                .name("frame")
                                .size(size - margin, size - margin)
                                .pos(margin * 0.5f, margin * 0.5f)
                        ).parent(grid);
                } else {
                    item = grid.findActor(i + "," + j);
                }

                Lineup lineup = JsonHelper.get(GameSession.lineupList, "grid", i + "," + j);
                UIImage frame = item.findActor("frame");

//                    TODO tree -> grid/i,j/(UIImage)select
//                    TODO tree -> grid/i,j/(UIImage)frame
                final int m = i;
                final int n = j;
                item.onClick(() -> {
                    System.out.println("Grid: " + (lineup != null ? lineup.grid : "null"));
                    System.out.println(frame.getName());

                    if (heroSelect.characterId != null) {
                        if (lineup != null) {
                            Hero hero = JsonHelper.get(heroList, "characterId", lineup.characterId);
                            updateJsonValue(lineupList, lineup.grid, hero);
                            lineup.characterBaseId = hero.characterBaseId;
                            frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion("atlas/characters/" + lineup.characterBaseId + ".atlas", "idle")));
                            System.out.println(lineupList);
                        }
                        if (lineup == null && lineupList.size() < GameSession.sizeTeam) {
                            lineup.grid = m + "," + n;
                            lineup.characterId = heroSelect.characterId;
                            lineup.characterBaseId = heroSelect.characterBaseId;
                            lineupList.add(lineup);
                        }

                    } else {
                        // chọn
                    }
                });

            }
        }
        updateGridDrawable();
    }

    private static void createDetail() {
        if (detail == null) {
        }
    }

    private static void updateGridDrawable() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup item = grid.findActor(i + "," + j);
                Lineup lineup = JsonHelper.get(lineupList, "grid", i + "," + j);
                UIImage frame = item.findActor("frame");
                if (lineup != null) {
                    frame.setDrawable(new TextureRegionDrawable(MainGame.getAsM().getRegion("atlas/characters/" + lineup.characterBaseId + ".atlas", "idle")));
                } else {
                    frame.setVisible(false);
                }
            }
        }

    }

    private static void updateJsonValue(List<Lineup> full, String value, Hero hero) {
        for (Lineup item : full) {
            if (value.equals(item.grid)) {
                item.characterId = hero.characterId;
                item.characterBaseId = hero.characterBaseId;
                return;
            }
        }
    }

    private static void setCharaterIdTarget(Hero item) {
        // TODO popup/table/characterId/(UIImage)border_characterId
        // TODO popup/(UIGroup)i,j/(UIImage)select
        Table tableGroup = table.findActor("table");
        if (tableGroup == null) {
            System.out.println("Table not found!");
            return;
        }
        UIGroup uiGroup = tableGroup.findActor(item.characterId);
        if (uiGroup == null) {
            System.out.println("A UIGroup with name " + item.characterId + " not found!");
            return;
        }
        UIImage uiImage = uiGroup.findActor("border_" + item.characterId);
        if (uiImage == null) {
            System.out.println("UIImage with name " + item.characterId + " not found!");
            return;
        }
        UIImage uiImage2 = null;
        if (heroSelect.characterId != null) {
            System.out.println(heroSelect.characterId);
            UIGroup uiGroup2 = tableGroup.findActor(heroSelect.characterId);
            if (uiGroup2 == null) {
                System.out.println("B UIGroup with name " + heroSelect.characterId + " not found!");
                return;
            }
            uiImage2 = uiGroup2.findActor("border_" + heroSelect.characterId);
            if (uiImage == null) {
                System.out.println("UIImage with name " + heroSelect.characterId + " not found!");
                return;
            }
        }


        if (heroSelect.characterId == null) {
            uiImage.setVisible(true);
            updateSelectGrid(item.grid, true);
            heroSelect.characterId = item.characterId;
            heroSelect.characterBaseId = item.characterBaseId;
        } else {
            if (heroSelect.characterId == item.characterId) {
                uiImage.setVisible(false);
                heroSelect.characterId = null;
                heroSelect.characterBaseId = null;
                updateSelectGrid(item.grid, false);
            } else {
                uiImage.setVisible(true);
                uiImage2.setVisible(false);
                updateSelectGrid(item.grid, true);
                heroSelect.characterId = item.characterId;
                heroSelect.characterBaseId = item.characterBaseId;
            }
        }
    }

    private static void updateSelectGrid(String grid, boolean b) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String name = i + "," + j;
                if (popup.findActor(name) != null) {
                    if (name.equals(grid)) {
                        getUIImageFromGrid(name).setVisible(b);
                    } else {
                        if (getUIImageFromGrid(name) != null)
                            getUIImageFromGrid(name).setVisible(false);
                    }
                }
            }
        }
    }

    // Hàm trợ giúp để lấy UIImage theo characterId
    static UIImage getUIImageFromTable(String id) {
        UIGroup uiGroup = popup.findActor(id);
        if (uiGroup == null) {
            System.out.println("C UIGroup with name " + id + " not found!");
            return null;
        }
        return uiGroup.findActor("border_" + id);
    }

    // Hàm trợ giúp để lấy UIImage của select trong grid
    static UIImage getUIImageFromGrid(String grid) {
        UIGroup gridGroup = (UIGroup) popup.findActor(grid);
        if (gridGroup == null) {
            System.out.println("D UIGroup with name " + grid + " not found!");
            return null;
        }
        return gridGroup.findActor("select");
    }

    private static void btnRedirect(float w, float h, float size) {
        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_up"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.63f)
            .onClick(() -> {
                page -= page != 0 ? 1 : 0;
            })
            .parent(popup);

        new UIButton(MainGame.getAsM().getRegion(UI_POPUP, "btn_down"))
            .size(size, size)
            .pos(w * 0.93f, h * 0.09f)
            .onClick(() -> {
                if ((page + 1) * 21 < heroList.size()) {
                    page++;
                }
            })
            .parent(popup);
    }

    public static List<Hero> sortHero(List<Hero> heroList) {
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                return Integer.compare(hero2.level, hero1.level);
            }
        });
        Collections.sort(heroList, new Comparator<Hero>() {
            @Override
            public int compare(Hero hero1, Hero hero2) {
                String grid1 = hero1.grid;
                String grid2 = hero2.grid;

                if (!hero1.grid.equals("empty") && grid2.equals("empty")) {
                    return -1;
                } else if (grid1.equals("empty") && !grid2.equals("empty")) {
                    return 1;
                }
                return 0;
            }
        });
        return heroList;
    }


}
