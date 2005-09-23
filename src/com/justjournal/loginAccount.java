/*
Copyright (c) 2005, Lucas Holt
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

  Redistributions of source code must retain the above copyright notice, this list of
  conditions and the following disclaimer.

  Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or other
  materials provided with the distribution.

  Neither the name of the Just Journal nor the names of its contributors
  may be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package com.justjournal;

import org.apache.log4j.Category;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Login account servlet.
 *
 * @author Lucas Holt
 * @version 1.3
 *          Sat Jun 07 2003
 *          <p/>
 *          Version 1.1 changes to a stringbuffer for output.
 *          This should improve performance a bit.
 *          <p/>
 *          1.2 fixed a bug with NULL pointer exceptions.
 *          <p/>
 *          Mon Sep 19 2005
 *          1.3 added JJ.LOGIN.FAIL and JJ.LOGIN.OK for desktop
 *          clients.
 *          <p/>
 *          TODO CONVERT TO MAVERICK CODE
 * @since JJ 1.0
 */
public final class loginAccount extends HttpServlet {
    private static final char endl = '\n';
    private static Category log = Category.getInstance(loginAccount.class.getName());
    private static final String JJ_LOGIN_OK = "JJ.LOGIN.OK";
    private static final String JJ_LOGIN_FAIL = "JJ.LOGIN.FAIL";

    /**
     * Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    private void htmlOutput(StringBuffer sb, String userName) {
        // Begin HTML document.
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append(endl);
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append(endl);
        sb.append("<head>");
        sb.append(endl);
        sb.append("<title>JustJournal.com: Login</title>");
        sb.append(endl);
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" title=\"default\" href=\"/layout1.css\">");
        sb.append(endl);
        sb.append("</head>");
        sb.append(endl);

        sb.append("<body>");
        sb.append(endl);

        sb.append("<!-- Header: Begin -->");
        sb.append(endl);
        sb.append("<div style=\"padding-bottom: 30px;\" id=\"header\">");
        sb.append(endl);
        sb.append("<span style=\"float: left; top: -14px; position: relative;\">");
        sb.append("<img style=\" margin-top: 30px; margin-left: 30px;\" src=\"/images/jj-pencil3.gif\" />");
        sb.append("</span>");
        sb.append(endl);
        sb.append("<img style=\"left: -50px; top: -120px; position: relative;\" alt=\"Blogging for life\" src=\"/images/bloglife.gif\" />");
        sb.append("<img style=\"margin-top: 54px; margin-left: -120px;\" height=\"100\" width=\"445\" alt=\"Just Journal\" src=\"/images/jjheader2.gif\" />");
        sb.append("</div>");
        sb.append(endl);
        sb.append("<!-- Header: End -->");
        sb.append(endl);

        sb.append("\t<!-- Menu: Begin -->");
        sb.append(endl);
        sb.append("\t<div id=\"menu\">");
        sb.append(endl);

        sb.append("\t<ul>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/users/" + userName + "\">recent entries</a></li>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/users/" + userName + "/calendar\">Calendar</a></li>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/users/" + userName + "/friends\">Friends</a></li>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/profile.jsp?user=" + userName + "\">Profile</a></li>");
        sb.append(endl);
        sb.append("\t</ul>");
        sb.append(endl);

        // General stuff...
        sb.append("\t<ul>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/update.jsp\">Update Journal</a></li>");
        sb.append(endl);

        // User is logged in.. give them the option to log out.
        sb.append("\t\t<li><a href=\"/prefs/index.jsp\">Preferences</a></li>");
        sb.append(endl);
        sb.append("\t\t<li><a href=\"/logout.jsp\">Log Out</a></li>");
        sb.append(endl);
        sb.append("\t</ul>");
        sb.append(endl);

        sb.append("\t<p>RSS Syndication<br /><br />");
        sb.append("<a href=\"/users/");
        sb.append(userName);
        sb.append("/rss\"><img src=\"/img/v4_xml.gif\" alt=\"RSS content feed\" /> Recent</a><br />");
        sb.append("<a href=\"/users/");
        sb.append(userName);
        sb.append("/subscriptions\">Subscriptions</a>");
        sb.append("\t</p>");
        sb.append(endl);

        sb.append("\t</div>");
        sb.append(endl);
        sb.append("\t<!-- Menu: End -->\n");
        sb.append(endl);

        // END MENU

        sb.append("<div id=\"content\">");
        sb.append(endl);
        sb.append("\t<h2>Login</h2>");
        sb.append(endl);
        sb.append("\t<p><strong>You are logged in as " + userName + ".</strong></p>");
        sb.append(endl);
        sb.append("</div>");
        sb.append(endl);

        sb.append("</body>");
        sb.append(endl);
        sb.append("</html>");
        sb.append(endl);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        final StringBuffer sb = new StringBuffer();
        final HttpSession session = request.getSession(true);

        response.setContentType("text/html");
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setDateHeader("Last-Modified", System.currentTimeMillis());
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        boolean blnError = false;

        // Validate the login
        int userID;
        String userName = request.getParameter("username").toLowerCase();
        String password = request.getParameter("password");
        String passwordHash = request.getParameter("password_hash").trim().toLowerCase();

        String userAgent = request.getHeader("User-Agent");
        boolean webClient = true;  // browser

        if (userAgent != null && userAgent.indexOf("JustJournal") > -1)
            webClient = false; // desktop client.. win/mac

        if (userName == null || userName.length() < 3) {
            blnError = true;
            if (webClient)
                webError.Display("Input Error",
                        "username must be at least 3 characters.",
                        sb);
            else
                sb.append(JJ_LOGIN_FAIL);
        }

        if (passwordHash == null && password == null) {
            blnError = true;
            if (webClient)
                webError.Display("Input Error",
                        "Please input a password.",
                        sb);
            else
                sb.append(JJ_LOGIN_FAIL);
        }

        if (blnError == false) {
            try {
                if (log.isDebugEnabled())
                    log.debug("Attempting Login Validation  ");

                if (passwordHash != null && passwordHash != "") {
                    if (log.isDebugEnabled())
                        log.debug("Using SHA1 pass=" + passwordHash);

                    userID = webLogin.validateSHA1(userName, passwordHash);
                } else {
                    if (log.isDebugEnabled())
                        log.debug("Using clear pass=" + password);

                    userID = webLogin.validate(userName, password);
                }

                if (userID > 0) {
                    if (!webClient) {
                        sb.append(JJ_LOGIN_OK);
                    } else {
                        session.setAttribute("auth.uid", new Integer(userID));
                        session.setAttribute("auth.user", userName);

                        htmlOutput(sb, userName);
                    }
                } else {
                    if (webClient)
                        webError.Display("Authentication Error",
                                "Unable to login.  Please check your username and password.",
                                sb);
                    else
                        sb.append(JJ_LOGIN_FAIL);
                }
            } catch (Exception e3) {
                if (webClient)
                    webError.Display("Authentication Error",
                            "Unable to login.  Please check your username and password.",
                            sb);
                else
                    sb.append(JJ_LOGIN_FAIL);
            }
        }

        if (log.isDebugEnabled())
            log.debug("Write out Response  ");
        // output the result of our processing
        final ServletOutputStream outstream = response.getOutputStream();
        outstream.println(sb.toString());
        outstream.flush();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "login to journal service";
    }

}
