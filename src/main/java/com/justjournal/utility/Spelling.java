/*
Copyright (c) 2003-2006, 2014 Lucas Holt
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

package com.justjournal.utility;

import com.swabunga.spell.engine.GenericSpellDictionary;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Frontend to Jazzy Spell Check engine. Generates XHTML 1.0 output to spell check queries.
 *
 * @author Lucas Holt
 * @version 1.1
 * @since 1.0 User: laffer1 Date: Sep 19, 2003 Time: 6:11:18 PM
 */
public final class Spelling
        implements SpellCheckListener {
    private Logger log = LoggerFactory.getLogger(Spelling.class);

    private static final String dictFile = "/usr/local/dict/english.0";
    private SpellChecker spellCheck;
    private StringBuilder sb; // output variable

    /**
     * Creates an instance of Spell dictionary and reads the dictionary file from disk.
     */
    public Spelling() {
        try {
            com.swabunga.spell.engine.GenericSpellDictionary dictionary = new GenericSpellDictionary(new File(dictFile));
            // old version SpellDictionary dictionary = new SpellDictionary(new File(dictFile));

            spellCheck = new SpellChecker(dictionary);
            spellCheck.addSpellCheckListener(this);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * Action to perform when a spelling error occurs. In this case we output HTML markup to display the error.
     *
     * @param event A reference to the spell check event (word)
     */
    public void spellingError(SpellCheckEvent event) {
        final List suggestions = event.getSuggestions();
        if (suggestions.size() > 0) {
            sb.append("<p><span style=\"color:red\">");
            sb.append(event.getInvalidWord());
            sb.append("</span> ");

            // The counter fixes a bug where the , appears in the
            // output after the last word or after the only word.
            // this looks like crap.
            int i = 0;
            for (Iterator suggestedWord = suggestions.iterator();
                 suggestedWord.hasNext(); i++) {
                if (i > 0)
                    sb.append(",&nbsp;");

                sb.append(suggestedWord.next());
            }

            sb.append("</p>\n");

        }
        // Null actions since this is event based we probably
        // dont want to do anything if a word is "OK"
    }

    /**
     * Used to check the spelling of the given string.
     *
     * @param inText Text to check
     * @return String containing errors found in document.
     */
    public String checkSpelling(String inText) {
        sb = new StringBuilder();

        try {
            spellCheck.checkSpelling(new StringWordTokenizer(inText));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return sb.toString();
    }

}
