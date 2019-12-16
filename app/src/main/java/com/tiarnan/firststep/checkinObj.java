package com.tiarnan.firststep;

import java.util.Date;

public class checkinObj {
     private String value;
     private Date date;

     public checkinObj(Date date, String value){
         this.date = date;
         this.value = value;
     }

    public Date getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }
}
