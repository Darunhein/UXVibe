<%@ page import="mx.edu.utez.uxvibe.security.CsrfTokens" %>
<input type="hidden" name="_csrf" value="<%= CsrfTokens.get(request) %>" />
