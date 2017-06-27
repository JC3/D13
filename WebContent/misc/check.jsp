<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="org.joda.time.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.codec.language.*" %>
<%@ page import="org.apache.commons.codec.*" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
static class Problem {
    final User who;
    final String what;
    int following = -1;
    Problem (User who, String what) {
        this.who = who;
        this.what = what;
    }
}

static class ReviewTimes {
    int rrt = -1;
    int rat = -1;
}

static int countMatches (String s, String regex) {
    
    Matcher m = Pattern.compile(regex).matcher(s);
    
    int count = 0;
    while (m.find())
        ++ count;
    
    return count;
    
}

static class CheckCtx {
    Caverphone2 cav = new Caverphone2();
    Nysiis nys = new Nysiis();
    DaitchMokotoffSoundex dmk = new DaitchMokotoffSoundex();
    Map<String,String> cavCache = new HashMap<String,String>();
    Map<String,String> nysCache = new HashMap<String,String>();
    Map<String,String> dmkCache = new HashMap<String,String>();
    String cachedCav (String s) {
        String enc = cavCache.get(s);
        if (enc == null) {
            enc = cav.encode(s);
            cavCache.put(s, enc);
        }
        return enc;
    }
    String cachedNys (String s) {
        String enc = nysCache.get(s);
        if (enc == null) {
            enc = nys.encode(s);
            nysCache.put(s, enc);
        }
        return enc;
    }
    String cachedDmk (String s) {
        String enc = dmkCache.get(s);
        if (enc == null) {
            enc = dmk.encode(s);
            dmkCache.put(s, enc);
        }
        return enc;
    }
}

static boolean phoneticEquals (String n1, String n2, CheckCtx ctx) {
    
    List<String> ns1 = Arrays.asList(n1.split("\\b"));
    List<String> ns2 = Arrays.asList(n2.split("\\b"));
    int matches = 0;
    
    for (int i1 = 0; i1 < ns1.size(); ++ i1) {
        if (ns1.get(i1) == null)
            continue;
        for (int i2 = 0; i2 < ns2.size(); ++ i2) {
            if (ns2.get(i2) == null)
                continue;
            if (ctx.cachedCav(ns1.get(i1)).equals(ctx.cachedCav(ns2.get(i2))) || 
                ctx.cachedNys(ns1.get(i1)).equals(ctx.cachedNys(ns2.get(i2))) ||
                ctx.cachedDmk(ns1.get(i1)).equals(ctx.cachedDmk(ns2.get(i2)))) 
            {
                ++ matches;
                ns1.set(i1, null);
                ns2.set(i2, null);
                break;
            }
        }
    }
    
    return (matches == Math.min(ns1.size(), ns2.size()));
    
}

static boolean phoneticEqualsSimple (String n1, String n2, CheckCtx ctx) {

    /*
    return (ctx.cachedCav(n1).equals(ctx.cachedCav(n2)) || 
            ctx.cachedNys(n1).equals(ctx.cachedNys(n2)) ||
            ctx.cachedDmk(n1).equals(ctx.cachedDmk(n2)));
    */
    if (n1.equalsIgnoreCase(n2))
        return true;
    else {
        try {
		    RefinedSoundex ref = new RefinedSoundex();
		    return ref.encode(n1).equals(ref.encode(n2));
        } catch (Throwable t) {
            //System.out.println(n1 + " => " + n2 + ": " + t.getMessage());
            return false;
        }
    }
    
}

