<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%
java.util.Map<String,String[]> params = request.getParameterMap();

System.out.println("========= IPN RECEIVE ==========");
for (String key:params.keySet()) {
    for (String val:params.get(key))
        System.out.println("  " + key + "=" + val);
}
%>