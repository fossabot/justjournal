/*
Copyright (c) 2005-2006, Lucas Holt
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

package com.justjournal.db;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import com.justjournal.model.User;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.log4j.Logger;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;


/**
 * Access account information for a specific user or all users of Just Journal.
 *
 * @author Lucas Holt
 * @version 1.0
 * @since 1.0
 */
public final class UserDao {

    private static final Logger log = Logger.getLogger(UserDao.class);

    /**
     * Add a user to justjournal including their name, username and password.
     *
     * @param user User Instance
     * @return True if successful, false otherwise.
     */
    public static boolean add(UserTo user) {
        boolean noError = true;
        int records = 0;

        final String sqlStmt =
                "Insert INTO user (username,password,name,lastname) VALUES('"
                        + user.getUserName() + "',sha1('" + user.getPassword()
                        + "'),'" + user.getFirstName() + "','" + user.getLastName() + "');";

        try {
            records = SQLHelper.executeNonQuery(sqlStmt);
        } catch (Exception e) {
            noError = false;
        }

        if (records != 1)
            noError = false;

        return noError;
    }

    /**
     * edit the user record with a new first name. usernames can not be changed. passwords can be changed via the
     * <code>com.justjournal.ctl.ChangePasswordSubmit</code> class.
     *
     * @param user User Instance
     * @return true on success
     * @see com.justjournal.ctl.ChangePasswordSubmit
     */
    public static boolean update(UserTo user) {
        boolean noError = true;

        try {
            ObjectContext dataContext = DataContext.getThreadObjectContext();
            User u = Cayenne.objectForPK(dataContext, User.class, user.getId());
            u.setName(user.getFirstName());
            u.setLastname(user.getLastName());
            dataContext.commitChanges();
        } catch (Exception e) {
            noError = false;
        }

        // TODO: refactor so we don't do this in two calls.
        boolean sec = updateSecurity(user.getId(), user.getPrivateJournal());

        return noError && sec;
    }

    /**
     * Deletes a user from the database.  This should not be called by cancel account.  Accounts should be deactivated.
     *
     * @param userId Unique User ID
     * @return true if successful, false otherwise
     */
    public static boolean delete(int userId) {
        boolean noError = true;

        if (userId > 0) {
            try {
                ObjectContext dataContext = DataContext.getThreadObjectContext();
                User u = Cayenne.objectForPK(dataContext, User.class, userId);
                dataContext.deleteObject(u);
                dataContext.commitChanges();
            } catch (Exception e) {
                noError = false;
            }
        } else {
            noError = false;
        }

        return noError;
    }

    /**
     * Retrieve a user given the user id. Does NOT retrieve password or sha1 password.
     *
     * @param userId Unique User ID
     * @return user's info
     */
    public static UserTo view(final int userId) {
        UserTo user = new UserTo();

        try {
            ObjectContext dataContext = DataContext.getThreadObjectContext();
            User u = Cayenne.objectForPK(dataContext, User.class, userId);

            if (u != null) {
                user.setId(userId);
                user.setUserName(u.getUsername()); // username
                user.setName(u.getName()); // first name
                user.setSince(u.getSince());
                if (u.getLastlogin() != null) {
                    user.setLastLogin(u.getLastlogin());
                }
                if (u.getLastname() != null)
                    user.setLastName(u.getLastname());
            }
        } catch (Exception e1) {
            log.error(e1);
        }
        return user;

    }

    /**
     * Retrieve a user give the user name. Does not retrieve the password or sha1 password.
     *
     * @param userName User's username
     * @return user's info
     */
    @SuppressWarnings("unchecked")
    public static UserTo view(final String userName) {
        UserTo user = new UserTo();

        try {
            ObjectContext dataContext = DataContext.getThreadObjectContext();
            Expression exp = Expression.fromString("username = $user");
            final SelectQuery query = new SelectQuery(User.class, exp).queryWithParameters(Collections.singletonMap("user", userName));
            List<User> userList = dataContext.performQuery(query);

            if (!userList.isEmpty()) {
                User u = userList.get(0);

                user.setId(Cayenne.intPKForObject(u));
                user.setUserName(userName);
                user.setName(u.getName()); // first name
                user.setSince(u.getSince());
                if (u.getLastlogin() != null) {
                    user.setLastLogin(u.getLastlogin());
                }
                if (u.getLastname() != null)
                    user.setLastName(u.getLastname());
            }
        } catch (Exception e1) {
            log.error(e1);
        }
        return user;
    }

    public static UserTo viewWithPassword(final String userName) {
           UserTo user = new UserTo();

           try {
               ObjectContext dataContext = DataContext.getThreadObjectContext();
               Expression exp = Expression.fromString("username = $user");
               final SelectQuery query = new SelectQuery(User.class, exp).queryWithParameters(Collections.singletonMap("user", userName));
               List<User> userlist = dataContext.performQuery(query);

               if (!userlist.isEmpty()) {
                   User u = userlist.get(0);

                   user.setId(Cayenne.intPKForObject(u));
                   user.setUserName(userName);
                   user.setName(u.getName()); // first name
                   user.setSince(u.getSince());
                   if (u.getLastlogin() != null) {
                       user.setLastLogin(u.getLastlogin());
                   }
                   if (u.getLastname() != null)
                       user.setLastName(u.getLastname());
                   user.setPasswordSha1(u.getPassword());
               }
           } catch (Exception e1) {
               log.error(e1);
           }
           return user;
       }

