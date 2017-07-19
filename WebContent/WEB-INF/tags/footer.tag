<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="nomail" required="false" %>
<%@ attribute name="faqRedirect" required="false" %>
<%@ tag import="d13.ThisYear" %>
<%@ tag import="d13.dao.*" %>
<%@ tag import="d13.web.*" %>
<%
boolean frGlobal = RuntimeOptions.Global.isFAQRedirect();
boolean frPage = frGlobal && "true".equals(faqRedirect);
boolean isFAQ = request.getRequestURI().endsWith("/faq.jsp");
// note: we do the footer email link manually below instead of letting the frPage script
// handle it so that we don't have to use that script on pages that otherwise do not need
// it. frPage should be used only on camper-visible pages that may contain camp@ email
// links in the body.
%>
</div><!-- #dbody -->
<div id="dfooter">
<div style="border:0;margin:1ex;padding:0">
<hr>
<div style="float:left;border:0;margin:0;padding:0;width:40ex;" class="contact">
  <% if (isFAQ) { %>FAQ<% } else { %><a href="${pageContext.request.contextPath}/faq.jsp">FAQ</a><% } %> 
| <a href="http://wiki.disorient.info" target="_blank">Wiki</a> 
| <a href="https://www.facebook.com/groups/pornj" target="_blank">Facebook</a> 
</div>
<div style="float:right;border:0;margin:0;padding:0;text-align:right;width:40ex;<%= "true".equals(nomail) ? "visibility:hidden;" : "" %>" class="contact">
<% if (frGlobal) { %>
If you have any questions, <a href="${pageContext.request.contextPath}/faq.jsp?subject=Disorient <%=ThisYear.CAMP_YEAR %> Registration" target="_blank">send an email</a>.
<% } else { %>
If you have any questions, <a href="mailto:camp@disorient.info?subject=Disorient <%=ThisYear.CAMP_YEAR %> Registration">send an email</a>.
<% } %>
</div>
<div style="float:none;text-align:center;" class="version">
<%
    SessionData sess = new SessionData(session);
    User user = sess.isLoggedIn() ? sess.getUser() : null;
    boolean cl = (user != null && user.getRole().canViewUsers());
%>
<%= cl ? "<a href=\"changelog.txt\">" : "" %><%=ThisYear.CAMP_YEAR %>-<%=ThisYear.SYSTEM_VERSION %><%= cl ? "</a>" : "" %>
</div>
</div>
</div>
</div><!-- #container -->
<% if (frPage) { %>
<script type="text/javascript">
(function () {
    var links = document.querySelectorAll('a[href*="mailto:camp@disorient.info"]');
    for (var i = links.length; i --; ) {
        var e = links[i];
        var q = e.getAttribute('href');
        var n = q.indexOf('?');
        q = (n === -1 ? '?subject=Disorient <%=ThisYear.CAMP_YEAR %> Registration' : q.substring(n));
        e.setAttribute('href', '${pageContext.request.contextPath}/faq.jsp' + q);
        e.setAttribute('target', '_blank');
    }
})();
</script>
<% } %>