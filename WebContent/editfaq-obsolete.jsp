<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditFAQ())
    return;
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery" title="Edit FAQ"/>
<style type="text/css">
.centerer {
    display: flex;
    flex-direction: column;
    align-items: center;
}
#faq-form {
    display: inline-flex;
    flex-direction: column;
    width: 900px;
}
#faq-navigator {
    display: flex;
}
#categories {
    padding-right: 1ex;
    padding-bottom: 1ex;
    margin-right: 1ex;
    border-right: 1px dotted #666;
    flex-basis: 38%;
}
#questions {
    padding-bottom: 1ex;
    flex-basis: 62%;
}
#editor {
    border-top: 1px dotted #666 !important;
    padding-top: 1ex !important;
}
.panel, .list-items {
    display: flex;
    flex-direction: column;
}
.panel-list {
    min-width: 0;
}
.panel, .panel > div, .list-items, .list-items > div, #faq-form > div, .input-row, .input-row > div, .label {
    border: 0;
    margin: 0;
    padding: 0;
}
.list-items > div {
    display: flex;
    align-items: center;
    padding: 2px 4px;
    margin: 1px 0 0 0;
    border: 1px solid #666;
    background: #333;
    cursor: pointer;
}
.list-items > div > div.label {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.list-items > div:hover {
    border: 1px solid #844;
    background: #422;
    color: white;
}
.list-items > .selected-item, .list-items > .selected-item:hover {
    border: 1px solid #0cc;
    background: #077;
    color: white;
}
[data-edit-mode="q"] #category-list > .selected-item, 
[data-edit-mode="q"] #category-list > .selected-item:hover {
    border: 1px solid #ccc;
    background: #777;
    color: white;
}
div.title {
    padding-bottom: 0.5ex;
    font-weight: bold;
}
div.actions {
    padding-top: 0.5ex;
}
.btn {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 0;
    margin: 0 2px 0 0;
    padding: 0;
    cursor: pointer;
    flex-shrink: 0;
}
.btn:hover {
    filter: brightness(1.5);
}
.btn-delete {
    background-image: url(${pageContext.request.contextPath}/media/delete.png);
}
.btn-up {
    background-image: url(${pageContext.request.contextPath}/media/moveup.png);
}
.btn-down {
    background-image: url(${pageContext.request.contextPath}/media/movedown.png);
    margin-right: 6px;
}
.list-items > div:first-child .btn-up,
.list-items > div:last-child .btn-down {
    visibility: hidden;
}
.input-row {
    display: flex;
    padding-bottom: 4px !important;
}
.input-row > div:first-child {
    flex-basis: 8ex;
    padding-top: 2px;
}
.input-row > div:last-child {
    flex-grow: 1;
    display: flex;
    padding-left: 1ex;
}
.input-row textarea {
    width: 100% !important;
}
.init-invisible {
    display: none !important;
}
.infonote {
    opacity: 0.75;
    font-size: 90%;
}
[data-edit-mode="n"] .notn { display: none !important; }
[data-edit-mode="q"] .notq { display: none !important; }
[data-edit-mode="c"] .notc { display: none !important; }
[data-category-selected="yes"] .no-cat-selected { display: none !important; }
[data-category-selected="no"] .cat-selected { display: none !important; }
[data-edit-new="yes"] .not-edit-new { display: none !important; }
[data-edit-new="no"] .not-edit-existing { display: none !important; }
[data-edit-mode="c"][data-edit-new="yes"] #new-category-link { color: white; }
[data-edit-mode="q"][data-edit-new="yes"] #new-question-link { color: white; }
[data-edit-new="no"] #editor-submit::after { content: 'Save Changes'; }
[data-edit-mode="c"][data-edit-new="yes"] #editor-submit::after { content: 'Create Section'; }
[data-edit-mode="q"][data-edit-new="yes"] #editor-submit::after { content: 'Create Question'; }
</style>
<script type="text/javascript">
(function () {
	
	var faqEntries = {};

	$(document).ready(function () {
        $('.btn').click(function () {
            /*
            $(this).parent()
             .animate({opacity:0})
             .animate({height:0,'margin-top':0,'margin-bottom':0,'padding-top':0,'padding-bottom':0}, function () { $(this).remove(); });
            */
            return false;
        });
        $('#new-category-link').click(function () {
            selectItem({type:'c',creating:true});
        	return false;
        });
        $('#new-question-link').click(function () {
            selectItem({type:'q',creating:true});        
        	return false;
        });
        $('#editor-submit').click(function () {
        	submitChanges();
        	return false;
        });
        $('.btn-delete').attr('title', 'delete item');
        $('.btn-up').attr('title', 'move item up');
        $('.btn-down').attr('title', 'move item down');
        queryEntryList().then(function () {
        	selectItem();
            $('#faq-form').removeClass('init-invisible');            
        });
    });
    
	function reportServerError (e) {
        alert('A server error occurred: ' + e.status + ' ' + e.statusText);            
    }
    
	function checkAPIError (r) {
    	if (r.error)
    		alert('Error: ' + r.error_message);
    	return !r.error;
    }
	
    function queryEntryList () {
    	return $.post('${pageContext.request.contextPath}/ajax/faq/list', 'json').then(function (r) {
    		if (checkAPIError(r))
    			rebuildEntryList(r.entries);
    	}).fail(function (e) {
    		reportServerError(e);
    	});
    }
    
    function createListItem (itemId, text) {
        return $('<div/>')
           .attr('data-item', itemId)
           .append('<div class="btn btn-delete"></div>')
           .append('<div class="btn btn-up"></div>')
           .append('<div class="btn btn-down"></div>')
           .append($('<div class="label"/>').text(text));
    }
    
    function rebuildEntryList (entries) {
    	
    	faqEntries = entries;
    	
    	var clist = $('#category-list').empty();
    	var qlisttop = $('#question-lists').empty();
    	var qlists = {};
    	
    	for (let id in faqEntries) {
    		let entry = faqEntries[id];
    		if (!entry.is_category && !qlists[entry.parent]) {
    			qlists[entry.parent] = $('<div class="list-items"/>')
    			    .attr('data-category', entry.parent)
    			    .appendTo(qlisttop);
    		}
    		createListItem(id, faqEntries[id].title)
    		    .click(function () { selectItem(id, this); })
    		    .appendTo(faqEntries[id].is_category ? clist : qlists[entry.parent]);
    	}
    	
    }
    
    /*
    function rebuildQuestionList (category) {
    	var qlist = $('#question-list').empty();
    	if (category) {
	    	for (let qid of category.questions) {
	    		createListItem(qid, currentData[qid].q)
                    .click(function () { selectItem(listItemDetails(this)); })
                    .appendTo(qlist);
	    	}
	    	$('#faq-form').attr('data-category-selected', 'yes');
    	} else {
    		$('#faq-form').attr('data-category-selected', 'no');
    	}
    }*/
    
    function listItemDetails (element) {
        var r = {};
        r.element = element && $(element).closest('[data-item]');
        if (r.element && r.element.closest('#categories').length > 0) {
            r.type = 'c';
            r.item = Number(r.element.data('item'));
        } else if (r.element && r.element.closest('#questions').length > 0) {
            r.type = 'q';
            r.item = Number(r.element.data('item'));
        } else {
            r.type = 'n';
            r.item = null;
        }
        return r;
    }
    
    function selectItem (id, listitem) {
    	
    	var entry = id && faqEntries[id];
    	console.log('select ' + JSON.stringify(entry));
   
    	if (entry && entry.is_category) {
    		$('#faq-form').attr({
    			'data-category-selected': 'yes',
    			'data-edit-mode': 'c',
    			'data-edit-new': 'no',
    			'data-edit-item': id
    		});
    		$('[data-item]').removeClass('selected-item');
    		$(listitem).addClass('selected-item');
    		$('#question-lists [data-category]').each(function(_,e) { $(e).toggle($(e).data('category') == id); });
            $('#editor-category-title').val(entry.title);
    	} else if (entry && !entry.is_category) {
            $('#faq-form').attr({
                'data-category-selected': 'yes',
                'data-edit-mode': 'q',
                'data-edit-new': 'no',
                'data-edit-item': id
            });
            $('#question-lists [data-item]').removeClass('selected-item');
            $(listitem).addClass('selected-item');
            $('#editor-question-q').val(entry.title);
            $('#editor-question-a').val(entry.detail);
    	} else {
            $('#faq-form').attr({
                'data-category-selected': 'no',
                'data-edit-mode': 'n',
                'data-edit-new': 'no',
                'data-edit-item': null
            });
            $('[data-item]').removeClass('selected-item');
            $('#question-lists [data-category]').toggle(false);
    	}
    	
    	
        //$('#faq-form').attr('data-category-selected', 'no');

    	/*
        details = details || listItemDetails(null);
        $('#faq-form').attr('data-edit-mode', details.type);
        $('#faq-form').attr('data-edit-new', details.creating ? 'yes' : 'no');
        $('#faq-form').attr('data-edit-item', details.item || null);
        var container = null;
        /*
        if (details.type === 'c') {
        	rebuildQuestionList(currentData[details.item]);
        	container = $('#category-list');
        	$('#editor-category-title').val(details.item && currentData[details.item].title);
        } else if (details.type === 'q') {
        	container = $('#question-list');
        	$('#editor-question-q').val(details.item && currentData[details.item].q);
        	$('#editor-question-a').val(details.item && currentData[details.item].a);
        }
        */
        /*
        if (container)
            container.children('[data-item]').removeClass('selected-item');
        else
            $('[data-item]').removeClass('selected-item');
        if (details.element)
            details.element.addClass('selected-item');
        */
    }
    
    /*
    function submitChanges () {
    	var creating = ($('#faq-form').data('edit-new') === 'yes');
    	var type = $('#faq-form').data('edit-mode');
    	var url = null;
    	var params = {};
    	if (type === 'c') {
    		if (creating) {
    			url = '${pageContext.request.contextPath}/ajax/faq/newc';
    			params.title = $('#editor-category-title').val();
    		} else {
                alert('category edit not implemented');
    		}
    	} else if (type === 'q') {
            if (creating) {
                url = '${pageContext.request.contextPath}/ajax/faq/newq';
                params.c = Number($('#category-list .selected-item').data('item'));
                params.q = $('#editor-question-q').val();
                params.a = $('#editor-question-a').val();
            } else {
            	alert('question edit not implemented');
            }
    	}
    	if (url) {
    		$.post(url, params, 'json').then(function (r) {
                if (checkAPIError(r))
                    rebuildEntryList(r.entries);
            }).fail(function (e) {
                reportServerError(e);
    		});
    	}
    }
    */
    window.Disorient = window.Disorient || {};
    window.Disorient.FAQ = {
        //newCategory: newCategory,
        //newQuestion: newQuestion
        data: function () { return faqEntries; }
    };
})();
</script>
</head>
<body>
<dis:header/>
<div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div>

	<div class="centerer">
		<div id="faq-form" class="init-invisible">
			<div id="faq-navigator">
				<div id="categories" class="panel panel-list">
					<div class="title">Sections:</div>
					<div class="list-items" id="category-list"></div>
					<div class="actions">
						<a href="#" id="new-category-link">New Section...</a>
					</div>
				</div>
				<div id="questions" class="panel panel-list">
					<div class="title">Questions:</div>
					<div id="question-lists"></div>
					<div class="actions">
					    <span class="no-cat-selected infonote">No section selected.</span>
						<a href="#" id="new-question-link" class="cat-selected">New Question...</a>
					</div>
				</div>
			</div>
			<div id="editor" class="panel">
				<div class="notq notc infonote">Select a question or section above to edit.</div>
                <div class="notn notc not-edit-new title">Edit Question:</div>
                <div class="notn notc not-edit-existing title">New Question:</div>
				<div class="notn notc input-row">
					<div>Question:</div>
					<div><input id="editor-question-q" placeholder="Question" type="text" class="dtext"></div>
				</div>
				<div class="notn notc input-row">
					<div>Answer:</div>
					<div><textarea id="editor-question-a" placeholder="Answer. Markdown formatting is allowed." class="dtextarea"></textarea></div>
				</div>
                <div class="notn notq not-edit-new title">Edit Section:</div>
                <div class="notn notq not-edit-existing title">New Section:</div>
				<div class="notn notq input-row">
					<div>Title:</div>
					<div><input id="editor-category-title" placeholder="Section Title" type="text" class="dtext"></div>
				</div>
				<div class="notn input-row">
				    <div></div>
				    <div><button class="dbutton" id="editor-submit"></button></div>
				</div>
			</div>
		</div>
	</div>

	<!-- <div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div> -->
<dis:footer/>
</body>
</html>