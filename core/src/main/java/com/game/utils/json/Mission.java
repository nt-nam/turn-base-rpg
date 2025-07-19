package com.game.utils.json;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    public String idBase;
    public String title;
    public String description;
    public int progress;
    public int targetAmount;
    public List<Reward> rewards = new ArrayList<>();
}
