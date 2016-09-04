package com.justjournal.ctl;

import com.justjournal.ErrorPage;
import com.justjournal.model.Journal;
import com.justjournal.model.User;
import com.justjournal.repository.UserRepository;
import com.justjournal.utility.FileIO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: laffer1 Date: Mar 31, 2007 Time: 11:34:00 AM
 */
@Component
public class DeletePicture extends JustJournalBaseServlet {
    private static final Logger log = Logger.getLogger(AddPicture.class.getName());
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected void execute(HttpServletRequest request, HttpServletResponse response, HttpSession session, StringBuffer sb) {
        boolean blnError = false;

        // Retreive username
        String userName;
        userName = (String) session.getAttribute("auth.user");

        // Retreive user id
        Integer userIDasi = (Integer) session.getAttribute("auth.uid");
        // convert Integer to int type
        int userID = 0;
        if (userIDasi != null) {
            userID = userIDasi.intValue();
        }

        /* Make sure we are logged in */
        if (userID < 1) {
            ErrorPage.Display("Error", "You must be logged in to upload a picture.", sb);
            return;
        }

        String picIDstr = request.getParameter("id");
        if (picIDstr == null) {
            ErrorPage.Display("Error", "A picture id must be provided.", sb);
            return;
        }

        Integer picID = Integer.parseInt(picIDstr);
        if (picID.intValue() < 1) {
            ErrorPage.Display("Error", "Invalid picture id.", sb);
            return;
        }

        try {
            jdbcTemplate.execute("DELETE FROM user_images WHERE id='" + picID.intValue() + "' AND owner='" + userID + "';");
        } catch (DataAccessException dae) {
            log.error(dae.getMessage());
            blnError = true;
        }

        if (blnError) {
            ErrorPage.Display("Database",
                    "Database Error.  Please try again later.",
                    sb);
        } else
            htmlOutput(sb, userName);
    }

    private void htmlOutput(StringBuffer sb, String userName) {

        try {
            String template = FileIO.ReadTextFile("/home/jj/docs/journal_template.inc");
            String loginMenu;
            StringBuilder content = new StringBuilder();
            User user = userRepository.findByUsername(userName);

            content.append("\t\t<h2>Preferences</h2>");
            content.append(endl);
            content.append("\t<p>You are logged in as <a href=\"/users/").append(userName).append("\"><img src=\"/images/userclass_16.png\" alt=\"user\" />").append(userName).append("</a>.</p>");
            content.append(endl);
            content.append("\t<h3>Remove Picture</h3>");
            content.append(endl);
            content.append("\t<p>Picture Deleted.</p>");
            content.append(endl);
            content.append("\t<p><a href=\"/prefs/index.jsp\">Return to preferences.</a></p>");
            content.append(endl);

            // User is logged in.. give them the option to log out.
            loginMenu = ("\t\t<a href=\"/prefs/index.jsp\">Preferences</a><br />");

            loginMenu += ("\t\t<a href=\"/logout.jsp\">Log Out</a>");

            final Journal journal = user.getJournals().get(0);
            template = template.replaceAll("\\$JOURNAL_TITLE\\$", journal.getName());
            template = template.replaceAll("\\$USER\\$", userName);
            template = template.replaceAll("\\$USER_STYLESHEET\\$", journal.getStyle().getId() + ".css");
            template = template.replaceAll("\\$USER_STYLESHEET_ADD\\$", "");
            template = template.replaceAll("\\$USER_AVATAR\\$", "");
            template = template.replaceAll("\\$RECENT_ENTRIES\\$", "");
            template = template.replaceAll("\\$LOGIN_MENU\\$", loginMenu);
            template = template.replaceAll("\\$CONTENT\\$", content.toString());
            sb.append(template);
        } catch (Exception e) {
            ErrorPage.Display("Internal Error", "Error dislaying page. " + e.getMessage(), sb);
        }
    }

    public String getServletInfo() {
        return "Remove Picture";
    }
}
