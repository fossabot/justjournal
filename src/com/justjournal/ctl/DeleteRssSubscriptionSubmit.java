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

package com.justjournal.ctl;

import com.justjournal.db.RssSubscriptionsDAO;
import com.justjournal.db.RssSubscriptionsTO;
import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: laffer1
 * Date: Mar 28, 2005
 * Time: 5:33:44 PM
 */
public class DeleteRssSubscriptionSubmit extends Protected {
    private static Category log =
            Category.getInstance(AddRssSubscriptionSubmit.class.getName());
    protected String uri;  // lj username

    public String getUri() {
        return uri;
    }

    public void setUserName(String uri) {
        this.uri = uri;
    }

    protected String insidePerform() throws Exception {
        if (log.isDebugEnabled())
            log.debug("Loading DAO Objects  ");

        RssSubscriptionsDAO dao = new RssSubscriptionsDAO();
        RssSubscriptionsTO to = new RssSubscriptionsTO();
        boolean result;


        if (uri == null || uri.length() < 10 || uri.length() > 1024)
            addError("uri", "The RSS feed URI is invalid.");

        if (this.currentLoginId() < 1)
            addError("login", "The login timed out or is invalid.");

        if (this.hasErrors() == false) {
            to.setId(this.currentLoginId());
            to.setUri(this.uri);

            result = dao.delete(to);

            if (result == false)
                addError("Unknown", "Could not delete subscription.");
        }

        if (this.hasErrors())
            return ERROR;
        else
            return SUCCESS;
    }

}
