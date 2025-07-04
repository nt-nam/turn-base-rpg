package com.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.game.ui.base.UIProgressBar;

public class ProgressBarComponent implements Component {
    public UIProgressBar progressBar;
    public ProgressBarComponent(UIProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
