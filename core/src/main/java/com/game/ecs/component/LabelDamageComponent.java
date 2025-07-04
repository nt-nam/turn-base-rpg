package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.ui.base.UILabel;

public class LabelDamageComponent implements Component {
    public UILabel label;
    public LabelDamageComponent(UILabel label){
        this.label = label;
    }
}
