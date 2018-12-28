package com.aaars.b;

import java.util.*;

public class Module {
    public int activityid;
    public int triggerid;
    public Boolean enabled;
    public ArrayList<String> parameters;


    public Module() {
        activityid = -1;
        triggerid = -1;
        enabled = false;
        parameters = new ArrayList<>();
    }

    public void onStart() {
        if(enabled) {
            switch(activityid) {
                case 1:
                    break;
                case -1:
                    return;
            }

            switch(triggerid) {
                case 1:
                    break;
                case -1:
                    return;
            }
        }
    }
}
