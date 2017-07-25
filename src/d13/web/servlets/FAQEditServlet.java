package d13.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import d13.dao.FAQItem;
import d13.util.HibernateUtil;
import d13.util.Util;
import d13.web.SessionData;

/**
 * Servlet implementation class FAQEditServlet
 */
@WebServlet("/ajax/faq/*")
public class FAQEditServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
       
	// For development only; don't forget to remove this.
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
	@Override
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    JSONObject result = new JSONObject();

	    try {
	        
	        SessionData sess = new SessionData(request.getSession());
	        if (!sess.isLoggedIn() || !sess.getUser().getRole().canEditFAQ())
	            throw new SecurityException("Permission denied.");
	        
	        String[] parts = request.getPathInfo() == null ? null : Util.removeEmptyStrings(request.getPathInfo().split("/"));
	        String action = ((parts != null && parts.length == 1) ? parts[0] : "");

	        switch (action) {
	        case "list":
	            listEntries(result);
	            break;
	        case "newc":
	            createCategory(result, request.getParameter("title"));
	            break;
	        case "newq":
	            createQuestion(result, request.getParameter("c"), request.getParameter("q"), request.getParameter("a"));
	            break;
	        default:
	            response.sendError(404);
	            return;
	        }
	        
	        result.put("error", false);
	        
	    } catch (SecurityException secx) {
	        response.sendError(403);
	        return;
	    } catch (IllegalArgumentException x) {
	        try {
	            result = new JSONObject();
    	        result.put("error", true);
    	        result.put("error_message", x.getMessage());
	        } catch (JSONException dumb) {
	            throw new ServletException(dumb);
	        }
	    } catch (ServletException | IOException sx) {
	        throw sx;
	    } catch (Throwable t) {
	        throw new ServletException(t);
	    }
	    
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    out.print(result.toString());
	    out.close();
	    
	}
	
	public static void listEntries (JSONObject result) throws JSONException {
	    
	    JSONObject entries = new JSONObject();
	    
        for (FAQItem category : FAQItem.findRoot().getChildren()) {
            JSONArray qids = new JSONArray();
            for (FAQItem question : category.getChildren()) {
                JSONObject jsonQuestion = new JSONObject();
                //jsonQuestion.put("id", question.getFaqId());
                jsonQuestion.put("title", question.getTitle());
                jsonQuestion.put("detail", question.getDetail());
                jsonQuestion.put("parent", category.getFaqId());
                entries.put(Long.toString(question.getFaqId()), jsonQuestion);
                qids.put(question.getFaqId());
            }
            JSONObject jsonCategory = new JSONObject();
            //jsonCategory.put("id", category.getFaqId());
            jsonCategory.put("title", category.getTitle());
            jsonCategory.put("detail", category.getDetail());
            jsonCategory.put("is_category", true);
            jsonCategory.put("questions", qids);
            entries.put(Long.toString(category.getFaqId()), jsonCategory);
        }	    
	    
	    result.put("entries", entries);
	        
	}
	
	public static void createCategory (JSONObject result, String title) throws JSONException {
	    
	    FAQItem item = FAQItem.findRoot().addCategory(title);
	    HibernateUtil.getCurrentSession().save(item); // todo: move into dao?
	    
	    result.put("item", item.getFaqId());	    
	    listEntries(result);
	    
	}
	
	public static void createQuestion (JSONObject result, String parentStr, String question, String answer) throws JSONException {
	    
	    long parent;
	    try {
	        parent = Long.parseLong(parentStr);
	    } catch (NumberFormatException x) {
	        throw new IllegalArgumentException("Valid parent ID must be specified.");
	    }
	    
	    FAQItem item = FAQItem.findById(parent).addQuestion(question, answer);
        HibernateUtil.getCurrentSession().save(item); // todo: move into dao?
        
        result.put("item", item.getFaqId());        
        listEntries(result);	    
	    
	}

}
