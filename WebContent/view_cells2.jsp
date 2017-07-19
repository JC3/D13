<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
void buildCellList (List<Cell> cs, Cell c) {
    if (c.isCategory()) {
        for (Cell cell:c.getChildren())
            buildCellList(cs, cell);
    } else {
        cs.add(c);
    }
}
void buildUserList (List<User> users, Collection<User> source, UserState state) {
    for (User u:source)
        if (u.getState() == state)
            users.add(u);
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canViewUsers())
    return;

Role role = sess.getUser().getRole();
boolean canEdit = role.canEditCells();
boolean canCreate = role.canCreateCells();

List<Cell> cells = new ArrayList<Cell>();
buildCellList(cells, Cell.findRoot());

List<Cell> cats = null;
if (canEdit)
    cats = Cell.findCategories(false);

String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));

String back_url = request.getParameter("next");
if (back_url == null)
    back_url = "home.jsp";

String message = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_CELL_MESSAGE);
String message_html = (message == null ? null : Util.html(message));
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery" title="View Cells"/>
<style type="text/css">
#maindiv { 
    width:75%;
    margin-left:auto;
    margin-right:auto;
}
#leftside {
    white-space: nowrap; 
    vertical-align:top;
    border-right:1px solid #804000 !important;
    padding-right: 1ex !important;
    margin-right: 1ex !important;
}
#rightside { 
    vertical-align:top;
    width:100%;
    padding-left: 1ex !important;
}
#slide {
    padding: 0;
    margin: 0;
    position: relative;
}
.toggle-pane {
    display:none;
    vertical-align:top;
    margin:0;
    padding:0;
}
.celldetails {
}
.celldetails > b, .celldetails > table {
    font-size: 90%;
}
.leftdetail {
    white-space: nowrap; 
    vertical-align:top;
    border-right:1px dotted #402000 !important;
    padding-right: 1ex !important;
    margin-right: 1ex !important;
}
.rightdetail {
    white-space: nowrap; 
    vertical-align:top;
    width: 100%;
    padding-left: 1ex !important;
}
#details_help {
}
table.volunteers td {
    text-align:left;
    white-space:nowrap;
    border-bottom:1px solid #603000 !important;
}
table.volunteers th {
    text-align:left;
    white-space:nowrap;
    background:#603000;
}
.indicator {
    font-family:"Consolas", "Monaco", "Courier New", monospace;
    font-size:90%;
    color:#cccccc;
}
td.indicator {
    text-align:center;
    padding-right:2px !important;
}
.indicator-letter {
    color:#00ccff;
}
.indicator-empty {
    color:red;
}
.indicator-low {
    color:yellow !important;
}
.indicator-full {
    color:#00ff00 !important;
}
.cell-mandatory {
    background:#800000 !important;
}
.cell-hidden {
    color: #bb6000;
}
td.cell-hidden a:link {
    color: #bb6000;
}
td.cell-hidden a:visited {
    color: #bb6000;
}
td.cell-hidden a:active {
    color: #bb6000;
}
.cell-link-td .cell-link {
    padding-left: 4px;
}
.cell-link-td {
}
.cell-hidden.current-cell a {
    color:#bbbbbb !important;
}
.select-mode-only {
    display: none;
}
.current-cell a {
    color:white !important;
}
.history {
}
.history h1 {
    font-size: medium;
    color: #ff8000;
}
.history .who {
    font-size: small;
    color: #ffeedd;
    margin-bottom: 0;
}
.history .desc {
    font-size: small;
    color: #ffeedd;
    padding-left: 4ex !important;
    margin-top: 0.25ex !important;
}
.catheader {
    padding-top: 1ex !important;
}
#details_select input[type="checkbox"] {
    margin-left: 0;
}
#details_select td:first-child {
    padding-right: 1ex !important;
}
</style>
<script type="text/javascript">
var curhashid = null;
function showDetails (id) {
    var divid = 'details_' + id;
    var linkid = 'link_' + id;
    var divs = document.getElementsByClassName('toggle-pane');
    var previous = null;
    var gotone = false;
    for (let i = 0; i < divs.length; ++ i) {
        let obj = divs[i];
        if (obj.classList.contains('active-pane'))
            previous = obj.id.substring(8);
        if (obj.id == divid) {
            obj.style.display = 'block';
            obj.classList.add('active-pane');
            gotone = true;
        } else {
            obj.style.display = 'none';
            obj.classList.remove('active-pane');
        };
    }
    var links = document.getElementsByClassName('cell-link');
    for (let i = 0; i < links.length; ++ i) {
        let obj = links[i];
        if (obj.id == linkid) {
            obj.parentElement.classList.add('current-cell');
        } else {
            obj.parentElement.classList.remove('current-cell');
        };
    }
    curhashid = '#' + id;
    window.location.hash = '#' + id;
    if (!gotone) {
        document.getElementById('details_help').style.display = 'block';
        document.getElementById('details_help').classList.add('active-pane');
    }
<% if (canEdit) { %>
    setEditMultiple(document.getElementById('details_select').classList.contains('active-pane'), previous || 'help');
<% } %>
    $(window).trigger('scroll');
}

