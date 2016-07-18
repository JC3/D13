<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (sess.getUser().isInviteCodeNeeded()) {
    response.sendRedirect("invite.jsp");
    return;
}

DataViewer view = new DataViewer(pageContext, sess, DataViewer.FLAG_SINGLE_USER | DataViewer.FLAG_NO_CELLS);
if (view.isFailed())
    return; // user should not be here
    
String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
String this_page = Util.getCompleteUrl(request);
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

// there will be only one row in result. cols and row.values will be the same size.
List<String> cols = view.getColumns();
DataViewer.Row row = view.getRows().get(0);

int profileBorder = view.getProfileBorderIndex();
int registrationBorder = view.getRegistrationBorderIndex();
int cellBorder = view.getCellBorderIndex();
int surveyBorder = view.getApprovalBorderIndex();

String next_html = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));

User editor = sess.getUser();
User editee = view.getSingleUser();
boolean canEdit = editee.isEditableBy2(editor);
boolean canReview = editee.isReviewableBy2(editor);
boolean canAdmit = editee.isApprovableBy2(editor);
boolean canFinalize = editee.isFinalizableBy(editor);
boolean canComment = editee.isCommentableBy(editor);
boolean needsReview = canReview && (editee.getState() == UserState.NEEDS_REVIEW);
boolean needsAdmit = canAdmit && (editee.getState() == UserState.REGISTERED);
boolean needsFinalize = canFinalize && (editee.getState() == UserState.APPROVE_PENDING || editee.getState() == UserState.REJECT_PENDING);

List<Note> notes = view.getNotes();
String noteTitle = null;

if (notes != null) {
    noteTitle = editor.getRole().canViewLogs() ? "Activity Logs" : "";
    if (editor.getRole().canViewComments()) {
        if (!noteTitle.isEmpty())
            noteTitle += " / ";
        noteTitle += "Comments";
    }
}

