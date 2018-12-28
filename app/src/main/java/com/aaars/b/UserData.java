package com.aaars.b;

import android.support.annotation.Keep;

@Keep
public class UserData {
    public String name;
    public String email;
    public String photo;

    public UserData() { }

    public UserData(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;

    }

    public class activity { }
}
