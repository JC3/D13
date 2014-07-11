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
<style type="text/css">
h1 {
    font-size: larger;
    font-weight: bold;
}
</style>
</head>
<body>
<dis:header/>
<% if (loggedin /* 2014-07-10 added to allow anybody to view */ && sess.getUser().isTermsAgreed()) { %>
<div class="nav"><a href="home.jsp">Home</a></div>
<% } %>

<h1>Disorient Registration Rules</h1>

<% if (loggedin) { %>
<div><strong>Welcome to Disorient! Before you register, please read the following information about our camp!</strong></div>
<% } %>

<!-- BEGIN -->
<div>
  <p>Disorient, you are about to set forth on a magical journey, you are
  going Home and there is a metric ton of fun to be had planning for our survival in
  Black Rock City and beyond!! Virgins and veterans are being called into pornj action
  and together we are going to, from dust, create the perfect union of art, elbow grease
  and love, leaving behind nothing but dust and taking with us all the tools necessary to
  shine in any imaginable scenario.</p>

  <p>In preparation for this journey there are a series of steps you
  will be taken through to ensure you are ready, registered and that you have been
  aligned with a playa purpose that resonates with you personally and works to accomplish
  our collective Disorient Camp Goals. This is an incredibly important year for
  Disorient, as we are going TIGHTER and BRIGHTER. Now we demonstrate our highest ideals
  through our collaboration in constructing this temporary metropolis of our own and as
  we help others learn to live life in Pornj.</p>

