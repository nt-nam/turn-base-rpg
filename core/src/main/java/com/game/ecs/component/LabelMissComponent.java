package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.ui.base.UILabel;

public class LabelMissComponent implements Component {
    public UILabel label;
    public LabelMissComponent(UILabel label){
        this.label = label;
    }
}
