package org.example.lombok;

import lombok.Data;

public class Test2 {
    private String house;
    private String shoes;

    private String getHouse() {
        Test m = new Test();
//        m.getHouse();
        m.setHouse("house");
        return this.house;
    }
}
