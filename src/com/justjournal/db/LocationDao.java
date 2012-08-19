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

package com.justjournal.db;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * View all locations
 *
 * @author Lucas Holt User: laffer1 Date: Jan 9, 2004 Time: 1:54:42 PM
 */
public final class LocationDao {
    private static final Logger log = Logger.getLogger(LocationDao.class.getName());

    public static Collection<LocationTo> view() {
        ObjectContext dataContext = DataContext.getThreadObjectContext();
        ArrayList<LocationTo> locations = new ArrayList<LocationTo>(4);
        LocationTo loc;

        try {
            SelectQuery query = new SelectQuery(com.justjournal.model.Location.class);
            List<Ordering> orderings = new ArrayList<Ordering>();
            orderings.add(new Ordering("title", SortOrder.ASCENDING));
            query.addOrderings(orderings);
            List<com.justjournal.model.Location> locationList = dataContext.performQuery(query);

            for (com.justjournal.model.Location location : locationList) {
                loc = new LocationTo();
                loc.setId(Cayenne.intPKForObject(location));
                loc.setName(location.getTitle());
            }
        } catch (Exception e1) {
            log.error(e1);
        }

        return locations;
    }

}
