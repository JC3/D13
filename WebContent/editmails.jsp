<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.notify.*" %>
<%@ page import="d13.util.*" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
static String makeFields (String[][] info, String what) {
    
    StringBuilder sb = new StringBuilder();
    
    sb.append("<div class=\"fields\">");
    for (int n = 0; n < info.length; ++ n) {
        sb.append(String.format("<div class=\"f-name\">{%s}</div>", Util.html(info[n][0])));
        if (info[n][0].equals("expires1") || info[n][0].equals("expires2"))
            sb.append(String.format("<div class=\"f-desc\"><a class=\"f-longdesc\" data-name=\"%s\" data-desc=\"%s\" href=\"#\">Click me...</a></div>",
                    info[n][0], info[n][1].replace("\"", "\\\"")));
        else
            sb.append(String.format("<div class=\"f-desc\">%s</div>", Util.html(info[n][1])));
    }
    sb.append("</div>");
    
    return sb.toString();
    
}
%>
<%
SessionData sess = new SessionData(session);

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditMailTemplates())
    return;

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String root = pageContext.getServletContext().getContextPath();

Email.Configuration cfg = Email.Configuration.fromDatabase(HibernateUtil.getCurrentSession());
String approve_subj = Util.html(cfg.getContentTitle(Email.RT_APPROVAL_CONTENT));
String approve_body = Util.html(cfg.getContentBody(Email.RT_APPROVAL_CONTENT));
String approve_fields = makeFields(ApprovalEmail.FIELD_INFO, "approve");
String reject_subj = Util.html(cfg.getContentTitle(Email.RT_REJECTION_CONTENT));
String reject_body = Util.html(cfg.getContentBody(Email.RT_REJECTION_CONTENT));
String reject_fields = makeFields(RejectionEmail.FIELD_INFO, "reject");
String invite_subj = Util.html(cfg.getContentTitle(Email.RT_INVITE_CONTENT));
String invite_body = Util.html(cfg.getContentBody(Email.RT_INVITE_CONTENT));
String invite_fields = makeFields(InviteEmail.FIELD_INFO, "invite");

//boolean initial_modified = (terms_title != null || terms_text != null);
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery"/>
<style type="text/css">
.m-section {
    padding: 1ex;
    text-align: center;
    font-size: larger;
    border-top: 1px solid rgb(48,48,48);
}
.m-actions {
    padding-bottom: 1ex;
    text-align: right;
}
.m-actions span {
    /*font-size: smaller;*/
    color: #777;
}
.m-instructions div {
    width: 100ex;
    padding: 0;
    margin: 0;
}
.m-subject {
    width: 1ex;
    white-space: nowrap;
}
.m-body-input textarea {
    width: calc(100% - 4px);
    height: 30ex;
}
.m-fields {
    vertical-align: top;
    padding-top: 3ex;
    width: 1ex;
    white-space: nowrap;
}
.fields {
    display: flex;
    flex-direction: column;
    margin-right: 1ex !important;
}