function displayAnchor () {
    var anchor = window.location.hash.substring(1);
    if (anchor.length > 0)
        showDetails(anchor);
    else
        showDetails('help');
}

window.onhashchange = function () {
    if (window.location.hash !== curhashid) // main goal: prevent recursion from displayAnchor
        displayAnchor();
};

$(window).scroll(function () {
    var s = $('#slide');
    if (s.height() < window.innerHeight) {
        var y = $(document).scrollTop() - s.parent().offset().top;
        var b = s.parent().height() - s.outerHeight();
        if (y < 0)
            y = 0;
        else if (y > b)
            y = b;
        s.css('top', y);
    } else {
        s.css('top', 0);
    };
});

<% if (canEdit) { %>
var initselect = null;
if (window.location.hash === '#select') {
	try {
	    initselect = JSON.parse(sessionStorage.getItem('jsp.view_cells2.selected'));
	} catch (e) {
	}
}
sessionStorage.removeItem('jsp.view_cells2.selected');

function setEditMultiple (enabled, previous) {
    if ($('#edit-multiple-link').data('active') == enabled)
        return; // nothing to do
    $('#edit-multiple-link').data('active', enabled);
    if (enabled) {
        $('#edit-multiple-link')
          .attr('href', "javascript:showDetails('" + previous + "');")
          .text('Cancel Edit Multiple');
    } else {
        $('#edit-multiple-link')
          .attr('href', "javascript:showDetails('select');")
          .text('Edit Multiple');
    }
    $('.select-mode-only').toggle(enabled);
    // reset the checkboxes to indeterminate
    if (enabled) {
        $('.cell-select-checkbox').each(function (_,cb) {
            cb.checked = false;
            cb.readOnly = cb.indeterminate = true;
        });
    };
    // apply initial state if specified
    if (initselect) {
        setCheckState(document.getElementById('cell-select-hide-when-full'), initselect.autohide);
        setCheckState(document.getElementById('cell-select-mandatory'), initselect.mandatory);
        setCheckState(document.getElementById('cell-select-hidden'), initselect.hidden);
        $('.select-cell').prop('checked', false);
        for (let id of initselect.cells)
        	$('.select-cell[data-cid="' + id + '"]').prop('checked', true);
        selectUpdate();
    	initselect = null;
    }
}
/*
 *         autohide: checkState(document.getElementById('cell-select-hide-when-full')),
 mandatory: checkState(document.getElementById('cell-select-mandatory')),
 hidden: checkState(document.getElementById('cell-select-hidden')),
 cells: $.map($('.select-cell[data-cid]:checked'), function (e) { return +e.dataset.cid; })

 */
function selectAll (all) {
    $('.select-cell').prop('checked', all);
    selectUpdate();
}

function selectUpdate () {
    var count = $('.select-cell:checked').length;
    $('#cell-select-count').text(count + ' cell' + (count === 1 ? '' : 's'));   
}

function checkState (cb) {
    return cb.indeterminate ? null : cb.checked;
}

function setCheckState (cb, state) {
	cb.checked = (state === true);
	cb.readOnly = cb.indeterminate = (state === null);
}

function selectApply () {
    var changes = {
        autohide: checkState(document.getElementById('cell-select-hide-when-full')),
        mandatory: checkState(document.getElementById('cell-select-mandatory')),
        hidden: checkState(document.getElementById('cell-select-hidden')),
        cells: $.map($('.select-cell[data-cid]:checked'), function (e) { return +e.dataset.cid; })
    };
    if (changes.cells.length === 0) {
        alert('No cells selected.');
        return;
    }
    if (changes.autohide === null && changes.mandatory === null && changes.hidden === null) {
    	alert('Select some settings to change.')
        return;
    }
    if (!confirm('Are you sure?'))
        return;
    $.post('${pageContext.request.contextPath}/ajax/inline_edit_cells.jsp', changes, 'json').then(function (r) {
        if (r.error)
            alert('Error: ' + r.error_message);
        else {
            if (r.warnings && r.warnings.length)
                console.log('Warnings: ' + r.warnings);
            sessionStorage.setItem('jsp.view_cells2.selected', JSON.stringify(changes));
            window.location.reload();
        };
    }).fail(function (e) {
        alert('A server error occurred: ' + e.status + ' ' + e.statusText);     
    });
}

//function updateProps (p) {
//	console.log(JSON.stringify(p));
//}

function moveCell (id, dir) {
    $.post('ajax/move_cell.jsp', {
        c: id,
        d: dir
    }, 'json').then(function (r) {
        if (r.ok)
            window.location.reload();
        else
            alert('Could not move cell: ' + r.e);
    }).fail(function (e) {
        alert('A server error occurred: ' + e.status + ' ' + e.statusText);        
    });
}

$(document).ready(function () {
    $('.cell-select-checkbox').click(function (ev) {
        var cb = ev.target;
        if (cb.readOnly) 
            cb.checked = cb.readOnly = false;
        else if (!cb.checked) 
            cb.readOnly = cb.indeterminate = true;
    });
    $('#cell-select-apply').click(function () {
        selectApply();
    });
});
<% } %>
</script>
</head>
<body onLoad="displayAnchor();">
<dis:header/>
<div class="nav">
     <a href="<%= Util.html(back_url) %>">Go Back</a> | <a href="javascript:showDetails('help');">Help</a>
