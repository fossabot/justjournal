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

package com.justjournal.search;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Lucas Holt
 * @version $Id: BaseSearch.java,v 1.6 2009/05/16 03:15:27 laffer1 Exp $
 */
@Component
public class BaseSearch {
    private static final Logger log = Logger.getLogger(BaseSearch.class);

    protected ArrayList<String> terms = new ArrayList<String>();
    protected ArrayList<String> fieldlist = new ArrayList<String>();
    protected int maxresults = 30;
    protected String baseQuery;
    protected String sort;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BaseSearch(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setMaxResults(final int results) {
        maxresults = results;
    }

    public void setBaseQuery(final String base) {
        if (base != null && base.length() > 0)
            baseQuery = base;
    }

    public void setFields(final String fields) {
        final String q[] = fields.split("\\s");
        fieldlist.addAll(Arrays.asList(q));
    }

    public void setSortAscending(final String field) {
        if (field != null && field.length() > 0)
            sort = "ORDER BY " + field;
    }

    public void setSortDescending(final String field) {
        if (field != null && field.length() > 0)
            sort = "ORDER BY " + field + " DESC";
    }

    public List<Map<String, Object>> search(final String query) {
        if (log.isDebugEnabled()) {
            log.debug("search() called with " + query);
        }
        final List<Map<String, Object>> result;
        parseQuery(query);

        result = realSearch(terms);

        return result;
    }

    protected void parseQuery(final String query) {
        final String q[] = query.split("\\s");
        final int qLen = java.lang.reflect.Array.getLength(q);

        for (int i = 0; i < qLen; i++) {
            if (!(q[i].equalsIgnoreCase("and") ||
                    (q[i].contains("*") ||
                            q[i].contains(";") ||
                            q[i].contains("|"))))
                terms.add(q[i]);
        }
    }

    protected List<Map<String, Object>> realSearch(final List<String> terms) {

        String sqlStmt = baseQuery;

        for (int i = 0; i < terms.size(); i++) {
            sqlStmt += " (";
            for (int y = 0; y < fieldlist.size(); y++) {
                if (y != 0)
                    sqlStmt += " or ";
                sqlStmt += fieldlist.get(y) + " like '%" + terms.get(i) + "%'";
            }
            sqlStmt += ") and ";
        }

        sqlStmt += " 1=1 " + sort + " LIMIT 0," + maxresults + ";";

        try {
            if (log.isDebugEnabled()) {
                log.debug("realSearch() called on " + sqlStmt);
            }

            return jdbcTemplate.queryForList(sqlStmt);

        } catch (final Exception e) {
            log.error("Error executing search with query: " +  sqlStmt + "; and error " + e.getMessage(), e);
        }

        return null;
    }
}
