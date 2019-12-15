package com.tiarnan.firststep;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class TextValidator implements TextWatcher {

    public TextValidator() {};

    public abstract void afterTextChanged(Editable s);

    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    public void onTextChanged(CharSequence s, int start, int before, int count){}

    public String formatVerificationCode(String input){
        String output = "";
        char char_arr[] = input.toCharArray();
        String digits = "";
        for (char c : char_arr){
            if (Character.isDigit(c)){
                digits = digits + c;
            }
        }
        int len = digits.length();
        if (len >= 5){
            output = digits.substring(0, 2) + " " + digits.substring(2, 4) + " " + digits.substring(4, Math.min(6, len));
        } else if (digits.length() >= 3){
            output = digits.substring(0, 2) + " " + digits.substring(2, len);
        } else {
            output = digits;
        }
        return output;
    }
}