<% if (loggedin) { %>
  <p>Welcome D14 Applicants!!</p>
<% } %>

  <h1>Camp Registration</h1>

  <ul>
    <li>
      <strong>Camp space is limited this year.</strong> Don't
      be left out in the dust! Register early for a better chance to camp with
      Disorient.
      
    <li>
      Registration is an application process that must go through a formal
      approval process.

    <li>
      Priority placement is given to senior veterans of Disorient who have made
      important contributions in the past, campers leading major projects,
      Disorient's Alpha Team, which arrives before Burning Man officially opens for
      set up and to campers staying extra days for Disengage as part of our Omega
      Team.

    <li>
      If you have not camped with Disorient before then you must be sponsored
      by a veteran of Disorient who takes personal responsibility for your behavior and
      contribution. Please note that sponsorship does not guarantee approval.

    <li>
      Campers may arrive at Disorient no later than Tue to check-in at camp
      headquarters and be placed.

    <li>
      Campers should plan to leave Disorient no earlier than sunset on Sun., in
      order to put in a full day of Disengage. <em>** Infrequent
      exceptions may apply under unusual circumstances for those making valuable
      contributions in other ways.</em>

  </ul>

  <p>Please adhere to registration deadlines as our smooth planning
  depends on the your timely accurate delivery of information and payment of camp
  dues.</p>

  <p><strong>IMPORTANT NOTICE FOR R.V.'S:</strong> In order to meet service request
  deadlines with United, we ask that you <strong>register your R.V. before July 24th</strong>! R.V.s
  that are not both registered and paid for by this date may not be eligible for water service! We
  are trying our best to work this out and will update as information becomes available, BUT PLEASE
  REGISTER YOUR R.V. AND PAY YOUR R.V. FEES AS SOON AS POSSIBLE to avoid this!</p> 
  <!-- <strong>the deadline for registering an R.V. is July 24!</strong>
  NO additional R.V.s will be admitted after this date, and there are NO exceptions! -->

  <a name="dues"></a>
  <h1>Camp Dues</h1>
  
  <p>Camp dues help us achieve our lofty goals on budget and include your meals, water, art,
  communications, electricity and materials. Your camp dues cover only a portion of the
  cost of building, storing, and maintaining Disorient - the rest we generate from
  fundraising events, individual support, and donations. Our camp dues in <%=ThisYear.CAMP_YEAR %>
  are:</p>

  <ul>
    <li><strong>Tier 1:</strong> $425 if paid by 11:59 PM (Eastern Time) on July 14th
    <li><strong>Tier 2:</strong> $475 if paid by 11:59 PM (Eastern Time) on July 24th
    <li><strong>Tier 3:</strong> $525 if paid by 11:59 PM (Eastern Time) on August 4th
    <li><strong>Tier 4:</strong> $575 if paid by 11:59 PM (Eastern Time) on August 12th
    <li><strong>Tier 5:</strong> $625 if paid by 11:59 PM (Eastern Time) on August 19th
    <li><strong>ANY CAMPER WITH UNPAID DUES AFTER AUGUST 19TH WILL NOT BE PLACED.</strong><br>&nbsp;
    <li><strong>RV campers:</strong> RV fee is in addition to your camp fee. You will be provided connection to our biodiesel generator grid, as the
    use of onboard generators is prohibited due to the exhaust produced at tent-level.
    RV fee also covers the ongoing week-long servicing such as potable H2O
    replenishments, grey water removal and black water removal to ensure things stay
    clean and fresh throughout the duration of Burning Man. <em>PLEASE NOTE: There are a
    <strong>limited amount</strong> of slots for RVs and RV slots will be given on a first come first
    serve basis, so register early!</em>
    <ul>
	    <li><strong>RV Fee Tier 1:</strong> $850 if paid by 11:59 PM (Eastern Time) on July 17th
	    <li><strong>RV Fee Tier 2:</strong> $900 if paid by 11:59 PM (Eastern Time) on July 24th
	    <li><strong>RV Fee Tier 3:</strong> $950 if paid by 11:59 PM (Eastern Time) on August 10th
	    <li><strong>IMPORTANT: Due to United service policy changes, R.V.s that are not registered and paid for by
	                July 24th MAY NOT BE ELIGIBLE FOR WATER SERVICE. We are trying our best to work this out, and 
	                will update you with information as it becomes available.</strong>
	    <li><strong>ANY RV WITH UNPAID FEES AFTER AUGUST 10TH WILL NOT BE PLACED AND WILL NOT RECEIVE SERVICE.</strong>
    </ul><br>&nbsp;
    <li>You will have a grace period of <strong>7 days</strong> after you are approved to pay the tier that
    was active <strong>at the time you originally registered</strong>! After that period is over, your tier
    will be determined by the date of your payment.
  </ul>

  <p>Campers have the opportunity to earn back a rebate for each day
  of Disengage worked <strong>past 8pm</strong> between Sunday
  and Wednesday.</p>

  <p>Rebate breakdown is as follows:</p>

  <ul>
    <li>Sunday: $50
    <li>Monday: $50
    <li>Tuesday: $75
    <li>Wednesday $75
  </ul>

  <p><strong>That's $250 of your camp dues that you can earn
  back, just by helping us Disengage!</strong> Please select your availability in
  registration and contact camp [at] disorient [dot] info for more details.</p>

  <p>Dues are tiered not only to encourage earlier registration and
  payment but also to accommodate the needs of our wide variety of campers. Should
  you be in a position to pay at a higher tier level you are encouraged to do so.
  We are making investments in our Disorient infrastructure this year that will
  make future years more economical and streamlined so please give with your hearts.
  </p>

  <h1>Camp Preparation</h1>

  <p>The Ten Principals and Burning Man Survival Guide are mandatory
  reading for EVERY CAMPER!</p>

  <p>Making sure our Disorientingly high level of awesome continues
  depends upon how well we empower our virgins and newbies to strive with us to maintain
  our high pornj ideals. We have a zero tolerance for certain substances in our
  camp while expecting our campers to have substance. It can be disorienting to
  even seasoned Burners. Let's educate and prepare our newest members and set them
  up to excel.</p>

  <p>New or seasoned pornj campers will find there are lots of paths
  to information on Disorient such as our Disorient <a href="http://wiki.disorient.info">Wiki</a>,
  which will have camp plans from
  previous years archived, and projects close to Disorients heart available to
  investigate. You will find what to expect when camping with us and learn what is
  expected of you. Everyone camping with Disorient should jump in and join
  <a href="http://puddle.disorient.info">The Puddle</a> to make an introduction in
  <a class="c7" href="http://puddle.disorient.info/forum/viewforum.php?f=14">Welcome to
  Disorient!</a>, and to generally get involved.
  You can read up on projects you may be interested in participating and you will
  find important information given to campers that you are responsible for knowing so
  please be alert to activity and be involved.</p>

  <ul>
  <li>The Puddle: <a href="http://puddle.disorient.info">http://puddle.disorient.info</a>
  <li>Wiki: <a href="http://wiki.disorient.info">http://wiki.disorient.info</a>
  <li>Email: <a href="mailto:camp@disorient.info?subject=Disorient <%=ThisYear.CAMP_YEAR %> Registration">camp [AT] disorient [DOT] info</a>
  </ul>

  <h1>Camp Participation</h1>

  <ul>
    <li>All Disorient Campers get to select their work on a
    particular camp project when registering and are expected to take an active role in
    everyone's playa experience. So, help out anywhere you can and find a
    project you like. There will be an opportunity to fine-tune your participation
    once you are approved and individual project details and needs are assessed.
    
    <li><strong>PLEASE NOTE</strong> that your
    placement on Playa is based upon
    factors such as footprint of your tent and arrival/departure times. If you have plans
    to camp with specific campers please indicate so when describing your personal camp
    footprint and electrical needs in the Approved Camper Survey.

    <li>Disorient has a camp design with strict camping layout.
    This means <strong>no over-the-top personal lounge areas</strong>, no crazy-over-the top tents
    that take up too much space and a limit on the amount of RVs and RV space.
    Please plan accordingly. NO EXCEPTIONS WILL BE MADE. Oh, and, don't argue
    with the placers, they work very hard to make sure that you all fit in our camp that
    we have made just for YOU.

    <li>Ongoing MOOP sweeps will be conducted throughout each day.
    Please be seen MOOPing each day. Glitter is everyone's problem so
    let's be diligent and pro-active on ensuring Disorient is GREEN with LNT
    perfection!
    
    <li>So that we Leave No Trace, every camper is expected to
    help with at least one full day of Disengage - the breaking down of camp structures,
    packing out everything, and MOOPing (removing all Matter Out Of Place).

    <li>General Disengage begins around Sun. at noon, so a full
    day of Disengage means you can't depart Disorient until <em>after</em>
    sunset that evening!

    <li>If possible, please stay two, three, or four days for
    Disengage. As a thank-you, you'll get a rebate back on your dues for each day!
    $50 back for Sunday and Monday and $75 for Tuesday and Wednesday! Thats a possible
    $250! Another bonus: the hot springs open on Tue!

    <li><strong>All campers/art cars and RV's camping with Disorient
    will be required to take away bags of camp trash/recycling.</strong>

    <li>When checking out, bags of trash will be assigned and
    distributed to RVs, trucks, and cars to take with them at the end of the week for
    proper sorting and disposal. Disorient expects you to maintain the principle of
    LNT even after departing BRC. Make us proud!

    <li><strong>Virgin pro-tip:</strong> You take out all trash that you create; so plan
    ahead and bring things that create little waste! Common gotchas include packaging of
    snacks, toiletries, wrappers on last-minute items from your Walgreen's panic run
    in Reno, etc. Remove as much packaging as possible before you arrive!

    <li>Also, a little reminder: Bring a color copy of your ID
    with you in case you need it on Playa. No need to bring anyone back to your camp in
    order to get your ID.
    
  </ul>

  <p>If you have ?s, suggestions, ideas or concerns please contact us
  at camp [AT] disorient [DOT] info - and let's talk! ***

  <p><strong>Disorient loves you!!</strong></p>
</div>
<!-- END -->

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
