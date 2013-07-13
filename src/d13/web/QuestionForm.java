package d13.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;
import d13.questions.Question;
import d13.questions.Question.Choice;
import d13.util.Util;

public class QuestionForm {
    
    public static void writeQuestions (JspWriter out, List<Question> qs, Object defaults) throws IOException {
        
        writeQuestions(out, qs, defaults, false);
        
    }

    public static void writeQuestions (JspWriter out, List<Question> qs, Object defaults, boolean script) throws IOException {
        
        for (Question q:qs)
            writeQuestion(out, q, defaults, script);
        
    }
    
    public static void writeQuestion (JspWriter out, Question q, Object defaults, boolean script) throws IOException {
       
        out.println("<div class=\"question\" id=\"" + q.getField() + "\">");
        out.println("<div class=\"qname\">" + Util.html(q.getBrief()) + "</div>");
        out.println("<div class=\"qinput\">");
        
        /*
        out.print("<tr><td style=\"width:50ex;vertical-align:top;\"><b>");
        out.print(Util.html(q.getBrief()));
        out.print("</b>");
        if (q.getDetail() != null) {
            out.print("<br>");
            out.print(q.getDetail());
        }
        out.println("<td style=\"vertical-align:top;\">");
        */
        
        switch (q.getType()) {
        case Question.TYPE_SHORT_TEXT: writeShortText(out, q, defaults); break;
        case Question.TYPE_LONG_TEXT: writeLongText(out, q, defaults); break;
        case Question.TYPE_PASSWORD: writePassword(out, q, defaults); break;
        case Question.TYPE_SINGLE_CHOICE: writeSingleChoice(out, q, defaults, script); break;
        case Question.TYPE_MULTI_CHOICE: writeMultiChoice(out, q, defaults); break;
        case Question.TYPE_DROPLIST: writeDropList(out, q, defaults); break;
        case Question.TYPE_USER_DROPLIST: writeUserDropList(out, q, defaults); break;
        }
     
        out.println("</div>");
        out.println("<div class=\"qdesc\">" + (q.getDetail() == null ? "" : q.getDetail()) + "</div>");
        out.println("</div>");

    }
    