.fields, .fields div {
    margin: 0;
    padding: 0;
    white-space: nowrap;
}
.f-name {
    font-size: smaller;
    font-weight: bold;
    font-family: monospace;
}
.f-desc {
    font-size: smaller;
    color: #ccc;
    padding-left: 1ex !important;
    margin-bottom: 0.5ex !important;
}
</style>
<script type="text/javascript">
$(document).ready(function () {

    let modified = {
        approve: false,
        reject: false,
        invite: false
    };
    
    function enableButton (btn, enable) {
        if (enable)
            btn.prop('disabled', false).removeClass('disabled').attr('title', null);
        else
            btn.prop('disabled', true).addClass('disabled').attr('title', 'You must view the preview before saving.');
    }
    
    function updateUnload () {
    	console.log(JSON.stringify(modified));
        if (modified.approve || modified.reject || modified.invite)
            window.onbeforeunload = () => false;
        else
            window.onbeforeunload = null;       
    }
    
    $('.m-preview > div').toggle(false);

    $('.btn-save').each(function (_,b) {
        enableButton($(b), false);
    });
    
    $('.edit-input').keypress(function () {
        let what = $(this).data('email');
        enableButton($(`#\${what}-save`), false);
        modified[what] = true;
        updateUnload();
    });
    
    $('.f-longdesc').click(function () {
        alert(`{\${\$(this).data('name')}}\n\n\${\$(this).data('desc')}`);
        return false;
    });
    
    $('.btn-preview').click(function () {
        let what = $(this).data('email');
        $.post('${pageContext.request.contextPath}/ajax/render_markdown.jsp', { 
            markdown: $(`#\${what}-body`).val()
        }).then(function (r) {
        	$(`#\${what}-md-preview`).toggle(true).html(r);
        }).fail(function (e) {
            alert(`A server error occurred: \${e.status} \${e.statusText}`);            
        }).always(function () {
            enableButton($(`#\${what}-save`), true);
        });
        
    });
    
    $('.btn-save').click(function () {
        let what = $(this).data('email');
        let subj = $(`#\${what}-subject`).val();
        let body = $(`#\${what}-body`).val();
        $.post('${pageContext.request.contextPath}/ajax/edit_email.jsp', {
            what: what,
            subject: subj,
            body: body
        }, 'json').then(function (r) {
            if (r.success) {
                modified[what] = false;
                updateUnload();
                alert(`Template "\${what}\" saved OK!`);
            } else
                alert(`Could not save template "\${what}\": \${r.message}`);
        }).fail(function (e) {
            alert(`A server error occurred: \${e.status} \${e.statusText}`);
        });        
    });

});
</script>
</head>
<body>
<dis:header/>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<!-- <form action="do_editmails.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>"> -->
<table class="form">
<tr>
  <td colspan="3" class="m-instructions"><div><p>Here you can edit the email templates, which you must format as <a href="http://commonmark.org/help/">Markdown</a> 
        (if you don't like the preview here you can also test your skills live on <a href="http://stackedit.io">stackedit.io</a>, but
        note the formatting looks a bit different than it does here). Make your edits below and use the buttons under each section.
        Each section is its own thing so be sure to save all three separately if you edit all three. Use the back button to leave
        this page when you're done.</p>
        <p>Also, each template can contain template fields (inside { and }). Available fields for each email type are listed on the
        left. If you need new fields ask Jason. <b>Warning: Fields are not checked for typos, if you mess one up, then you'll send
        out a whole bunch of obviously busted looking emails. Be careful!</b></p></div>
<% if (RuntimeOptions.Global.isInviteOnly()) { %>
<!-- invite -->
<tr>
  <td colspan="3" class="m-section">Invitation
<tr>
  <td rowspan="4" class="m-fields"><%=invite_fields %>
  <td class="m-subject">Subject:
  <td class="m-subject-input"><input type="text" class="dtext edit-input" id="invite-subject" data-email="invite" value="<%=invite_subj %>">
<tr>
  <td colspan="2" class="m-body-input"><textarea id="invite-body" data-email="invite" class="dtextarea markdown edit-input"><%=invite_body %></textarea>
<tr>
  <td colspan="3" class="m-actions"><span>Invite Template:</span>
    <button id="invite-preview" class="dbutton btn-preview" data-email="invite">Preview</button>
    <button id="invite-save" class="dbutton btn-save" data-email="invite">Save</button>
<tr>
  <td colspan="3" class="m-preview"><div id="invite-md-preview"></div>
<% } %>
<!-- approve -->
<tr>
  <td colspan="3" class="m-section">Approval
<tr>
  <td rowspan="4" class="m-fields"><%=approve_fields %>
  <td class="m-subject">Subject:
  <td class="m-subject-input"><input type="text" class="dtext edit-input" id="approve-subject" data-email="approve" value="<%=approve_subj %>">
<tr>
  <td colspan="2" class="m-body-input"><textarea id="approve-body" data-email="approve" class="dtextarea markdown edit-input"><%=approve_body %></textarea>
<tr>
  <td colspan="3" class="m-actions"><span>Approval Template:</span>
    <button id="approve-preview" class="dbutton btn-preview" data-email="approve">Preview</button>
    <button id="approve-save" class="dbutton btn-save" data-email="approve">Save</button>
<tr>
  <td colspan="3" class="m-preview"><div id="approve-md-preview"></div>
<!-- reject -->
<tr>
  <td colspan="3" class="m-section">Rejection
<tr>
  <td rowspan="4" class="m-fields"><%=reject_fields %>
  <td class="m-subject">Subject:
  <td class="m-subject-input"><input type="text" class="dtext edit-input" id="reject-subject" data-email="reject" value="<%=reject_subj %>">
<tr>
  <td colspan="2" class="m-body-input"><textarea id="reject-body" data-email="reject" class="dtextarea markdown edit-input"><%=reject_body %></textarea>
<tr>
  <td colspan="3" class="m-actions"><span>Rejection Template:</span>
    <button id="reject-preview" class="dbutton btn-preview" data-email="reject">Preview</button>
    <button id="reject-save" class="dbutton btn-save" data-email="reject">Save</button>
<tr>
  <td colspan="3" class="m-preview"><div id="reject-md-preview"></div>
<!-- end -->
</table>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<dis:footer/>
</body>
</html>