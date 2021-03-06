/*
 * Copyright (c) 2003-2011, 2013 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package com.justjournal;

import com.justjournal.model.PasswordType;
import com.justjournal.repository.UserRepository;
import com.justjournal.utility.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides authentication and password management services to web applications using the just journal data tier.
 * <p/>
 * Created on March 23, 2003, 2:34 PM
 *
 * @author Lucas Holt
 */
@Slf4j
@Component
public class Login {
    public static final int USERNAME_MAX_LENGTH = 15;
    public static final int PASSWORD_MAX_LENGTH = 18;
    public static final int BAD_USER_ID = 0;
    protected static final String LOGIN_ATTRNAME = "auth.user";
    protected static final String LOGIN_ATTRID = "auth.uid";

    @Autowired
    private UserRepository userRepository;

    public static boolean isAuthenticated(final HttpSession session) {
        final String username = (String) session.getAttribute("auth.user");
        return username != null && !username.isEmpty();
    }

    public

    static String currentLoginName(final HttpSession session) {
        return (String) session.getAttribute(LOGIN_ATTRNAME);
    }

    public

    static int currentLoginId(final HttpSession session) {
        int aUserID = 0;
        final Integer userIDasi = (Integer) session.getAttribute(LOGIN_ATTRID);

        if (userIDasi != null) {
            aUserID = userIDasi;
        }

        return aUserID;
    }

    protected static void logout(final HttpSession session) {
        session.removeAttribute(LOGIN_ATTRNAME);
        session.removeAttribute(LOGIN_ATTRID);
    }

    public static boolean isUserName(final CharSequence input) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_]+");
        final Matcher m = p.matcher(input);

        return m.matches(); // valid on true
    }

    /**
     * Check if a password is valid in terms of characters used.
     * @param input
     * @return
     */
    public static boolean isPassword(final CharSequence input) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_@.#$ ]+");
        final Matcher m = p.matcher(input);

        return m.matches(); // valid on true
    }

    private

    static String convertToHex(byte[] data) {
        final StringBuilder buf = new StringBuilder();
        for (final byte aData : data) {
            int halfByte = (aData >>> 4) & 0x0F;
            int twoHalves = 0;
            do {
                if ((0 <= halfByte) && (halfByte <= 9))
                    buf.append((char) ('0' + halfByte));
                else
                    buf.append((char) ('a' + (halfByte - 10)));
                halfByte = aData & 0x0F;
            } while (twoHalves++ < 1);   // TODO: wtf?
        }
        return buf.toString();
    }

    public static String sha1(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;

        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String sha256(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] sha2hash;

        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha2hash = md.digest();
        return convertToHex(sha2hash);
    }

    /**
     * Authenticate the user using clear text username and password.
     *
     * @param userName Three to Fifteen characters...
     * @param password Clear text password
     * @return user id of the user who logged in or 0 if the login failed.
     */
    public int validate(final String userName, final String password) {
        // the password is sha1 encrypted in the sql server

        if (!StringUtil.lengthCheck(userName, 3, USERNAME_MAX_LENGTH))
            return BAD_USER_ID; // bad username

        if (!StringUtil.lengthCheck(password, 5, PASSWORD_MAX_LENGTH))
            return BAD_USER_ID;

        if (!isUserName(userName))
            return BAD_USER_ID; // bad username

        if (!isPassword(password))
            return BAD_USER_ID; // bad password

        try {
            final int userId = lookupUserId(userName, password);
            setLastLogin(userId);
            return userId;
        } catch (final Exception e) {
            log.error("validate(): " + e.getMessage());
        }
        return BAD_USER_ID;
    }

    private int lookupUserId(final String userName, final String password) {
        final com.justjournal.model.User user = lookupUser(userName, password);
        if (user == null)
            return BAD_USER_ID;
        return user.getId();
    }

    public void setLastLogin(final int id) {
        /* verify its a real login */
        if (id < 1)
            return;

        try {
            final com.justjournal.model.User user = userRepository.findById(id).orElse(null);
            if (user == null)
                throw new IllegalArgumentException("id");

            user.setLastLogin(new java.util.Date());
            userRepository.saveAndFlush(user);
        } catch (final Exception e) {
            log.error("setLastLogin(): " + e.getMessage());
        }
    }

    /**
     * Change the password for an account given the username, old and new passwords.
     *
     * @param userName username
     * @param password existing password
     * @param newPass  new password
     * @return true if the password change worked.
     */
    public boolean changePass(final String userName,
                              final String password,
                              final String newPass) {

        final int uid;

        try {
            uid = validate(userName, password);

            if (uid > BAD_USER_ID && isPassword(newPass)) {
                final com.justjournal.model.User user = lookupUser(userName, password);
                user.setPassword(getHashedPassword(userName, newPass));
                user.setPasswordType(PasswordType.SHA256);
                userRepository.saveAndFlush(user);

                return true;
            }
        } catch (final Exception e) {
            log.error("changePass(): " + e.getMessage());
        }

        return false;
    }

    public static String getHashedPassword(final String userName, final String password) {
        try {
            return sha256(userName + password);
        } catch (final Exception e) {
            log.error("Invalid password hash algorithm?", e);
            throw new RuntimeException("Password hash not supported");
        }
    }

    private com.justjournal.model.User lookupUser(final String userName, final String password) {
        try {
            com.justjournal.model.User user;
            user = userRepository.findByUsernameAndPassword(userName, getHashedPassword(userName, password));
            if (user == null)
                user = userRepository.findByUsernameAndPassword(userName, sha1(password));

            return user;
        } catch (final Exception e) {
            log.error("Invalid password hash algorithm?", e);
            throw new RuntimeException("Password hash not supported");
        }
    }
}