static String onPlaya (String s, List<String> usernamesForFuzzy, List<String> playanamesForFuzzy, int fuzzyThresh, boolean phonetic, CheckCtx ctx) {
    
    Matcher m = Pattern.compile("[\\p{IsL}]+").matcher(s);
    
    String n = null;
    Set<String> people = new TreeSet<String>();
    while (m.find()) {
        String lastn = n;
        n = m.group();
        if (lastn != null) {
            String nm = lastn + " " + n;
            List<User> nu = UserSearchFilter.search(nm);
            for (User nuu : nu) {
                if (nuu.getState() != UserState.NEW_USER) {
                    people.add(nuu.getRealName());
                }
            }
            if (usernamesForFuzzy != null) {
                if (fuzzyThresh > 0) {
	                for (String uname : usernamesForFuzzy)
	                    if (StringUtils.getLevenshteinDistance(nm, uname) < fuzzyThresh)
	                        people.add(uname/* + " [lev]"*/);
	            }
                if (phonetic) {
	                for (String uname : usernamesForFuzzy)
	                    if (phoneticEquals(uname, nm, ctx))
	                        people.add(uname/* + " [phonetic]"*/);
	            }
            }
        }
        // playa names
        if (playanamesForFuzzy != null) {
            //if (fuzzyThresh > 0) {
            //    for (String uname : playanamesForFuzzy)
            //        if (StringUtils.getLevenshteinDistance(n, uname) < fuzzyThresh)
            //            people.add(uname + " [playa,lev]");
            //}
            if (phonetic) {
                for (String uname : playanamesForFuzzy)
                    if (phoneticEqualsSimple(uname, n, ctx))
                        people.add(uname + " [Playa Name]");
            }
        }
    }
    
    if (!people.isEmpty())
        return StringUtils.join(people, ", ");
    else   
        return null;
    
}

static ReviewTimes calcReviewTimes (User u) {
    
    ReviewTimes t = new ReviewTimes();
    DateTime rt = null, at = null;
    
    for (ActivityLogEntry e : u.getActivityLog()) {
        if (e.getDescription().contains("Needs Review to Registered"))
            rt = e.getTime();
        else if (e.getDescription().contains("Registered to Approval Pending"))
            at = e.getTime();
    }
 
    if (rt != null)
        t.rrt = Seconds.secondsBetween(u.getRegisteredOn(), rt).getSeconds();
    if (rt != null && at != null)
        t.rat = Seconds.secondsBetween(rt, at).getSeconds();
    
    return t;
    
}

static void writeProblems (JspWriter out, List<Problem> problems, String name, String detailUrlFmt) throws java.io.IOException {

    out.println("<tr>");
    out.println("  <td class=\"section\" colspan=\"5\">" + Util.html(name) + " (" + problems.size() + ")");
    
    out.println("<tr>");
    out.println("  <td class=\"header\">ID");
    out.println("  <td class=\"header\">Name");
    out.println("  <td class=\"header\">Email");
    out.println("  <td class=\"header\">State");
    out.println("  <td class=\"header\">Problem");
    
    Problem cur = null;
    for (Problem p : problems) {
        if (cur == null || p.who != cur.who)
            cur = p;
        p.following = 0;
        cur.following = cur.following + 1;
    }
    
    for (Problem p : problems) {
        String rowspan = (p.following > 1 ? (" rowspan=\"" + p.following + "\"") : "");
        out.println("<tr>");
        if (p.following > 0) {
            String detailUrl = String.format(detailUrlFmt, p.who.getUserId());
	        out.println(String.format("  <td%s class=\"info userid\"><a href=\"%s\" target=\"_blank\">%s</a>", rowspan, Util.html(detailUrl), p.who.getUserId()));
	        out.println("  <td" + rowspan + " class=\"info\">" + Util.html(p.who.getRealName()));
	        out.println("  <td" + rowspan + " class=\"info\">" + Util.html(p.who.getEmail()));
	        out.println("  <td" + rowspan + " class=\"info\">" + Util.html(p.who.getState().toString()));
        }
        out.println("  <td>" + Util.html(p.what));
    }
    
    out.println("<tr>");
    out.println("  <td class=\"spacer\" colspan=\"5\">&nbsp;");

}