</div>
<!-- ----------------------------------------------------------------- -->
<% if (message != null) { %>
<div class="message" style="margin-top:1em"><%=message_html%></div>
<% } %>

<div id="maindiv">

<table style="width:100%">

<tr><td id="leftside">

  <table class="zerotable">
<% if (cats != null && canEdit) { %>
    <tr>
      <td colspan="3">Cells<span class="select-mode-only"> (<a href="javascript:selectAll(true);">Select All</a> | <a href="javascript:selectAll(false);">Select None</a>)</span>:
<% } %>
<% for (Cell cell:cells) { 
    int max = cell.getPeople();
    int tot = cell.getUsers().size();
    String excls = "";
    String nexcls = "";
    if (cell.isFull()) excls += " indicator-full";
    if (cell.getUsers().isEmpty()) 
        excls += " indicator-empty";
    else if (cell.getPeople() > 0 && cell.getUsers().size() <= cell.getPeople() / 2) 
        excls += " indicator-low";
    if (cell.isMandatory()) nexcls += " cell-mandatory";
    if (cell.isReallyHidden()) nexcls += " cell-hidden";
%>
    <tr>
      <td class="indicator indicator-letter"><%= cell.isHidden() ? "H" : (cell.isHideWhenFull() ? "A" : "") %>
      <td class="indicator<%= excls %>"><%= ((max > 0) ? String.format("%2d/%2d", tot, max) : String.format("%2d   ", tot)).replaceAll(" ", "&nbsp;") %>
      <td class="cell-link-td<%= nexcls %>"><% if (canEdit) { %><input type="checkbox" class="select-mode-only select-cell" onchange="selectUpdate()" data-cid="<%=cell.getCellId()%>"><% } %><a class="cell-link" id="link_<%=cell.getCellId() %>" href="javascript:showDetails('<%=cell.getCellId() %>');"><%=Util.html(cell.getFullName()) %></a>
<% } %>
<% if (cats != null && canEdit) { %>
    <tr>
      <td class="catheader" colspan="3">Categories:
<%  for (Cell cat : cats) { 
     String excls = "";
     if (cat.getNonCategoryChildCount() == 0)
         excls += " indicator-empty"; 
    %>
    <tr>
      <td class="indicator indicator-letter">
      <td class="indicator<%= excls %>"><%= String.format("%2d   ", cat.getChildren().size()).replaceAll(" ", "&nbsp;") %>
      <td class="cell-link-td"><a class="cell-link" id="link_<%=cat.getCellId() %>" href="javascript:showDetails('<%=cat.getCellId() %>');"><%=Util.html(cat.getFullName()) %></a>
<%  }
   } 
   if (canCreate || canEdit) { %>
    <tr><td class="catheader" colspan="3">
      <a href="javascript:showDetails('select');" id="edit-multiple-link">Edit Multiple</a>
      <% if(canCreate) { %> | <a href="editcell.jsp?c=new&next=<%= this_url %>">New Cell...</a><% } %>
<% } %>
  </table>

