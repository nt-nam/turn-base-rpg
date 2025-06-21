package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * StatComponent chứa các chỉ số hiện tại của nhân vật trong trận chiến.
 * Dùng để thay đổi động khi bị buff/debuff/hồi máu/mất máu.
 */
public class StatComponent implements Component {
    public int hp;      // Máu hiện tại
    public int mp;      // Mana hiện tại
    public int atk;     // Sát thương vật lý
    public int def;     // Phòng thủ vật lý
    public int agi;     // Tốc độ (xác định lượt đi)
    public int crit;    // Tỉ lệ chí mạng (có thể là % hoặc số tuyệt đối)

    // Có thể mở rộng thêm nếu muốn:
    public int maxHp;   // Giới hạn máu (nếu cần hồi đầy, hoặc buff max HP)
    public int maxMp;   // Giới hạn mana

    public float critRate;    // Nếu muốn tách tỉ lệ chí mạng kiểu float
    public float critDamage;  // Hệ số nhân sát thương khi chí mạng

    // Constructor mặc định
    public StatComponent() {}

    // Constructor đầy đủ
    public StatComponent(int hp, int mp, int atk, int def, int agi, int crit) {
        this.hp = this.maxHp = hp;
        this.mp = this.maxMp = mp;
        this.atk = atk;
        this.def = def;
        this.agi = agi;
        this.crit = crit;
    }
    public StatComponent(int hp, int maxHp, int atk, int def, int mp, int maxMp, int level) {
        this.hp = hp;
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.mp = mp;
        this.maxMp = maxMp;
//        this.level = level;
    }
}