static void probadd (List<Problem> a, List<Problem> b, Problem p) {
    a.add(p);
    b.add(p);
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User user = sess.getUser();
if (!user.getRole().canViewUsers())
    return;

boolean sep = (request.getParameter("separate") != null);

long tstart = System.nanoTime();

List<User> users = User.findAll("realName");

List<Problem> allprob = new ArrayList<Problem>();
List<Problem> qprob = new ArrayList<Problem>();
List<Problem> eprob = new ArrayList<Problem>();
List<Problem> rprob = new ArrayList<Problem>();
List<Problem> vprob = new ArrayList<Problem>();
List<Problem> cprob = new ArrayList<Problem>();

List<String> usernamesForFuzzy = null, playanamesForFuzzy = null;
if (true) {
    usernamesForFuzzy = new ArrayList<String>();
    for (User u : users)
        if (u.getState() != UserState.NEW_USER)
            usernamesForFuzzy.add(u.getRealName());
}
if (true) {
    playanamesForFuzzy = new ArrayList<String>();
    for (User u : users)
        if (u.getState() != UserState.NEW_USER && !StringUtils.trimToEmpty(u.getPlayaName()).isEmpty())
            playanamesForFuzzy.add(u.getPlayaName());
}

int optQuickReviewTime = 180;
int optQuickApproveTime = 45;
int optECMinLetters = 5;
int optECMinNumbers = 10;
int optECFuzzyThreshold = 3;
boolean optECPhonetic = true;

CheckCtx ctx = new CheckCtx();

for (User u : users) {
    
    if (!u.isRegistrationComplete() || u.getState() == UserState.REJECTED)
        continue;
    
    RegistrationForm r = u.getRegistration();
    ApprovalSurvey a = (u.isApprovalComplete() ? u.getApproval() : null);

    // --- reviewed too quickly ---
    
    ReviewTimes rt = calcReviewTimes(u);
    if (rt.rrt >= 0 && rt.rrt <= optQuickReviewTime)
        probadd(allprob, qprob, new Problem(u, "Quick review (review completed " + rt.rrt + " seconds after registering); might want to re-review."));
    if (rt.rat >= 0 && rt.rat <= optQuickApproveTime)
        probadd(allprob, qprob, new Problem(u, "Quick approve (approved " + rt.rat + " seconds after review); might want to re-review."));
    
    // --- emergency contact ---
    
    String em = u.getEmergencyContact();
    String op = null;
    boolean momordad = (em.toLowerCase().contains("mom") || em.toLowerCase().contains("dad"));
    
    if ((countMatches(em, "[a-zA-Z]") < optECMinLetters && !momordad) || countMatches(em, "[0-9]") < optECMinNumbers) {
        probadd(allprob, eprob, new Problem(u, "Emergency contact info seems incomplete (need name and number): '" + em + "'"));
    } else if ((op = onPlaya(em, usernamesForFuzzy, playanamesForFuzzy, optECFuzzyThreshold, optECPhonetic, ctx)) != null) {
        probadd(allprob, eprob, new Problem(u, "Emergency contact possibly camping with Disorient (must be off playa): '" + em + "', found a registered user named " + op + "."));
    }
    
    // --- does rv selection match living space selection ---
    
    RVSelection rv = r.getRvType();
    String ls = r.getLivingIn().replaceAll("[^a-zA-Z]", "");
    
    if (rv == RVSelection.NEED_CLARIFICATION) {
        probadd(allprob, rprob, new Problem(u, "RV selection needs clarification."));
    } else if (rv == RVSelection.NOT_STAYING_IN && "rv".equalsIgnoreCase(ls)) {
        probadd(allprob, rprob, new Problem(u, String.format("RV selection '%s' conflicts with living space selection '%s'.", rv.toString(), ls)));
    }
	
    // --- expects to park at camp; does not have vehicle pass or is not driving ---
    
    if (r.isParkAtCamp() && !r.isDriving())
        probadd(allprob, vprob, new Problem(u, "Possible mistake? Expects to park at camp but is not driving."));
    if (r.isParkAtCamp() && !r.isHaveVehiclePass())
        probadd(allprob, vprob, new Problem(u, "Expects to park at camp but does not have vehicle pass."));
    
    // --- cells ---
    
    //if (!u.isInMandatoryCells())
    //    prob.add(new Problem(u, "Missing one or more mandatory cells (possibly registered before mandatory cells added)"));
    if (u.getApprovedOn() != null && !u.isInEnoughCells())
        probadd(allprob, cprob, new Problem(u, "Approved but needs to sign up for more non-mandatory cells."));
     
    // --- disengage cells ---
    
    /*
    final long CELL_DMON = 429;
    final long CELL_DTUE = 430;
    final long CELL_DWED = 431;
    
    boolean dmon = u.isInCell(Cell.findById(CELL_DMON));    
    boolean dtue = u.isInCell(Cell.findById(CELL_DTUE));    
    boolean dwed = u.isInCell(Cell.findById(CELL_DWED));    
                
    if ((dtue && !dmon) || (dwed && !dtue))
        probadd(allprob, cprob, new Problem(u, "Seems to have skipped a day in disengage cell signups. Might want to ask them."));
    */
    
    // --- approval survey disengage ---

    if (a != null) {
        boolean anyd = a.isDisengageSun() || a.isDisengageMon() || a.isDisengageTue() || a.isDisengageWed();
        if (anyd && a.isDisengageNone())
            probadd(allprob, cprob, new Problem(u, "Approval survey: Said no disengage but also selected disengage days."));
        /*
        if (a.isDisengageMon() != dmon ||
            a.isDisengageTue() != dtue ||
            a.isDisengageWed() != dwed)
            probadd(allprob, cprob, new Problem(u, "Approval survey disengage days do not match disengage cell signups."));      
        */
    }
    
}

long tdelta = System.nanoTime() - tstart;
double msgen = tdelta / 1000000000.0;

String detailUrlFmt = Util.getAbsoluteUrl(request, "details.jsp") + "?u=%s";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<style type="text/css">
table.problems {
    border-collapse: collapse;
}
table.problems td {
    border: 1px solid #aaa;
    padding-left: 1ex;
    padding-right: 1ex;
    vertical-align: top;
}
table.problems td.info {
    white-space: nowrap;
}
table.problems td.userid {
    text-align: right;
}
table.problems td.section {
    text-align: center;
    font-weight: bold;
    background: #ddd;
}
table.problems td.header {
    font-weight: bold;
    background: #eee;
}
table.problems td.spacer {
    border: 0;
}
</style>
</head>
<body>

<% if (sep) { %>
<p><a href="check.jsp">Combine Problem Lists</a></p>
<% } else { %>
<p><a href="check.jsp?separate">Split Up Problem Lists</a></p>
<% } %>

<p>Check Time: <%= String.format("%.3f", msgen) %> seconds (please refresh this page sparingly)</p>
<p><b>Some problems below may be false positives, use judgment before emailing users.</b></p>

<table class="problems">
<% if (sep) { %>
<% writeProblems(out, qprob, "Quick Review / Approve", detailUrlFmt); %>
<% writeProblems(out, eprob, "Suspicious Emergency Contact", detailUrlFmt); %>
<% writeProblems(out, rprob, "RV Inconsistencies", detailUrlFmt); %>
<% writeProblems(out, vprob, "Vehicle Issues", detailUrlFmt); %>
<% writeProblems(out, cprob, "Cell Signup Issues", detailUrlFmt); %>
<% } else { %>
<% writeProblems(out, allprob, "Possible Problems", detailUrlFmt); %>
<% } %>
</table>

<table class="config">
<tr><td>Quick Review Threshold:<td><%= optQuickReviewTime %> seconds
<tr><td>Quick Approve Threshold:<td><%= optQuickApproveTime %> seconds
<tr><td>Emergency Contact Min. Letters:<td><%= optECMinLetters %> (excluding "mom" or "dad")
<tr><td>Emergency Contact Min. Numbers:<td><%= optECMinNumbers %>
<tr><td>Emergency Contact Match Methods:<td>Substring<%= optECFuzzyThreshold > 0 ? String.format(", Levenshtein (&lt; %d)", optECFuzzyThreshold) : "" %><%= optECPhonetic ? ", Phonetic (Names: NYSIIS / Caverphone / Daitch-Mokotoff, Playa Names: Refined Soundex)" : "" %>
</table>

</body>
</html>