<td id="rightside">
<div id="slide">
<div id="details_help" class="toggle-pane">
<p>Click a cell on the left to view details. The numbers and letters to the left of the cell names mean:</p>
<ul>
<li>An <span class="indicator indicator-letter">H</span> means the cell is set to always be hidden.
<li>An <span class="indicator indicator-letter">A</span> means the cell is set to auto hide when full.
<li>If the cell name is <span class="cell-hidden">dark orange</span> then it is currently hidden as per above rules.
<li>The number of people in the cell and total number of people needed is also shown.
<li>If the number is <span class="indicator-empty">red</span> then the cell is empty.
<li>If the number is <span class="indicator-low">yellow</span> then the cell is &le; half full.
<li>If the number is <span class="indicator-full">green</span> then the cell is full.
<li>If the cell name is <span class="cell-mandatory">highlighted</span> then the cell is mandatory.
</ul>
<p>Cells that are <span class="cell-mandatory">mandatory</span> but also <span class="cell-hidden">hidden</span> are weird and should be avoided.
They won't cause problems for users but do take up space adding warnings to the server logs and are probably a general sign that the cell's rules and
purpose should be reconsidered.</p>
<% if (canEdit) { %>
<p>You can edit cell descriptions and such by clicking a cell name then clicking the edit link in the details. Categories appear at
the bottom of the list (the numbers on the left are the number of cells/subcategories in that category) and you can edit them in a similar way.</p>
<% } %>
<% if (canCreate) { %>
<p>You can also create/delete/move cells:</p>
<ul>
<li>To create a new cell click "New Cell..." at the bottom of the list on the left.
<li>New categories can be created from the cell detail edit page.
<li>Cells and categories can be deleted from the cell detail edit page.
<li>Categories can be renamed and moved to other categories, just like cells.
<li>To move a cell in the list, select it and use "Move Up" and "Move Down" next to "Edit Cell Details".
<li>To move an entire category, select the first/last cell in the category and move it up/down.
</ul>
<% } %>
</div>
<% if (canEdit) { %>
<div id="details_select" class="toggle-pane" style="display:none">
  <p>Select one or more cells, then select the changes you want to make below and press "Apply". A checkbox that looks like <input type="checkbox" id="example-checkbox" style="margin:0;padding:0;"> means don't change that setting.</p>
  <script type="text/javascript">
  (function(){
      var cb = document.getElementById('example-checkbox');
      cb.indeterminate = true;
      cb.onchange = function () { cb.indeterminate = true; };
  })();
  </script>
  <table>
  <tr><td>Selected:<td id="cell-select-count">0 cells
  <tr><td><label for="cell-select-hide-when-full">Hide When Full?</label><td><input type="checkbox" id="cell-select-hide-when-full" class="cell-select-checkbox">
  <tr><td><label for="cell-select-mandatory">Mandatory?</label><td><input type="checkbox" id="cell-select-mandatory" class="cell-select-checkbox">
  <tr><td><label for="cell-select-hidden">Hidden?</label><td><input type="checkbox" id="cell-select-hidden" class="cell-select-checkbox">
  <tr><td><td><input id="cell-select-apply" type="button" class="dbutton" value="Apply">  
  </table>
</div>
<% } %>
<% 
List<Cell> allthings = new ArrayList<Cell>(cells);
if (cats != null && canEdit)
    allthings.addAll(cats);
