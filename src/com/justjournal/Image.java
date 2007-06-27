/*
Copyright (c) 2006, Lucas Holt
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

import com.justjournal.db.SQLHelper;
import com.justjournal.utility.ServletUtilities;
import org.apache.log4j.Category;
import sun.jdbc.rowset.CachedRowSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Image viewer servlet to display userpics and other images
 * from the database.
 * <p/>
 * User: laffer1
 * Date: Nov 22, 2005
 * Time: 9:31:28 PM
 *
 * @version $Id: Image.java,v 1.9 2007/06/27 20:20:24 laffer1 Exp $
 */
public final class Image extends HttpServlet {

    private static final Category log = Category.getInstance(Image.class.getName());

    // processes get requests
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Integer id;

        try {
            id = new Integer(request.getParameter("id"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (id.intValue() < 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            response.reset();
            response.setHeader("Expires", ServletUtilities.createExpiresHeader(180));
            CachedRowSet rs = SQLHelper.executeResultSet("call getimage(" + id + ");");
            if (rs.next()) {
                response.setContentType(rs.getString("mimetype").trim());
               BufferedInputStream img = new BufferedInputStream(rs.getBinaryStream("image"));
                byte[] buf = new byte[4 * 1024]; // 4k buffer
                int len;
                while ((len = img.read(buf, 0, buf.length)) != -1)
                    baos.write(buf, 0, len);

                response.setContentLength(baos.size());
                final ServletOutputStream outstream = response.getOutputStream();
                baos.writeTo(outstream);
                outstream.flush();
                outstream.close();
            } else
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

            rs.close();
        } catch (Exception e) {
            log.debug("Could not load image: " + e.toString());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
