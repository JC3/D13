package d13.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import d13.dao.User;
import d13.dao.UserState;

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

    public static String getCompleteUri (HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL;
        } else {
            return requestURL + "?" + queryString;
        }
    }

    public static int parseIntDefault (String str, int def) {
        if (str == null)
            return def;
        try {
            return Integer.parseInt(str.trim());
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
    
    private static Object pnMutex = new Object();
    
    public static class PhoneNumberInfo {
        public String raw;
        public String formatted;
        public String normalized;
        public String fromUS;
        public String region;
    }
    
    public static PhoneNumberInfo parsePhoneNumber (String s) {
        
        if (s == null || s.trim().isEmpty())
            return null;
        
        // Unsure if PhoneNumberUtil is thread-safe, so just assume not.
        synchronized (pnMutex) {
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                PhoneNumber pn = pnu.parseAndKeepRawInput(s, "US");
                if (!pnu.isValidNumber(pn))
                    throw new IllegalArgumentException("Specified phone number does not appear to be valid. If it is a non-US number please be sure to include the country code (possibly with a + sign in front).");
                PhoneNumberInfo info = new PhoneNumberInfo();
                info.raw = s.trim();
                info.formatted = pnu.formatInOriginalFormat(pn, "US");
                info.normalized = PhoneNumberUtil.normalizeDigitsOnly(info.formatted);
                info.region = pnu.getRegionCodeForNumber(pn);
                if ("US".equals(info.region) || "CA".equals(info.region))
                    info.fromUS = info.formatted.replaceAll("[^0-9]+", " ").trim();
                else
                    info.fromUS = pnu.formatOutOfCountryCallingNumber(pn, "US");
                return info;
            } catch (NumberParseException x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
        
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
    
    // Source: https://stackoverflow.com/a/15323776, modified slightly.
    public static String ip (HttpServletRequest request) {
        
        String ip = request.getHeader("X-Forwarded-For"); // set by nginx on server  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("X-Real-IP");  // set by nginx on server
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_CLIENT_IP");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();  
        if (ip == null || "unknown".equalsIgnoreCase(ip))
            ip = "";
        
        return ip;
        
    }  
    
    
    //=============================================================================================
    // HACKY INTERNAL STUFF, DO NOT USE IN MAIN APP
    //=============================================================================================
    
    /** For phone.jsp use only! */
    public static String formatPhoneNumber (String s, PhoneNumberFormat fmt) {
        if (s == null || s.trim().isEmpty())
            return "";
        synchronized (pnMutex) {
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                PhoneNumber pn = pnu.parseAndKeepRawInput(s, "US");
                if (!pnu.isValidNumber(pn))
                    throw new IllegalArgumentException();
                if (fmt == null)
                    return pnu.formatOutOfCountryCallingNumber(pn, "US");
                else
                    return pnu.format(pn, fmt);
            } catch (NumberParseException x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
    }
    
    /** For phone.jsp use only! */
    public static String formatPhoneNumberMobile (String s) {
        if (s == null || s.trim().isEmpty())
            return "";
        synchronized (pnMutex) {
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                PhoneNumber pn = pnu.parseAndKeepRawInput(s, "US");
                if (!pnu.isValidNumber(pn))
                    throw new IllegalArgumentException();
                return pnu.formatNumberForMobileDialing(pn, "US", true);
            } catch (NumberParseException x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
    }
    
    /** For phone.jsp use only! */
    public static String getPhoneNumberRegion (String s) {
        if (s == null || s.trim().isEmpty())
            return "";
        synchronized (pnMutex) {
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                PhoneNumber pn = pnu.parseAndKeepRawInput(s, "US");
                String code = pnu.getRegionCodeForNumber(pn);
                return code == null ? "" : code;
            } catch (NumberParseException x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
    }
    
    /** For phone.jsp use only! */
    public static List<String> getPhoneNumbers (String s) {
        List<String> numbers = new ArrayList<String>();
        synchronized (pnMutex) {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            try {
                for (PhoneNumberMatch pnm : pnu.findNumbers(s, "US")) {
                    //PhoneNumber pn = pnm.number();
                    PhoneNumber pn = pnu.parseAndKeepRawInput(pnm.rawString(), "US");
                    String pns = pnu.formatInOriginalFormat(pn, "US");
                    numbers.add(pns);
                }
            } catch (Exception x) {
                throw new IllegalArgumentException(x.getMessage(), x);
            }
        }
        return numbers;
    }
    
    /** For check.jsp use only! */
    public static class PhoneCheckData {
        private final Map<PhoneNumber,List<Long>> numbers = new HashMap<PhoneNumber,List<Long>>();
        private final Map<Long,List<PhoneNumber>> contacts = new HashMap<Long,List<PhoneNumber>>();
        public PhoneCheckData (Collection<User> users) {
            synchronized (pnMutex) {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                for (User u : users) {
                    if (u.getState() == UserState.NEW_USER)
                        continue;
                    try {
                        PhoneNumber pn = pnu.parseAndKeepRawInput(u.getPhone(), "US");
                        List<Long> uids = numbers.get(pn);
                        if (uids == null) {
                            uids = new ArrayList<Long>();
                            numbers.put(pn, uids);
                        }
                        uids.add(u.getUserId());
                    } catch (Exception x) {
                    }
                    try {
                        List<PhoneNumber> contactpn = new ArrayList<PhoneNumber>();
                        for (PhoneNumberMatch pnm : pnu.findNumbers(u.getEmergencyContact(), "US")) {
                            PhoneNumber pn = pnu.parseAndKeepRawInput(pnm.rawString(), "US");
                            contactpn.add(pn);
                        }
                        contacts.put(u.getUserId(), contactpn);
                    } catch (Exception x) {
                    }
                }
            }
        }
        public static class Match {
            public String thisPhone;
            public User otherUser;
        }
        public List<Match> getECMatches (User u) {
            List<PhoneNumber> thisContacts = contacts.get(u.getUserId());
            if (thisContacts == null || thisContacts.isEmpty())
                return Collections.emptyList();
            List<Match> matches = new ArrayList<Match>();
            synchronized (pnMutex) {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                for (PhoneNumber thisContact : thisContacts) {
                    List<Long> otherUsers = numbers.get(thisContact);
                    if (otherUsers == null)
                        continue;
                    for (Long uid : otherUsers) {                        
                        Match match = new Match();
                        match.thisPhone = pnu.formatInOriginalFormat(thisContact, "US");
                        match.otherUser = User.findById(uid);
                        matches.add(match);
                    }
                }
            }
            return matches;
        }
    }
    
}
