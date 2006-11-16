<%@ page contentType="text/html; charset=iso-8859-1" language="java"
         import="com.justjournal.User, com.justjournal.db.*" %>
<%@ page import="com.justjournal.utility.StringUtil" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%
    int eid;
    boolean nopermission = false;

    try {
        eid = new Integer(request.getParameter("id")).intValue();
    } catch (NumberFormatException ex1) {
        eid = 0;
    }

    if (eid == 0) {
        response.reset();
        response.sendError(500, "bad entry id");
        response.flushBuffer();
    }

    String aUser = (String) session.getAttribute("auth.user");

    Collection comments = CommentDao.view(eid);

    EntryTo entry = EntryDAO.viewSingle(eid, false);

    User pf = new User(entry.getUserName());

    // user wants it private, has comments disabled for this entry
    // or the security level is private.
    if (pf.isPrivateJournal() ||
            !entry.getAllowComments() ||
            entry.getSecurityLevel() == 0) {
        entry = new EntryTo();
        nopermission = true;
    }
%>
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title><%=entry.getUserName()%>: <%=entry.getSubject()%></title>
    <link rel="stylesheet" type="text/css" href="../layout.css" media="all"/>
    <link rel="stylesheet" type="text/css" href="../font-normal.css" media="all"/>
    <link rel="home" title="Home" href="../index.jsp"/>
    <link rel="contents" title="Site Map" href="../sitemap.jsp"/>
    <link rel="help" title="Technical Support" href="../support/index.jsp"/>
</head>

<body>

<jsp:include page="../header.inc" flush="false"/>

<div id="content">

    <jsp:include page="../inc_login.jsp" flush="false"/>

    <% if (nopermission) { %>
    <p>The entry is private or does not allow comments.</p>
    <% } else { %>

    <p>
        <img src="../images/userclass_16.png" alt="user"/><a href="../users/<%=entry.getUserName()%>"
                                                             title="<%=entry.getUserName()%>">
        <%=entry.getUserName()%></a>

        <% DateTime dte = entry.getDate(); %>
        wrote @ <%=dte.toPubDate()%>
    </p>

    <h3><%=entry.getSubject()%></h3>
    <%

        if (entry.getAutoFormat()) {
            String tmpBody = entry.getBody();
    %>
    <p>
        <% if (tmpBody.indexOf("\n") > -1) { %>
        <%=(StringUtil.replace(tmpBody, '\n', "<br />"))%>
        <% } else if (entry.getBody().indexOf("\r") > -1) { %>
        <%=(StringUtil.replace(tmpBody, '\r', "<br />"))%>
        <% } else {
            // we do not have any "new lines" but it might be
            // one long line.
        %>
        <%=tmpBody%>
        <% } %>
    </p>
    <% } else { %>
    <%=entry.getBody()%>
    <% } %>

    <div class="commentcount">
        <%=entry.getCommentCount()%> comments
    </div>

    <div class="rightflt">
        <a href="add.jsp?id=<%=eid%>" title="Add Comment">Add Comment</a>
    </div>

    <%
        CommentTo o;
        final Iterator itr = comments.iterator();

        for (int i = 0, n = comments.size(); i < n; i++) {
            o = (CommentTo) itr.next();

    %>
    <div class="comment">

        <div class="chead">
            <h3><span class="subject"><%=o.getSubject()%></span></h3>
            <img src="../images/userclass_16.png" alt="user"/>
            <a href="../users/<%=o.getUserName()%>" title="<%=o.getUserName()%>">
                <%=o.getUserName()%>
            </a>

            <br/><span class="time"><%=o.getDate().toPubDate()%></span>

            <%
                if (aUser != null && aUser.equalsIgnoreCase(o.getUserName())) {
            %>
            <br/><span class="actions">
				    <a href="edit.h?commentId=<%=o.getId()%>" title="Edit Comment">
                        <img src="../images/compose-message.png" alt="Edit Comment" width="24" height="24"/>
                    </a>

                    <a href="delete.h?commentId=<%=o.getId()%>" title="Delete Comment">
                        <img src="../images/stock_calc-cancel.png" alt="Delete Comment" width="24" height="24"/>
                    </a>
                    </span>
            <% } %>
        </div>

        <p><%=o.getBody()%></p>
    </div>
    <%
            } // end for loop
        } // end nopermission if
    %>
</div>

<jsp:include page="../footer.inc" flush="false"/>

</body>
</html>