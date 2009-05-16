package com.justjournal.ctl;

import com.justjournal.JustJournalBaseServlet;
import com.justjournal.User;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * An implementation of Really Simple Discovery (RSD).
 * <p/>
 * A list of points that blogging clients can use to post entries by "autodiscovery".  This is used by blog clients
 * such as Microsoft's Live Writer.
 * <p/>
 * http://cyber.law.harvard.edu/blogs/gems/tech/rsd.html
 *
 * @author Lucas Holt
 * @version $Id: Rsd.java,v 1.5 2009/05/16 03:13:12 laffer1 Exp $
 *          <p/>
 *          User: laffer1
 *          Date: Apr 26, 2008
 *          Time: 10:22:20 AM
 */
public class Rsd extends JustJournalBaseServlet {

    private static final Logger log = Logger.getLogger(Rsd.class.getName());
    private static final String XML_HEADER =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    private static final String RSD_HEADER =
            "<rsd xmlns=\"http://archipelago.phrasewise.com/rsd\" version=\"1.0\">\n";
    private static final String RSD_FOOTER =
            "</rsd>\n";

    protected void execute(HttpServletRequest request, HttpServletResponse response, HttpSession session, StringBuffer sb) {
        try {
            response.setContentType("application/rsd+xml; charset=utf-8");

            String blogID = request.getParameter("blogID");
            if (blogID == null || blogID.length() < 2) {
                throw new IllegalArgumentException("Missing required parameter \"blogID\"");
            }

            User user = new User(blogID);

            sb.append(XML_HEADER);
            sb.append(RSD_HEADER);
            sb.append("<service>\n");
            sb.append("\t<engineName>JustJournal</engineName>\n");
            sb.append("\t<engineLink>http://www.justjournal.com</engineLink>\n");
            sb.append("\t<homePageLink>http://www.justjournal.com/users/");
            sb.append(user.getUserName()); // yeah we already know this but it's for output and thus safer.
            sb.append("</homePageLink>\n");
            // APIS we support.
            sb.append("\t<apis>\n");
            sb.append("\t\t<api name=\"Blogger\" preferred=\"false\" apiLink=\"http://www.justjournal.com/xml-rpc\" blogID=\"");
            sb.append(user.getUserName());
            sb.append("\" />\n");
            sb.append("\t\t<api name=\"MetaWeblog\" preferred=\"true\" apiLink=\"http://www.justjournal.com/xml-rpc\" blogID=\"");
            sb.append(user.getUserName());
            sb.append("\" />\n");
            sb.append("\t</apis>\n");
            sb.append("</service>\n");
            sb.append(RSD_FOOTER);

        } catch (Exception e) {
            sb.delete(0, sb.length() - 1);
            sb.append(XML_HEADER);
            sb.append(RSD_HEADER);
            sb.append(RSD_FOOTER);
            response.setStatus(500);
        }
    }

}
