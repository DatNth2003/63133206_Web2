package com.ntd63133206.bookbuddy.util;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
	public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
	public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
