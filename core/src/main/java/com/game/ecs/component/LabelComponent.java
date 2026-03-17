package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import java.util.ArrayList;
import java.util.List;

public class LabelComponent implements Component {
    public static class DamageText {
        public String text;
        public float x;
        public float y;
        public float lifeTime;
        public boolean isCritical;
        
        public DamageText(String text, float x, float y, boolean isCritical) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.lifeTime = 1.0f; // 1 second default lifetime
            this.isCritical = isCritical;
        }
    }
    
    public List<DamageText> activeLabels = new ArrayList<>();
}
