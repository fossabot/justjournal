/*
Copyright (c) 2007, Lucas Holt
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

import com.justjournal.metaweblog.MetaWeblog;
import com.justjournal.google.Blogger;
import redstone.xmlrpc.XmlRpcServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * XML RPC endpoints for blogger and metaweblog legacy APIs.
 *
 * @author Lucas Holt
 */
final public class XmlRpc extends XmlRpcServlet {
    private static final String BLOGGER = "blogger";
    private static final String METAWEBLOG = "metaWeblog";

    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        getXmlRpcServer().addInvocationHandler(BLOGGER, new Blogger());   // This is blogger 1.0 api which is no longer used by Google, but still popular with older blogging clients.
        getXmlRpcServer().addInvocationHandler(METAWEBLOG, new MetaWeblog());
    }
}
