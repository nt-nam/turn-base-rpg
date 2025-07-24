package com.game.utils.json;

import java.time.LocalDate;
import java.util.List;

public class CheckMap {
    public String name;
    public int sum;
    public List<BattleDes> battleList;

    public static class BattleDes{
        public String id;
        public LocalDate dayCheck;
    }
}
