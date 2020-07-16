package com.kuaishou.kcode;

import java.io.Serializable;

public class ServicesIP implements Serializable {
    int Mainservices;
    String IPs;

    public ServicesIP(int mainservices, String IPs) {
        Mainservices = mainservices;
        this.IPs = IPs;
    }
}
