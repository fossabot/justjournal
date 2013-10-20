/*
 * Copyright (c) 2013 Lucas Holt
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

package com.justjournal.ctl;

import com.justjournal.WebLogin;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Lucas Holt
 */
@Controller
@RequestMapping("/UploadAvatarSubmit")
public class AvatarController {
    private static final Logger log = Logger.getLogger(AvatarController.class);

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
     String post(HttpSession session, HttpServletRequest req, HttpServletResponse response) throws Exception {
           log.debug("Begin inside perform");
           int RowsAffected = 0;
           boolean isMultipart = FileUpload.isMultipartContent(req);

           if (isMultipart) {
               log.debug("we have a multipart file upload");
               DiskFileUpload upload = new DiskFileUpload();

               // set limits
               upload.setSizeMax(15 * 1024);
               upload.setSizeThreshold(15 * 1024);
               upload.setRepositoryPath("/tmp");  // should be changed.

               // process request
               List /*FileItem*/ items = upload.parseRequest(req);

               for (Object item1 : items) {
                   FileItem item = (FileItem) item1;

                   if (!item.isFormField()) {
                         // we're a file
                       //String fieldName = item.getFieldName();
                       //String fileName = item.getName();
                       String contentType = item.getContentType();
                       //boolean isInMemory = item.isInMemory();
                       long sizeInBytes = item.getSize();

                       // must be large enough
                       if (sizeInBytes > 500) {
                           byte[] data = item.get();

                           Context ctx;
                           DataSource ds;
                           Connection conn = null;
                           PreparedStatement stmt = null; // create statement
                           PreparedStatement stmtOn = null; // turn on avatar preference.
                           PreparedStatement stmtRemove = null; // delete old ones

                           try {
                               ctx = new InitialContext();
                               ds = (DataSource) ctx.lookup("java:comp/env/jdbc/jjDB");
                           } catch (Exception e) {
                               log.error(e.getMessage());
                               response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                               return "Could not retrieve database connection";
                           }

                           try {
                               conn = ds.getConnection();

                               stmtRemove = conn.prepareStatement("DELETE FROM user_pic WHERE id=? LIMIT 1");
                               stmtRemove.setInt(1, WebLogin.currentLoginId(session));
                               stmtRemove.execute();

                               // do the create of the image
                               stmt = conn.prepareStatement("INSERT INTO user_pic (id, date_modified, mimetype, image) VALUES(?,Now(),?,?)");
                               stmt.setInt(1, WebLogin.currentLoginId(session));
                               stmt.setString(2, contentType);
                               stmt.setBytes(3, data);
                               stmt.execute();
                               RowsAffected = stmt.getUpdateCount();
                               stmt.close();

                               // turn on avatars.
                               stmtOn = conn.prepareStatement("UPDATE user_pref set show_avatar=? WHERE id=? LIMIT 1");
                               stmtOn.setString(1, "Y");
                               stmtOn.setInt(2, WebLogin.currentLoginId(session));
                               stmtOn.execute();

                               if (stmtOn.getUpdateCount() != 1)
                                   log.debug("error turning on avatar.");
                               stmtOn.close();

                               conn.close();
                           } catch (Exception e) {
                               log.debug(e.getMessage());
                               throw new Exception("Error getting connect or executing it", e);
                           } finally {
                               /*
                               * Close any JDBC instances here that weren't
                               * explicitly closed during normal code path, so
                               * that we don't 'leak' resources...
                               */

                               try {
                                   stmt.close();
                               } catch (SQLException sqlEx) {
                                   // ignore -- as we can't do anything about it here
                                   log.debug(sqlEx.getMessage());
                               }

                               try {
                                   stmtOn.close();
                               } catch (SQLException sqlEx) {
                                   // ignore -- as we can't do anything about it here
                                   log.debug(sqlEx.getMessage());
                               }

                               try {
                                   stmtRemove.close();
                               } catch (SQLException sqlEx) {
                                   // ignore -- as we can't do anything about it here
                                   log.debug(sqlEx.getMessage());
                               }

                               try {
                                   conn.close();
                               } catch (SQLException sqlEx) {
                                   // ignore -- as we can't do anything about it here
                                   log.debug(sqlEx.getMessage());
                               }
                           }
                       } else {
                           log.error("File size is too small");
                           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                           return "File size is too small.";
                       }
                   }
               }
           }

           if ( RowsAffected != 1) {
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               return "Error Saving Document";
           }
           else
               return "OK";
       }
}