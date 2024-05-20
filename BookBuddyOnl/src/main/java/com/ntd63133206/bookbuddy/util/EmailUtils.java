package com.ntd63133206.bookbuddy.util;

public class EmailUtils {

    public static String getEmailMessage(String name, String link) {
        return "<p>Hello " + name + ",</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, or you have not made the request.</p>";
    }

}