%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
table.form td.key { white-space: nowrap; font-weight: bold; color: #ff8000; vertical-align: top; border-top: 1px solid #202020; }
table.form td.value { vertical-align: top; border-top: 1px solid #202020; }
table.form td.link { font-size: smaller; color: #ff8080; text-align: center; vertical-align: top; border-top: 1px solid #202020; }
td.left { text-align: right; vertical-align: top; }
td.wide { text-align: center; vertical-align: top; }
table.form td.cell { border: 0; text-indent: 4ex; vertical-align: top; border-top: 1px solid #202020; }
table.form td.log { border: 0; text-indent: 2ex; vertical-align: top; font-size: smaller; }
table.form td.log2 { border: 0; padding-left: 4ex; vertical-align: top; font-size: smaller; }
table.form td.log-comment { color: #80ffff; }
table.form td.log-activity { color: #d4d4d4; }
table.form td.title { text-align: center; font-weight: bold; color: #ff8080; }
table.form td.status { text-align: center; color: #ff8000; }
.approved { color: #00ff00; font-weight: bold; }
.rejected { color: #ff0000; font-weight: bold; }
</style>
<script type="text/javascript">
function checkc () {
    var action = document.getElementById("action");
    if (action.options[action.selectedIndex].value == "") {
        alert("Please select 'Approve' or 'Reject'.");
        return false;
    }
    if (!document.getElementById("c1").checked ||
        !document.getElementById("c2").checked) {
        alert("You must read and check the checkboxes.");
        return false;
    }
    return true;
}
</script>
<title>Disorient</title>
</head>
<body>

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<% if (needsReview) { %>
<div class="notice"><p>This user has just signed up. Please review the form below and click "Looks Good!" at the bottom if
the application is in order. If there are issues, you may <a href="mailto:<%=Util.html(editee.getEmail())%>?subject=Your Disorient <%=ThisYear.CAMP_YEAR %> Registration">contact the user</a> to clear them up first. Until this application is
marked as "Reviewed", this user cannot be accepted or rejected!</p></div>
<% } else if (needsAdmit) { %>
<div class="notice"><p>This user's registration application is complete. Please review the form below then use the buttons
at the bottom to make an approval decision.</p></div>
<% } else if (needsFinalize) {%>
<div class="notice"><p>This user has been approved/rejected and that decision is pending. Please review the form below then use
the buttons at the bottom to confirm a final decision. For a user to be officially accepted or rejected, you <em>must</em> confirm
here.</div>
<% } %>

<table class="form">

<% for (int n = 0; n < cols.size(); ++ n) { %>
     <tr><td class="key"><%=Util.html(cols.get(n)) %><td class="value"><%=Util.html(row.values.get(n)) %>
<%   if (n == profileBorder && canEdit) { %>
        <tr><td class="link" colspan="2">^ <a href="personal.jsp?u=<%=editee.getUserId() %>&next=<%=next_html%>">Edit</a> this user's profile data. ^
<%   } else if (n == registrationBorder) { 
        if (canEdit) { %>
          <tr><td class="link" colspan="2">^ <a href="registration.jsp?u=<%=editee.getUserId() %>&next=<%=next_html%>">Edit</a> this user's registration data. ^
<%      }
        break; // hack, relying on order of sections, to put approval survey after cells. %>
<%   } %>
<% } %>

<tr><td class="key" colspan="2">Cell Assignments:
<% if (editee.isInCells()) { %>
    <% for (Cell cell:editee.getCells()) { %>
         <tr><td class="cell" colspan="2"><%= Util.html(cell.getFullName()) %>
    <% } %>
<% } else { %>
     <tr><td class="cell" colspan="2">This person has not volunteered for any cells!
<% } %>
<% if (canEdit) { %>
     <tr><td class="link" colspan="2">^ <a href="cells.jsp?u=<%=editee.getUserId() %>&next=<%=next_html%>">Edit</a> this user's cell assignments. ^
<% } %>

<% if (editee.getState() == UserState.APPROVED) { %>
<%   for (int n = registrationBorder + 1; n < cols.size(); ++ n) { // continuation of hack from above %>
       <tr><td class="key"><%=Util.html(cols.get(n)) %><td class="value"><%=Util.html(row.values.get(n)) %>
<%   } %>
<%   if (canEdit) { %>
<tr><td class="link" colspan="2">^ <a href="survey.jsp?u=<%=editee.getUserId() %>&next=<%=next_html%>">Edit</a> this user's approval survey. ^
<%   } %>
<% } %>

<% if (notes != null) { %>
<tr><td class="key" colspan="2"><a name="comments"></a><%= noteTitle %>:
  <% if (notes.isEmpty()) { %>
     <tr><td class="log" colspan="2">None.
  <% } else { 
       DefaultDataConverter ddc = new DefaultDataConverter();
       for (Note n:notes) { 
           String datestr = Util.html(ddc.asString(n.getTime()));
           String authstr = Util.html(n.getAuthorEmail());
           String textstr = Util.html(n.getText()).replace("\n", "<br/>");
           boolean comment = n.isComment(); %>
          <tr><td class="log" colspan="2"><%=datestr%>, <a href="mailto:<%=authstr%>"><%=authstr%></a>:
          <tr><td class="log2 <%= comment ? "log-comment" : "log-activity" %>" colspan="2"><%=textstr%>
       <% } 
     } %>
<% } %>

</table>

<% if (canComment) { %>
<form action="do_comment.jsp" method="post">
<input type="hidden" name="next" value="<%= this_page %>#comments">
<input type="hidden" name="subject" value="<%= editee.getUserId() %>">
<table class="form" style="margin-top: 1ex;">
<tr><td class="title">Post Comment
<tr><td class="wide" style="padding-top: 1ex;"><textarea style="width:100%" class="dtextarea" name="comment" placeholder="You cannot edit or delete comments, so think before posting. Comments are not visible to users."></textarea>
<tr><td class="wide" style="padding-top: 1ex;"><input type="submit" class="dbutton" value="Post">
</table>
</form>
<% } %>

<% if (needsReview) { %>
<% /*==========================================================================================================*/ %>
<form action="do_approval.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<input type="hidden" name="action" value="review">
<table class="form" style="margin-top: 1ex;">
<tr><td class="title">Application Review
<tr><td class="wide" style="padding-top: 1ex;"><input class="dbutton" type="submit" value="Looks good!">
</table>
</form>
<% /*==========================================================================================================*/ %>
<% } else if (canAdmit && !needsFinalize) { %>
<% /*==========================================================================================================*/ %>
<form action="do_approval.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<table class="form" style="margin-top: 1ex;">
<tr><td colspan="2" class="title">Approve / Reject User
<tr><td class="left"><input class="dcheckbox" type="checkbox" name="c1" id="c1" value="1"><td>I have thoroughly reviewed the above application and if I approve it, I believe that <%=Util.html(editee.getRealName()) %> is a DOer who will positively contribute to our camp.
<tr><td class="left"><input class="dcheckbox" type="checkbox" name="c2" id="c2" value="1"><td>I understand that I have been trusted with the responsibility to enforce our maximum camper limit so that we can provide <%=Util.html(editee.getRealName()) %> with food, water, power, and a place to live for a week.
<% if (!editee.isInMandatoryCells()) { %>
<tr><td colspan="2" style="text-align:center;"><span style="color: red;">Warning: This user has not signed up for all of the mandatory cells!</span>
<% } %>
<% if (!editee.isInEnoughCells()) { %>
<tr><td colspan="2" style="text-align:center;"><span style="color: red;">Warning: This user has not signed up for enough volunteer cells!</span>
<% } %>
<tr><td colspan="2" style="text-align:center;">Action: <select class="dselect" name="action" id="action">
    <option value="">--- Select Action ---</option>
    <option value="approve"<%= editee.getState() == UserState.APPROVE_PENDING ? " selected" : "" %>>Approve</option>
    <option value="reject"<%= editee.getState() == UserState.REJECT_PENDING ? " selected" : "" %>>Reject</option>
</select>
<tr><td class="wide" colspan="2"><input class="dbutton" type="submit" value="Apply" onclick="return checkc();">
</table>
</form>
<% /*==========================================================================================================*/ %>
<% } else if (canFinalize) { %>
<% /*==========================================================================================================*/ %>
<form action="do_approval.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<table class="form" style="margin-top: 1ex;">
<tr><td colspan="2" class="title">Confirm Approve / Reject User
<tr><td colspan="2" class="status">This user is: <%
boolean selectApprove = false, selectReject = false;
String labelApprove = "Confirm Approve", labelReject = "Confirm Reject";
switch (editee.getState()) {
case APPROVE_PENDING: 
    out.println("<span class=\"approved\">Pending Approval</span>");
    selectApprove = true;
    break;
case REJECT_PENDING: 
    out.println("<span class=\"rejected\">Pending Rejection</span>");
    selectReject = true;
    break;
case APPROVED: 
    out.println("<span class=\"approved\">Approved (Finalized)</span>");
    selectApprove = true;
    labelApprove = "Approved";
    labelReject = "Change to Rejected";
    break;
case REJECTED: 
    out.println("<span class=\"rejected\">Rejected (Finalized)</span>");
    selectReject = true;
    labelApprove = "Change to Approved";
    labelReject = "Rejected";
    break;
default: 
    break; // shouldn't happen
}
%>
<tr><td class="left"><input class="dcheckbox" type="checkbox" name="c1" id="c1" value="1"><td>I understand that the admissions team has made a decision regarding <%=editee.getRealName() %> based on their review, and I will try to honor that decision if it is best for Disorient, regardless of my personal feelings.
<tr><td class="left"><input class="dcheckbox" type="checkbox" name="c2" id="c2" value="1"><td>I understand that <%=editee.getRealName() %> will be notified immediately of this decision. While I can change the decision later, I accept all responsibility for any drama that may result.
<tr><td colspan="2" style="text-align:center;">Action: <select class="dselect" name="action" id="action">
    <option value="">--- Select Action ---</option>
    <option value="final_approve"<%= selectApprove ? " selected" : "" %>><%=labelApprove %></option>
    <option value="final_reject"<%= selectReject ? " selected" : "" %>><%=labelReject %></option>
</select>
<tr><td class="wide" colspan="2"><input class="dbutton" type="submit" value="Apply" onclick="return checkc();">
</table>
</form>
<% /*==========================================================================================================*/ %>
<% } %>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<dis:footer/>

</body>
</html>