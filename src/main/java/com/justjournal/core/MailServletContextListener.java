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
package com.justjournal.core;

import com.justjournal.utility.MailSender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Mail Sender Context Listener runs mail sender code continuously on the servlet engine.
 * @author Lucas Holt
 * @version $Id: MailServletContextListener.java,v 1.5 2006/10/14 00:29:03 laffer1 Exp $
 * @since 1.0
 */
public class MailServletContextListener
        implements ServletContextListener {

    Thread m;

    public MailServletContextListener() {

    }

    public void contextInitialized(ServletContextEvent sce) {
        //ServletContext sc = sce.getServletContext();
        m = new MailSender();
        m.start();
        System.out.println("MailServletContextListener:" +
                "contextInitialized.");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        m.interrupt();
        System.out.println("MailServletContextListener:" +
                "contextDestroyed.");
    }
}