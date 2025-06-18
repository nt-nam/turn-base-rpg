package com.game.utils.data;

import com.badlogic.gdx.utils.Array;

public class SkillManager {
    public static Array<SkillData> allSkills = new Array<>();

    public static void loadAllSkills() {
        // ví dụ: load từ file json hoặc file text mà bạn paste ở trên
        // allSkills.add(new SkillData(...));
    }
    public static SkillData getSkill(String skillId, int frameIndex) {
        for (SkillData s : allSkills)
            if (s.skillId.equals(skillId) && s.frameIndex == frameIndex) return s;
        return null;
    }
}
