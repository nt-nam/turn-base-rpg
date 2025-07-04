package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.ui.base.UILabel;

public class LabelComponent implements Component {
    public UILabel label;
    public LabelComponent(UILabel label){
        this.label = label;
    }
}