    private static void writeShortText (JspWriter out, Question q, Object defaults) throws IOException {
        
        String valuestr = null;
        
        if (defaults != null) {
            try {
                valuestr = BeanUtils.getProperty(defaults, q.getField());
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        
        out.println(String.format("<input class=\"dtext\" type=\"text\" name=\"%s\" value=\"%s\">", Util.html(q.getField()), Util.html(valuestr)));
        
    }
    
    private static void writeLongText (JspWriter out, Question q, Object defaults) throws IOException {
        
        String valuestr = null;
        
        if (defaults != null) {
            try {
                valuestr = BeanUtils.getProperty(defaults, q.getField());
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        
        out.println(String.format("<textarea class=\"dtextarea\" name=\"%s\">%s</textarea>", Util.html(q.getField()), Util.html(valuestr)));

    }
    
    private static void writePassword (JspWriter out, Question q, Object defaults) throws IOException {
        
        String valuestr = null;
        
        if (defaults != null) {
            try {
                valuestr = BeanUtils.getProperty(defaults, q.getField());
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        
        out.println(String.format("<input class=\"dtext\" type=\"password\" name=\"%s\" value=\"%s\">", Util.html(q.getField()), Util.html(valuestr)));
        
    }
    
    private static void writeSingleChoice (JspWriter out, Question q, Object defaults, boolean script) throws IOException {
        
        for (Choice c:q.getChoices()) {

            String defaultvaluestr = null;
            if (defaults != null) {
                try {
                    defaultvaluestr = BeanUtils.getProperty(defaults, c.getField());
                    // hack for booleans
                    if ("true".equalsIgnoreCase(defaultvaluestr))
                        defaultvaluestr = "1";
                    else if ("false".equalsIgnoreCase(defaultvaluestr))
                        defaultvaluestr = "0";
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
          
            //System.out.println("field: " + c.getField() + " default: " + defaultvaluestr + " current: " + c.getValue());
            
            boolean checked = c.getValue().equalsIgnoreCase(defaultvaluestr);
            
            out.print(String.format("<input class=\"dradio\" id=\"%s_%s\"%s type=\"radio\" name=\"%s\" value=\"%s\"%s>%s",
                    Util.html(c.getField()), Util.html(c.getValue()),
                    script ? " onclick=\"updateVisibility()\"" : "",
                    Util.html(c.getField()), Util.html(c.getValue()), checked ? " checked" : "", Util.html(c.getText())));
            if (c.isOther()) {
                String othervaluestr = null;
                if (defaults != null) {
                    try {
                        othervaluestr = BeanUtils.getProperty(defaults, c.getField() + "Other");
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
                out.print(String.format(": <input class=\"dtext\" type=\"text\" name=\"%sOther\" value=\"%s\">", Util.html(c.getField()), Util.html(othervaluestr)));
            }
            out.println("<br>");
        }
        
    }
    
    private static void writeDropList (JspWriter out, Question q, Object defaults) throws IOException {
        
        out.println(String.format("<select class=\"dselect\" name=\"%s\">", q.getField()));

        out.println("<option value=\"\">-- Select One --</option>");
        
        for (Choice c:q.getChoices()) {

            String defaultvaluestr = null;
            if (defaults != null) {
                try {
                    defaultvaluestr = BeanUtils.getProperty(defaults, c.getField()); 
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
            
            boolean checked = c.getValue().equalsIgnoreCase(defaultvaluestr);
            
            out.println(String.format("<option value=\"%s\"%s>%s</option>", Util.html(c.getValue()), checked ? " selected" : "", Util.html(c.getText())));
/*            if (c.isOther()) {
                String othervaluestr = null;
                if (defaults != null) {
                    try {
                        othervaluestr = BeanUtils.getProperty(defaults, c.getField() + "Other");
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
                out.print(String.format(": <input type=\"text\" name=\"%sOther\" value=\"%s\">", Util.html(c.getField()), Util.html(othervaluestr)));
            }
            out.println("<br>");*/
        }
        
        out.println("</select>");
        
    }
    
    private static void writeUserDropList (JspWriter out, Question q, Object defaults) throws IOException {
        
        out.println(String.format("<select class=\"dselect\" name=\"%s\">", q.getField()));
       
        out.println("<option value=\"\">-- Select One --</option>");

        String defaultvaluestr = null;
        if (defaults != null) {
            try {
                defaultvaluestr = BeanUtils.getProperty(defaults, q.getField()); 
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        
        for (User u:User.findAll("realName")) {
            boolean checked = u.toString().equalsIgnoreCase(defaultvaluestr);
            out.println(String.format("<option value=\"%s\"%s>%s</option>", Util.html(u.toString()), checked ? " selected" : "", Util.html(u.getRealName())));
        }
        
        out.println("</select>");
        
    }

    private static void writeMultiChoice (JspWriter out, Question q, Object defaults) throws IOException {
        
        for (Choice c:q.getChoices()) {
            
            boolean checked = false;
            if (defaults != null) {
                try {
                    checked = Boolean.parseBoolean(BeanUtils.getProperty(defaults, c.getField())); 
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
            
            out.print(String.format("<input class=\"dcheckbox\" type=\"checkbox\" name=\"%s\" value=\"%s\"%s>%s", Util.html(c.getField()), Util.html(c.getValue()), checked ? " checked" : "", Util.html(c.getText())));
            if (c.isOther()) {
                String othervaluestr = null;
                if (defaults != null) {
                    try {
                        othervaluestr = BeanUtils.getProperty(defaults, c.getField() + "Other");
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
                out.print(String.format(": <input class=\"dtext\" type=\"text\" name=\"%sOther\" value=\"%s\">", Util.html(c.getField()), Util.html(othervaluestr)));
            }
            out.println("<br>");
            
        }

    }
    
}
