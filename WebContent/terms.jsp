<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.web.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
boolean loggedin = sess.isLoggedIn();

//2014-07-10 changed to allow anybody to view
/*if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must log in.");
    response.sendRedirect("index.jsp");
    return;
}*/

boolean cont = "1".equals(request.getParameter("continue"));
boolean accept = "1".equals(request.getParameter("accept"));

if (!loggedin) { // 2014-07-10 added to allow anybody to view
    cont = false;
    accept = false;
}

if (cont) {
    if (accept) {
        sess.getUser().setTermsAgreed(true);
        response.sendRedirect("home.jsp");
    } else {
        response.sendRedirect("sorry.jsp");
    }
    return;
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
h1 {
    font-size: larger;
    font-weight: bold;
    padding-top: 1em;
}
.termstext {
    /*max-width: 900px;*/
    margin-left: auto;
    margin-right: auto;
    max-width: 100ex;
}
</style>
</head>
<body>
<dis:header/>
<% if (loggedin /* 2014-07-10 added to allow anybody to view */ && sess.getUser().isTermsAgreed()) { %>
<div class="nav"><a href="home.jsp">Home</a></div>
<% } %>

<div class="termstext">

<h1 style="padding-top:0">D16ORIENT Registration Guidelines</h1>

<% if (loggedin) { %>
<div><strong>Welcome to Disorient! Before you register, please read the following information!</strong></div>
<% } %>

<!-- BEGIN -->
<div>

<p>We are about to set forth on a magical journey Home to Black Rock City. Together, we will create the perfect union of art, elbow grease, and love that resonates with you personally and accomplishes our collective Disorient camp goals. This is an incredibly important year for us, as we are going TIGHTER and BRIGHTER.</p>

<h1>Camp Registration</h1>

<ul>
<li>Applications for camp must go through the formal approval process.
<li>If any clarification is needed regarding your application, you will be contacted. Please keep an eye out for emails and respond promptly to keep things running smoothly.
<li>Campers may arrive at Disorient no later than Tue to check-in at camp headquarters and be placed.
<li>All campers are expected to help with general camp build and break down tasks near the beginning and end of the week.
<li>Campers should be prepared to participate in MANDATORY camp-wide Disengage on Saturday Sept. 3rd. Campers should plan to leave Disorient no earlier than sunset on Sunday, in order to contribute a full day of Disengage.
</ul> 

<p>Please adhere to registration deadlines as our smooth planning depends on timely and accurate delivery of information and payment of camp dues.</p>

<p><strong>IMPORTANT NOTICE FOR RVs:</strong> In order to meet service request deadlines with United, you must <strong>register your RV before August 16th!</strong> RVs that are not both registered and paid for by this date will not be eligible for water service.</p>

<a name="dues"></a>
<h1>Camp Dues</h1>

<p>Camp dues help us achieve our goals and go a long way towards covering the expenses for our art, sound, materials, electricity, water, and some food. Our camp dues cover only a portion of the cost of maintaining Disorient - the rest we generate from fundraising events and donations.</p>
<p>You may have noticed we are keeping most dues the same as last year - despite some pretty big increases in several vendor charges for 2016. Keeping the dues mostly unchanged takes the pressure off many, while allowing individuals who have both the means and inclination to donate above the requested dues levels. If you have the capacity to buy at a higher tier level for dues or RV fee, PLEASE do so. To make a larger or tax-free contribution, <a href="https://www.fracturedatlas.org/site/fiscal/profile?id=4611">visit our site on Fractured Atlas</a>.</p>

<p>*Disorient has a tight budget this year and all help will be greatly appreciated by camp.*</p>

<p>Our camp dues in 2016 are:</p>

<ul>
<li><strong>Tier 1:</strong> $395 if paid by 11:59 PM (Eastern Time) on July 15th
<li><strong>Tier 2:</strong> $545 if paid by 11:59 PM (Eastern Time) on July 25th
<li><strong>Tier 3:</strong> $665 if paid by 11:59 PM (Eastern Time) on August 16th
<li><strong>ANY CAMPER WITH UNPAID DUES AFTER AUGUST 16TH WILL NOT BE PLACED.</strong>
<li><strong>RV campers:</strong> The RV fee is in addition to your camp fee. You will be provided connection to the generator grid, as the use of onboard generators is prohibited due to the exhaust produced at tent-level. RV fee also covers the ongoing week-long servicing such as potable water replenishments and gray/black water removal to ensure things stay clean and fresh throughout the duration of Burning Man. <em>PLEASE NOTE: There are a <strong>limited amount</strong> of slots for RVs and RV slots will be given on a first come first serve basis!</em>
  <ul>
  <li><strong>RV Fee Tier 1:</strong> $1050 if paid by 11:59 PM (Eastern Time) on July 15th
  <li><strong>RV Fee Tier 2:</strong> $1250 if paid by 11:59 PM (Eastern Time) on July 25th
  <li><strong>RV Fee Tier 3:</strong> $1550 if paid by 11:59 PM (Eastern Time) on August 16th
  <li><strong>IMPORTANT: Due to United service policy changes, RVs that are not registered and paid for by August 16th WILL NOT BE ELIGIBLE FOR WATER SERVICE.</strong>
  <li><strong>ANY RV WITH UNPAID FEES AFTER AUGUST 16TH WILL NOT BE PLACED AND WILL NOT RECEIVE SERVICE.</strong>
  </ul>
<li>You will have a grace period of <strong><%= ThisYear.GRACE_PERIOD_DAYS %> days</strong> after you are approved to pay the tier that was active <strong>at the time you originally completed the registration form</strong>! After that period is over, your tier will be determined by the date of your payment.
</ul>

<p>Campers have the opportunity to earn back a rebate for each day of Disengage worked at the end of the event, between Monday and Wednesday. Rebate breakdown is as follows:</p>

<ul>
<li>Monday: $40
<li>Tuesday: $80
<li>Wednesday $120
</ul>

<p><strong>That's $240 of your camp dues that you can earn back by helping Disengage.</strong> Please select your availability in registration and contact <a href="mailto:camp@disorient.info?subject=D16ORIENT">camp@disorient.info</a> for more details.</p>

<h1>Camp Preparation</h1>

<p>The Ten Principles and Burning Man Survival Guide are mandatory reading for EVERY CAMPER!</p>

<p>Making sure our Disorientingly high level of awesome continues depends upon how well veteran Disorienters empower virgins and newbies to maintain our high pornj ideals. We have zero tolerance for certain substances in camp while expecting new and veteran campers to have substance. It can be disorienting to even seasoned Burners. Let's work hard to educate and set up new members to excel.</p>

<p>New or seasoned pornj campers will find there are lots of paths to information about camp such as the <a href="http://wiki.disorient.info">Disorient Wiki</a>, which has camp plans from previous years archived, and projects close to Disorient's heart available to investigate. There, you can learn what to expect at camp and what is expected of you. Every Disorient camper should jump in and join 
<a href="https://www.facebook.com/groups/d16orient">the Facebook group</a> to make an introduction and to generally get involved. You can read up on projects you may be interested in participating and you will find important information that you are responsible for knowing so please be alert to activity and stay involved.</p>

<ul>
<li>The Facebook group: <a href="https://www.facebook.com/groups/d16orient">https://www.facebook.com/groups/d16orient</a>
<li>Wiki: <a href="http://wiki.disorient.info">http://wiki.disorient.info</a>
<li>Email: <a href="mailto:camp@disorient.info?subject=D16ORIENT">camp@disorient.info</a>
</ul>

<h1>Camp Participation</h1>

<ul>
<li>There are no tourists at D16orient! All are expected to take an active role in everyone's Playa experience. So, help out anywhere you can and find a project you like. Be proactive about your passions. Specific tasks will be delegated by the Volunteer Coordinator once on Playa. There will be an opportunity to fine-tune your participation once you are approved and individual project details and needs are assessed.
<li><strong>CANTEEN:</strong> In a move towards more radical self reliance, we have returned to a smaller and more contained kitchen area, known as the Disorient Canteen. Camp dinners will be organized by groups of Doers, and offerings during the day will be limited. We are seeking 3-4 Dinner leads and Grill Masters to spearhead sustenance. Please email <a href="mailto:camp@disorient.info?subject=D16ORIENT">camp@disorient.info</a> if you are called to culinary excellence. 
<li><strong>PLEASE NOTE</strong> that your placement on Playa is based upon factors such as footprint of your tent and arrival/departure times. If you have plans to camp with specific friends please indicate so when describing your personal camp footprint and electrical needs in the registration application.
<li>The camp design has a strict camping layout. This means <strong>no over-the-top personal lounge areas</strong>, no crazy-over-the top tents that take up too much space and a limit on the amount of RVs and RV space. Please plan accordingly. Oh, and, don't argue with the placement volunteers, they work very hard to make sure that we all fit in our camp.
<li>CLEAN AND GREEN IS A PRIORITY IN 2016!! This means YOU! Ongoing MOOP sweeps will be conducted throughout each day. Please be seen MOOPing each day. MOOP is everyone's problem so let's set an example for our fellow burners by being diligent and proactive to ensure Disorient is GREEN with LNT perfection!
<li>So that we Leave No Trace, <em>everybody</em> is expected to help with at least one full day of Disengage - the breaking down of camp structures, packing out everything, and MOOPing (removing all Matter Out Of Place).
<li>General Disengage begins around Sunday at noon, so a full day of Disengage means <strong>you can't depart Disorient until <em>after</em> sunset that evening!</strong>
<li>If possible, please stay two, three, or four days for Disengage. As a thank-you, you'll get a rebate back on your dues for each day. $40 back for Monday, $80 for Tuesday, and $120 for Wednesday. Another bonus: The hot springs open on Tuesday!
<li><strong>All campers, art cars, and RV's camping with us will be required to take away bags of camp trash/recycling.</strong>
<li>When checking out, bags of trash will be assigned and distributed to RVs, trucks, and cars to take with them at the end of the week for proper sorting and disposal. Everybody is expected to maintain the principle of LNT <em>even after departing BRC.</em>
<li><strong>Virgin pro-tip:</strong> You take out all trash that you create; so plan ahead and bring things that create little waste! Common gotchas include packaging of snacks, toiletries, wrappers on last-minute items from your Walgreen's panic run in Reno, etc. Remove as much packaging as possible before you arrive!
<li>Also, a little reminder: Bring a color copy of your ID with you in case you need it on Playa. Never bring anyone back to camp in order to get your ID!
</ul>

<p>If you have questions, suggestions, ideas or concerns please contact <a href="mailto:camp@disorient.info?subject=D16ORIENT">camp@disorient.info</a>.</p>

</div>
<!-- END -->

</div>

<% if (loggedin /* added to support anybody viewing */ && !sess.getUser().isTermsAgreed()) { %>
<form action="terms.jsp" method="get">
<div style="margin-top:2ex;text-align:center;" class="content">
<br>
<input type="hidden" name="continue" value="1">
<input class="dcheckbox" type="checkbox" name="accept" value="1">I have read and understood the rules above!<br><br>
<input class="dbutton" type="submit" value="Continue">
</div>
</form>
<% } else { %>
    <% if (loggedin) { %>
    <div class="nav"><a href="home.jsp">Home</a></div>
    <% } else { %>
    <div class="nav"><a href="index.jsp">Register Now!</a></div>
    <% } %>
<% } %>

<dis:footer/>

</body>
</html>
