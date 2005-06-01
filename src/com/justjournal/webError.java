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

/*
 * webError.java
 *
 * Created on March 23, 2003, 1:09 PM
 */

package com.justjournal;

import java.io.PrintWriter;

/**
 * Prints out an error message in HTML.
 *
 * @author Lucas Holt
 * @version 1.1
 * @since 1.0
 */
public final class webError {

    static void Display(final String ErrTitle, final String ErrMsg, final PrintWriter ResponseWriter) {
        StringBuffer sb = new StringBuffer();

        Display(ErrTitle, ErrMsg, sb);  // call the other version

        ResponseWriter.write(sb.toString());

    }

    static void Display(final String ErrTitle, final String ErrMsg, final StringBuffer sb) {
        if (sb.length() > 0) {
            // reset the output to display the error.
            sb.delete(0, sb.length() - 1);
        }

        // Head
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        sb.append("<head>\n");
        sb.append("<title>");
        sb.append(ErrTitle);
        sb.append("</title>\n");
        sb.append("</head>\n");

        // Body
        sb.append("<body style=\"margin: 0;\">\n");

        sb.append("<div style=\"width: 100%; height: 100px; margin-top: 1in; margin-left: 0; margin-right: 0; position relative; text-align: center; background: orange; color: white;\">\n");
        sb.append("<h1 style=\"font: 72pt Arial, Helvetica, sans-serif; letter-spacing: .2in;\">" + ErrTitle + "</h1>\n");
        sb.append("</div>\n");

        sb.append("<div style=\"margin: 1in; font: 12pt Arial, Helvetica, sans-serif;\">\n");
        sb.append("<p>");
        sb.append(ErrMsg);
        sb.append("</p>\n");
        sb.append("</div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");
    }

}