    /**
     * Retrieve the list of all users including their name, username, sign up year (since), and unique id.
     *
     * @return All users of just journal.
     */
    public static Collection<UserTo> memberList() {
        ArrayList<UserTo> users = new ArrayList<UserTo>(1024);
        UserTo usr;
        final String sqlStatement = "call memberlist();";

        try {
            final ResultSet RS = SQLHelper.executeResultSet(sqlStatement);

            while (RS.next()) {
                usr = new UserTo();
                usr.setId(RS.getInt(1));
                usr.setUserName(RS.getString(2));
                usr.setName(RS.getString(3));
                usr.setSince(RS.getInt(4));
                usr.setLastName(RS.getString(5));
                users.add(usr);
            }

            RS.close();
        } catch (Exception e1) {
            log.error(e1);
        }

        return users;
    }

    /**
     * Retrieve the last five users to sign up.
     *
     * @return collection of UserTo's.
     */
    public static Collection<UserTo> newUsers() {
        ArrayList<UserTo> users = new ArrayList<UserTo>(5);
        UserTo usr;
        final String sqlStatement = "SELECT id, username, name, since, lastname FROM user ORDER by id DESC Limit 0,5;";

        try {
            final ResultSet RS = SQLHelper.executeResultSet(sqlStatement);

            while (RS.next()) {
                usr = new UserTo();
                usr.setId(RS.getInt(1));
                usr.setUserName(RS.getString(2));
                usr.setName(RS.getString(3));
                usr.setSince(RS.getInt(4));
                usr.setLastName(RS.getString(5));
                users.add(usr);
            }

            RS.close();
        } catch (Exception e1) {
            log.error(e1);
        }

        return users;
    }

    public static Collection<String> friends(String username) {
        ArrayList<String> friends = new ArrayList<String>();
        final String sql = "SELECT friends.friendid as fif, (SELECT user.username from user WHERE user.id=fif) FROM friends, user WHERE user.username=\"" + username + "\" AND user.id=friends.id;";

        try {
            final ResultSet RS = SQLHelper.executeResultSet(sql);

            while (RS.next()) {
                friends.add(RS.getString(2));
            }

            RS.close();
        } catch (Exception e1) {
            log.error(e1);
        }

        return friends;
    }

    public static Collection<String> friendsof(String username) {
        ArrayList<String> friends = new ArrayList<String>();
        final String sql = "SELECT friends.id as fif, (SELECT user.username from user WHERE user.id=fif) FROM friends, user WHERE user.username=\"" + username + "\" AND user.id=friends.friendid;";

        try {
            final ResultSet RS = SQLHelper.executeResultSet(sql);

            while (RS.next()) {
                friends.add(RS.getString(2));
            }

            RS.close();
        } catch (Exception e1) {
            log.error(e1);
        }

        return friends;
    }

      /**
     * Retrieves the journal preferences for a certain user including
     * style information, and privacy settings.
     *
     * @param userName the user who needs their settings defined.
     * @return Preferences in cached rowset.
     * @throws Exception SQL exception
     */
    @Deprecated
    public static ResultSet getJournalPreferences(final String userName)
            throws Exception {
        ResultSet RS;

        if (userName == null)
            throw new IllegalArgumentException("Missing username.");

        if (userName.length() < 3)
            throw new IllegalArgumentException("Username must be at least 3 characters");

        String sqlStatement =
                "SELECT user.name, user.id, user.since, up.style, up.allow_spider, " +
                        "up.owner_view_only, st.url, st.doc, uc.email, " +
                        "up.show_avatar, up.journal_name, ubio.content, up.ping_services FROM user, user_bio as ubio, user_pref As up, user_style as st, user_contact As uc " +
                        "WHERE user.username='" + userName + "' AND user.id = up.id AND user.id=st.id AND user.id=uc.id AND user.id = ubio.id LIMIT 1;";

        try {
            RS = SQLHelper.executeResultSet(sqlStatement);
        } catch (Exception e1) {
             log.error(e1.getMessage());
            throw new Exception("Couldn't get preferences: " + e1.getMessage());
        }

        return RS;
    }

    /**
     * Update the owner view only security feature.
     *
     * @param userId  user id of blog owner
     * @param ownerOnly  if the blog is private
     * @return true on success, false on any error
     */
    public static boolean updateSecurity(int userId, boolean ownerOnly) {
        boolean noError = true;
        int records = 0;
        String ownerview = "N";

        if (ownerOnly)
            ownerview = "Y";

        final String sqlStmt = "Update user_pref SET owner_view_only='" + ownerview
                + "' WHERE id='" + userId + "' LIMIT 1;";

        try {
            records = SQLHelper.executeNonQuery(sqlStmt);
        } catch (Exception e) {
            noError = false;
            log.error(e.getMessage());
        }

        if (records != 1)
            noError = false;

        return noError;
    }
}