for (Cell cell:allthings) { 
    boolean cat = cell.isCategory();
    List<User> approved = new ArrayList<User>();
    List<User> pending = new ArrayList<User>();
    List<User> review = new ArrayList<User>();
    if (!cat) {
        buildUserList(approved, cell.getUsers(), UserState.APPROVED);
        java.util.Collections.sort(approved, new User.RealNameComparator());
        buildUserList(approved, cell.getUsers(), UserState.APPROVE_PENDING);
        buildUserList(pending, cell.getUsers(), UserState.REGISTERED);
        java.util.Collections.sort(pending, new User.RealNameComparator());
        buildUserList(review, cell.getUsers(), UserState.NEEDS_REVIEW);
        java.util.Collections.sort(review, new User.RealNameComparator());
    }
%>
<div id="details_<%=cell.getCellId()%>" class="celldetails toggle-pane">

<b style="font-size:100% !important"><%= cat ? "Category" : "Cell" %>:</b> <%= Util.html(cell.getFullName()) %><br>
<% if (cat) { %>
<b style="font-size:100% !important">Items:</b> <%= cell.getChildren().size() %><br>
<hr>
<ul>
<%   for (Cell catchild : cell.getChildren()) { %>
<li><%= catchild.isCategory() ? "Category" : "Cell" %>: <a class="cell-link" id="link_<%=catchild.getCellId() %>" href="javascript:showDetails('<%=catchild.getCellId() %>');"><%= Util.html(catchild.getName()) %></a>
<%   } %>
</ul>
<% } else { %>
<b style="font-size:100% !important">Volunteers:</b> <%= cell.getUsers().size() + (cell.getPeople() > 0 ? " of " + cell.getPeople() : "") %><br>
<b style="font-size:100% !important">Description:</b> <%= Util.html(cell.getDescription()) %><br>
<hr>

<b style="color: #ffddaa;">APPROVAL FINALIZED:</b><br>

<table style="width:100%">
<tr><td class="leftdetail">
<b>Email Addresses:</b><br>
  <textarea style="dtextarea"><% 
    {
    boolean first = true;
    for (User u:approved) {
        if (first)
            first = false;
        else
            out.print(", ");
        out.print(Util.html(u.getEmail())); 
    }
} %></textarea><br><br>
<td class="rightdetail">

<table style="width:100%" class="volunteers">
<tr><th>Name<th>Email<th>Arrival<th>Departure
<% 
for (User u:approved) { %>
<tr><td><a href="details.jsp?u=<%=u.getUserId()%>&next=<%=this_url%>%23<%=cell.getCellId()%>"><%=Util.html(u.getRealName()) %></a>
    <td><a href="mailto:<%=Util.html(u.getEmail())%>"><%=Util.html(u.getEmail()) %></a>
    <% if (u.isRegistrationComplete()) { %>
      <td><%=Util.html(u.getRegistration().getArrivalDate()) %>
      <td><%=Util.html(u.getRegistration().getDepartureDate() + " " + u.getRegistration().getDepartureTime()) %>
    <% } else { %>
      <td><td>
    <% }
} %>
</table>

</table>

<hr>
<b style="color: #ffddaa;">APPROVED / REGISTERED:</b><br>

<table style="width:100%">
<tr><td class="leftdetail">
<b>Email Addresses:</b><br>
  <textarea style="dtextarea"><% 
    {
        boolean first = true;
        for (User u:pending) {
            if (first)
                first = false;
            else
                out.print(", ");
            out.print(Util.html(u.getEmail())); 
        }
    } 
  %></textarea><br><br>
  
<td class="rightdetail">
<table style="width:100%" class="volunteers">
<tr><th>Name<th>Email<th>Arrival<th>Departure
<% for (User u:pending) { %>
<tr><td><a href="details.jsp?u=<%=u.getUserId()%>&next=<%=this_url%>%23<%=cell.getCellId()%>"><%=Util.html(u.getRealName()) %></a>
    <td><a href="mailto:<%=Util.html(u.getEmail())%>"><%=Util.html(u.getEmail()) %></a>
    <% if (u.isRegistrationComplete()) { %>
      <td><%=Util.html(u.getRegistration().getArrivalDate()) %>
      <td><%=Util.html(u.getRegistration().getDepartureDate() + " " + u.getRegistration().getDepartureTime()) %>
    <% } else { %>
      <td><td>
    <% } %>
<% } %>
</table>

</table>
<!-- section -->
<hr>
<b style="color: #ffddaa;">NEEDS REVIEW:</b><br>

<table style="width:100%">
<tr><td class="leftdetail">
<b>Email Addresses:</b><br>
  <textarea style="dtextarea"><% 
    {
        boolean first = true;
        for (User u:review) {
            if (first)
                first = false;
            else
                out.print(", ");
            out.print(Util.html(u.getEmail())); 
        }
    } 
  %></textarea><br><br>
  
<td class="rightdetail">
<table style="width:100%" class="volunteers">
<tr><th>Name<th>Email<th>Arrival<th>Departure
<% for (User u:review) { %>
<tr><td><a href="details.jsp?u=<%=u.getUserId()%>&next=<%=this_url%>%23<%=cell.getCellId()%>"><%=Util.html(u.getRealName()) %></a>
    <td><a href="mailto:<%=Util.html(u.getEmail())%>"><%=Util.html(u.getEmail()) %></a>
    <% if (u.isRegistrationComplete()) { %>
      <td><%=Util.html(u.getRegistration().getArrivalDate()) %>
      <td><%=Util.html(u.getRegistration().getDepartureDate() + " " + u.getRegistration().getDepartureTime()) %>
    <% } else { %>
      <td><td>
    <% } %>
<% } %>
</table>

</table>
<!-- end -->
<% } // if (!cat) %>

<% if (canEdit) {
    List<CellActivityLogEntry> logs = cell.getActivityLog();
    %>
<hr>
<p>
  <a href="editcell.jsp?c=<%= cell.getCellId() %>&next=<%= this_url %>%23<%= cell.getCellId() %>">Edit <%= cat ? "Category" : "Cell" %> Details</a>
<% if (!cat) { %>
  | <a href="javascript:moveCell(<%=cell.getCellId()%>,'u')">Move Up</a>
  | <a href="javascript:moveCell(<%=cell.getCellId()%>,'d')">Move Down</a>
<% } %>
</p>
  <% if (logs != null && !logs.isEmpty()) { %>
  <div class="history">
  <h1>Edit History:</h1>
    <%
       DefaultDataConverter ddc = new DefaultDataConverter();
       for (CellActivityLogEntry e:logs) { 
          String datestr = Util.html(ddc.asString(e.getTime()));
          String whostr = Util.html(e.getByWho().getEmail());
          String descstr = Util.html(e.getDescription()); 
    %>
          <div class="who"><%=datestr%>, <a href="mailto:<%=whostr%>"><%=whostr%></a>:</div>
          <div class="desc"><%=descstr%></div>
    <% } %>
  </div>
  <% } %>
<% } %>

</div>
<% } /* end loop over each cell */ %>
</div>
</table>

</div>

<!-- ----------------------------------------------------------------- -->
<br>
<div class="nav">
     <a href="<%= Util.html(back_url) %>">Go Back</a> | <a href="javascript:showDetails('help');">Help</a>
</div>
<dis:footer/>
</body>
</html>