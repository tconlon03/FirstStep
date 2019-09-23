package com.tiarnan.firststep;

public interface OnSendConfirmationListener {
    void sendConfirmationText();
    void sendConfirmationEmail();
    void validateEnteredVerificationCode(String input);
}
