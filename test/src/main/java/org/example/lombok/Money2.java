package org.example.lombok;

import lombok.Data;

public class Money2 {
    private String house;
    private String shoes;

    private String getHouse() {
        Money m = new Money();
//        m.getHouse();
        m.setHouse("house");
        return this.house;
    }
}
