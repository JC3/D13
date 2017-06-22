package d13.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

public class Util {
    
    public static String intAmountToString (int cents) {
        
        float dollars = (float)cents / 100.f;
        return String.format("$%.2f", dollars);
        
    }

    public static String getAbsoluteUrl (HttpServletRequest request, String rootRelative) {
        
        String abs = null;
        URL req = null, root = null, url = null;
        
        try {
            req = new URL(request.getRequestURL().toString());
            root = new URL(req, request.getContextPath() + "/");
            url = new URL(root, rootRelative);
            abs = url.toString();
        } catch (MalformedURLException e) {
        }

        //System.out.println(rootRelative + " => " + req + " => " + root + " => " + url + " => " + abs);
        //System.out.println("  " + request.getRequestURI());
        //System.out.println("  " + request.getContextPath());
        
        return abs;
        
    }
    
    public static String getCompleteUrl (HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static int parseIntDefault (String str, int def) {
        if (str == null)
            return def;
        try {
            return Integer.parseInt(str);
        } catch (Throwable t) {
            return def;
        }
    }
    
    public static Long getParameterLong (ServletRequest request, String param) {
        String s = request.getParameter(param);
        Long v = null;
        if (s != null) {
            try {
                v = Long.parseLong(s);
            } catch (Throwable t) {
            }
        }
        return v;
    }
    
    public static String html (String value) {
        if (value == null)
            return "";
        else
            return StringEscapeUtils.escapeHtml(value);
    }

    public static boolean unbox (Boolean b) {
        if (b == null)
            return false;
        else
            return b;
    }
    
    public static String require (String str, String what) {
        str = (str == null ? "" : str.trim());
        if (str.isEmpty())
            throw new IllegalArgumentException(what + " must be specified.");
        return str;
    }
    
    public static String requireEmail (String str, String what) {
        if (str != null) {
            try {
                InternetAddress ia = new InternetAddress(str);
                ia.validate();
                str = ia.getAddress();
            } catch (Throwable t) {
                str = null;
            }
        }
        if (str == null)
            throw new IllegalArgumentException(what + " must be specified.");
        return str;
    }
    
    public static String validatePhoneNumber (String s) {
        // only u.s. 10 digit numbers are valid for now
        StringBuilder pn = new StringBuilder();
        for (int n = 0; n < s.length(); ++ n)
            if (Character.isDigit(s.charAt(n)))
                pn.append(s.charAt(n));
        if (pn.length() != 10 && pn.length() != 0) // either 10-digit, or none
            throw new IllegalArgumentException("Phone number, if specified, must be a 10-digit US phone number.");
        return pn.toString();
    }
    
    public static String randomString (int length) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < length; ++ n) {
            int v = (int)(Math.random() * 62.0);
            if (v < 26)
                sb.append((char)(v + 'a'));
            else if (v < 52)
                sb.append((char)(v - 26 + 'A'));
            else if (v < 62)
                sb.append((char)(v - 52 + '0'));
            else
                sb.append('9');
        }
        return sb.toString();
    }
    
    public static String hashString (String str) throws SecurityException {
        
        if (str == null)
            str = "";
        
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] h = md.digest(str.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b:h) {
                if (b >= 0 && b < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(b & 255));
            }
            
            return sb.toString().toLowerCase();
            
        } catch (Throwable t) {
            
            throw new SecurityException(t);
            
        }
        
    }
    
}
