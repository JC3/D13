<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must log in.");
    response.sendRedirect("index.jsp");
    return;
}

boolean cont = "1".equals(request.getParameter("continue"));
boolean accept = "1".equals(request.getParameter("accept"));

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
</head>
<body>

<h1>Disorient Registration Rules</h1>

<div><strong>Welcome to Disorient! Before you register, please read the following information about our camp!</strong></div>

<div>

Disorient, you are about to set forth on a magical journey, you are going Home and there is a metric ton of fun to be had planning for our survival in Black Rock City and beyond!! This year is a monumental one for Burning Man and all eyes are on us to lead the way. Virgins and veterans are being called into pornj action and together we are going to, from dust, create the perfect union of art, elbow grease and community, leaving behind nothing but dust and taking with us all the tools necessary to shine in any imaginable scenario.   
<p>In preparation for this journey there are a series of steps you will be taken through to ensure you are ready, registered and that you have been aligned with a playa purpose that resonates with you personally and works to accomplish our collective Disorient Camp Goals. This is an incredibly important year for Disorient. Now we demonstrate our highest ideals through our collaboration in constructing this temporary metropolis of our own and as we help others learn to live life in Pornj. <br> <br>

<strong>Welcome D13 Applicants!!</strong><br>
<br>
1.  Camp Registration<br>
<ul>
<li>Every individual who would like to camp with Disorient must complete registration.</li>
<li>Registration is the application process to be approved to camp with Disorient.</li>
<li>Priority placement is given to senior veterans of Disorient who have made important contributions in the past, campers leading major projects, Disorient's Alpha Team, which arrives before Burning Man officially opens for set up and to campers staying extra days for Disengage as part of our Omega Team.</li> 
<li>Every potential Disorient camper, who has not camped with Disorient before, must be sponsored by a veteran of Disorient who takes personal responsibility for your behavior and contribution. Please note that sponsorship does not guarantee approval.</li> 
<li>Campers may generally arrive at Disorient no later than Tue to check-in at camp headquarters and be placed.</li> 
<li>Campers should plan to leave Disorient no earlier than sunset on Sun., in order to put in a full day of Disengage. <em>**Infrequent exceptions may apply under unusual circumstances for those making valuable contributions in other ways.</em></li>
</ul>
All registration applications must be submitted by June 15, 2013.  Any applications received after this date will not be approved. <br>
<br><strong>Camp Dues</strong><br>
<br>Camp dues help us achieve our lofty goals on budget and include your meals, water, Porto Potty access, art, communications, electricity and materials. Your camp dues cover only a portion of the cost of building, storing, and maintaining Disorient - the rest we generate from fundraising events, individual support, and donations. Our camp dues in 2013 are:
<ul>
<li>tier one - $275 per camper if paid by July 22</li>
<li>tier two - $325 if paid by July 29</li>
<li>tier three - $375 if paid by August 6</li>
<li>overdue - $425 after August 6</li>
<li>RV campers: -Fee per RV is $725.  You will be provided connection to our biodiesel generator grid, as the use of onboard generators is prohibited due to the exhaust produced at tent-level.  RV fee also covers the ongoing weeklong servicing such as potable H2O replenishments, grey water removal and black water removal to ensure things stay clean and fresh throughout the duration of Burning Man.</li> 
</ul>
Campers have the opportunity to earn back a $50 rebate for each day of Disengage worked past 8pm between Sunday and Wednesday.  Please select your availability in registration and contact Diana or Friar Tuck for more details.<br><br>  
Dues, are tiered not only to encourage earlier registration and payment but also to accommodate the needs of our wide variety of campers.  Should you be in a position to pay at a higher tier level you are encouraged to do so.  We are making investments in our Disorient infrastructure this year that will make future years more economical and streamlined so please give with your hearts.<br><br> 
2. Camp Preparation<br><br>
The Ten Principals and Burning Man Survival Guide are mandatory reading for EVERY CAMPER!<br><br>
Making sure our Disorientingly high level of awesome continues depends upon how well we empower our virgins and newbies to strive with us to maintain our high pornj ideals.  We have a zero tolerance for certain substances in our camp while expecting our campers to have substance.  It can be disorienting to even seasoned Burners. Let's educate and prepare our newest members and set them up to excel.<br><br> 
New or seasoned pornj campers will find there are lots of paths to information on Disorient such as our Disorient <a href="http://disorient.com/">Wiki</a>, which will have camp plans from previous years archived, and projects close to Disorients heart available to investigate.  You will find what to expect when camping with us and learn what is expected of you.  Everyone camping with Disorient should jump in and join <a href="http://disorient.tv/forum">The Puddle</a> to make an introduction in <a href="http://disorient.tv/forum/viewforum.php?f=14">Welcome to Disorient!</a> - and generally get involved.  You can read up on projects you may be interested in participating and you will find important information given to campers that you are responsible for knowing so please be alert to activity and be involved. The magic Disorient brings to BRC is entirely a team effort so get involved months not weeks before the Playa~ <br>
The Puddle: http://disorient.tv/forum <br>
Wiki: http://disorient.com <br>
Email: camp{AT}disorient{DOT}info <br>
<br><strong>Camp Participation</strong><br>
<ul>
<li>All Disorient Campers get to select their work on a particular camp project when registering.  There will be an opportunity to fine-tune your participation once you are approved and individual project details and needs are assessed.</li>
<li>This year, all campers will be assigned to a node after they have been accepted as campers.  These nodes will have shared responsibilities, including the service of one meal (including décor or costuming and help with set up and clean-up).  This does not constitute or replace work cell participation. 
**Note that your placement on Playa is NOT based on your node, but based upon other factors such as footprint of your tent and arrival/departure times. If you have plans to camp with specific campers please indicate so when describing your personal camp footprint and electrical needs.</li>  
<li>Ongoing MOOP sweeps will be conducted throughout each day.  Please be seen MOOPing each day.  Glitter is everyone's problem so let's be diligent and pro-active in ensuring Disorient is GREEN with LNT perfection!</li>  
<li>So that we Leave No Trace, every camper is expected to help with at least one full day of Disengage - the breaking down of camp structures, packing out everything, and MOOPing (removing all Matter Out Of Place).</li>
<li>General Disengage begins around Sun. at noon, so a full day of Disengage means you can't depart Disorient until after sunset that evening</li>
<li>If possible, please stay two, three, or four days for Disengage. As a thank-you, those who stay for extra days of disengage will receive a partial reimbursement on their dues.  Another bonus: the hot springs open on Tue!</li>
<li>All campers/art cars and RV's camping with Disorient will be required to take away bags of camp trash/recycling</li> 
<li>When checking out bags of trash will be assigned and distributed to RVs, trucks, and cars to take with them at the end of the week for proper sorting and disposal.  Disorient expects you to maintain the principle of LNT even after departing BRC.  Make us proud!</li>  
<li>And a little reminder to bring a color copy of your ID with you in case you need it on Playa. No need to bring anyone back to your camp in order to get your ID.</li> 
</ul>
If you have ?s, suggestions, ideas or concerns please contact us at camp{AT}disorient{DOT}info - and let's talk!<br>

<br><strong>Disorient loves you!!</strong> 

</div>

<form action="terms.jsp" method="get">
<div>
<br>
<input type="hidden" name="continue" value="1">
<input type="checkbox" name="accept" value="1">I have read and understood the rules above!<br>
<input type="submit" value="Continue">
</div>
</form>

</body>
</html>
