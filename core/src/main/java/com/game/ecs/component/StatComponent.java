package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.JsonValue;

/**
 * StatComponent chứa các chỉ số hiện tại của nhân vật trong trận chiến.
 * Dùng để thay đổi động khi bị buff/debuff/hồi máu/mất máu.
 */
public class StatComponent implements Component {
    public int hp;
    public int mp;
    public int atk;
    public int def;
    public int agi;
    public int crit;

    public int maxHp;
    public int maxMp;

    public float critRate;
    public float critDamage;

    // Constructor mặc định
    public StatComponent() {}

    // Constructor với các giá trị mặc định
    public StatComponent(int hp, int mp, int atk, int def, int agi, int crit) {
        this.hp = this.maxHp = hp;
        this.mp = 0;
        this.maxMp = mp;
        this.atk = atk;
        this.def = def;
        this.agi = agi;
        this.crit = crit;
    }

    public StatComponent fromJson(JsonValue json) {
        if (json == null) {
            return null;
        }

        // Đọc các trường cơ bản
        hp = json.getInt("hp", 0);
        mp = json.getInt("mp", 0);
        atk = json.getInt("atk", 0);
        def = json.getInt("def", 0);
        agi = json.getInt("agi", 0);
        crit = json.getInt("crit", 0);

        maxHp = json.getInt("maxHp", hp);
        maxMp = json.getInt("maxMp", mp);

        critRate = json.getFloat("critRate", 0f);
        critDamage = json.getFloat("critDamage", 1f);

        return this;
    }

    public StatComponent fromCharacter(CharacterComponent character) {
        if (character == null) {
            return null;
        }

        this.hp = character.hp;
        this.mp = character.mp;
        this.atk = character.atk;
        this.def = character.def;
        this.agi = character.agi;
        this.crit = character.crit;

        this.maxHp = this.hp;
        this.maxMp = this.mp;

        this.critRate = 0.1f;
        this.critDamage = 1.5f;

        return this;
    }
}
