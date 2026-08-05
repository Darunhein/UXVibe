<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String target = request.getContextPath() + "/participants";
  String queryString = request.getQueryString();
  if (queryString != null && !queryString.trim().isEmpty()) {
    target += "?" + queryString;
  }
  response.sendRedirect(target);
%>
