<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="org.joda.time.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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

static String onPlaya (String s) {
    
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

void writeProblems (JspWriter out, List<Problem> problems, String name) throws java.io.IOException {

    out.println("<tr>");
    out.println("  <td class=\"section\" colspan=\"5\">" + Util.html(name));
    
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
	        out.println("  <td" + rowspan + " class=\"info\">" + p.who.getUserId());
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

List<User> users = User.findAll("realName");

List<Problem> allprob = new ArrayList<Problem>();
List<Problem> qprob = new ArrayList<Problem>();
List<Problem> eprob = new ArrayList<Problem>();
List<Problem> rprob = new ArrayList<Problem>();
List<Problem> vprob = new ArrayList<Problem>();
List<Problem> cprob = new ArrayList<Problem>();

for (User u : users) {
    
    if (!u.isRegistrationComplete() || u.getState() == UserState.REJECTED)
        continue;
    
    RegistrationForm r = u.getRegistration();
    ApprovalSurvey a = (u.isApprovalComplete() ? u.getApproval() : null);

    // --- reviewed too quickly ---
    
    ReviewTimes rt = calcReviewTimes(u);
    if (rt.rrt >= 0 && rt.rrt <= 180)
        probadd(allprob, qprob, new Problem(u, "Quick review (review completed " + rt.rrt + " seconds after registering); might want to re-review."));
    if (rt.rat >= 0 && rt.rat <= 45)
        probadd(allprob, qprob, new Problem(u, "Quick approve (approved " + rt.rat + " seconds after review); might want to re-review."));
    
    // --- emergency contact ---
    
    String em = u.getEmergencyContact();
    String op = null;
    boolean momordad = (em.toLowerCase().contains("mom") || em.toLowerCase().contains("dad"));
    
    if ((countMatches(em, "[a-zA-Z]") < 5 && !momordad) || countMatches(em, "[0-9]") < 10) {
        probadd(allprob, eprob, new Problem(u, "Emergency contact info seem incomplete (need name and number): '" + em + "'"));
    } else if ((op = onPlaya(em)) != null) {
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
    
    final long CELL_DMON = 429;
    final long CELL_DTUE = 430;
    final long CELL_DWED = 431;
    
    boolean dmon = u.isInCell(Cell.findById(CELL_DMON));    
    boolean dtue = u.isInCell(Cell.findById(CELL_DTUE));    
    boolean dwed = u.isInCell(Cell.findById(CELL_DWED));    
                
    if ((dtue && !dmon) || (dwed && !dtue))
        probadd(allprob, cprob, new Problem(u, "Seems to have skipped a day in disengage cell signups. Might want to ask them."));
    
    // --- approval survey disengage ---

    if (a != null) {
        boolean anyd = a.isDisengageSun() || a.isDisengageMon() || a.isDisengageTue() || a.isDisengageWed();
        if (anyd && a.isDisengageNone())
            probadd(allprob, cprob, new Problem(u, "Approval survey: Said no disengage but also selected disengage days."));
        if (a.isDisengageMon() != dmon ||
            a.isDisengageTue() != dtue ||
            a.isDisengageWed() != dwed)
            probadd(allprob, cprob, new Problem(u, "Approval survey disengage days do not match disengage cell signups."));       
    }
    
}

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

<table class="problems">
<% if (sep) { %>
<% writeProblems(out, qprob, "Quick Review / Approve"); %>
<% writeProblems(out, eprob, "Suspicious Emergency Contact"); %>
<% writeProblems(out, rprob, "RV Inconsistencies"); %>
<% writeProblems(out, vprob, "Vehicle Issues"); %>
<% writeProblems(out, cprob, "Cell Signup Issues"); %>
<% } else { %>
<% writeProblems(out, allprob, "Possible Problems"); %>
<% } %>
</table>

</body>
</html>
