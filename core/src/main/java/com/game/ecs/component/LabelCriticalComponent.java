package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.ui.base.UILabel;

public class LabelCriticalComponent implements Component {
    public UILabel label;
    public LabelCriticalComponent(UILabel label){
        this.label = label;
    }
}
