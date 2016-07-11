<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag import="d13.ThisYear" %>
<%@ tag import="d13.dao.*" %>
<%@ tag import="d13.web.*" %>
</div><!-- #dbody -->
<div id="dfooter">

<div style="border:0;margin:1ex;padding:0">
<hr>

<div style="float:left;border:0;margin:0;padding:0;width:40ex;" class="contact">
<a href="http://wiki.disorient.info" target="_blank">Wiki</a> <!-- | <a href="http://puddle.disorient.info" target="_blank">Puddle</a>--> | <a href="https://www.facebook.com/groups/pornj" target="_blank">Facebook</a>
</div>

<div style="float:right;border:0;margin:0;padding:0;text-align:right;width:40ex;" class="contact">
If you have any questions, <a href="mailto:camp@disorient.info?subject=Disorient <%=ThisYear.CAMP_YEAR %> Registration">send an email</a>.
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
