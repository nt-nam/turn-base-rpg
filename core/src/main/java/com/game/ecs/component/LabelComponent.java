package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import java.util.ArrayList;
import java.util.List;

public class LabelComponent implements Component {
    public static class DamageText {
        public String text;
        public float x;
        public float y;
        public float vx; // velocity x
        public float vy; // velocity y
        public float lifeTime;
        public float maxLifeTime;
        public boolean isCritical;
        public boolean isHeal;
        
        public DamageText(String text, float x, float y, boolean isCritical) {
            this(text, x, y, isCritical, false);
        }

        public DamageText(String text, float x, float y, boolean isCritical, boolean isHeal) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.maxLifeTime = 1.2f; // slightly longer lifetime for smooth fade
            this.lifeTime = this.maxLifeTime;
            this.isCritical = isCritical;
            this.isHeal = isHeal;
            
            // Random scatter trajectory
            this.vx = (float) (Math.random() * 80 - 40);
            this.vy = (float) (Math.random() * 100 + 150); // initial upward burst
            if (isCritical) {
                this.vy += 100; // crit bursts higher
                this.vx *= 1.5f;
            }
        }
    }
    
    public List<DamageText> activeLabels = new ArrayList<>();
}
