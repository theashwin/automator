package com.aaars.b;

import java.util.*;

public class LastRun {
    public ArrayList<String> lastrun;


    public LastRun() {
        lastrun = new ArrayList<>();
        for(int i = 0; i < 29; i++) {
            lastrun.add("0");
        }
    }

}
