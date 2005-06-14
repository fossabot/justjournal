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

import com.justjournal.db.DateTimeBean;
import com.justjournal.db.RssCacheDao;
import com.justjournal.db.RssCacheTo;
import org.apache.log4j.Category;

import java.net.URL;


/**
 * Stores RSS content collected from the internet into a datastore,
 * retrieves stored versions, and spits out HTML to render them.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: laffer1
 * Date: Apr 27, 2005
 * Time: 8:15:45 PM
 *
 * @author Lucas Holt
 * @version 1.0
 */
public final class CachedHeadlineBean
        extends HeadlineBean {

    private static Category log = Category.getInstance(CachedHeadlineBean.class.getName());

    protected void getRssDocument(final String uri)
            throws Exception {

        if (log.isDebugEnabled())
            log.debug("Starting getRssDocument()");

        RssCacheDao dao = new RssCacheDao();
        RssCacheTo rss;
        final java.util.GregorianCalendar calendarg = new java.util.GregorianCalendar();

        rss = dao.view(uri);

        if (rss != null && rss.getUri() != null && rss.getUri().length() > 10) {
            if (log.isDebugEnabled())
                log.debug("Retrieved uri from database: " + uri);

            document = builder.parse(rss.getContent());

            DateTimeBean dt = rss.getLastUpdated();

            if (dt.getYear() != calendarg.get(java.util.Calendar.YEAR)) {
                u = new URL(uri);
                inputXML = u.openStream();

                rss.setContent(inputXML.toString());
                dao.update(rss);
            }

            if (dt.getMonth() != calendarg.get(java.util.Calendar.MONTH)) {
                u = new URL(uri);
                inputXML = u.openStream();

                rss.setContent(inputXML.toString());
                dao.update(rss);
            }

            if (dt.getYear() != calendarg.get(java.util.Calendar.DATE)) {
                u = new URL(uri);
                inputXML = u.openStream();

                rss.setContent(inputXML.toString());
                dao.update(rss);
            }

            if (log.isDebugEnabled())
                log.debug("Hit end.. no date change.");


        } else {
            String rssDoc;

            if (log.isDebugEnabled())
                log.debug("Fetch uri: " + uri);

            //Open the file for reading:
            u = new URL(uri);
            inputXML = u.openStream();

            builder = factory.newDocumentBuilder();
            document = builder.parse(inputXML);

            rssDoc = document.toString();
            log.debug(rssDoc);

            try {
                rss.setUri(uri);
                rss.setInterval(24);
                rss.setContent(rssDoc);
                dao.add(rss);
            } catch (java.lang.NullPointerException n) {
                if (log.isDebugEnabled())
                    log.debug("Null pointer exception creating/adding rss cache object to db.");
            }

        }
    }

}
