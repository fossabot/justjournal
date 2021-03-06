/*
 * Copyright (c) 2013, 2014 Lucas Holt
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

package com.justjournal.ctl.api.entry;

import com.justjournal.Login;
import com.justjournal.model.*;
import com.justjournal.model.api.EntryTo;
import com.justjournal.repository.*;
import com.justjournal.services.EntryService;
import com.justjournal.services.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Entry Controller, for managing blog entries
 *
 * @author Lucas Holt
 */
@Slf4j
@RestController
@RequestMapping("/api/entry")
public class EntryController {

    private static final int DEFAULT_SIZE = 20;
    private static final String ERR_INVALID_LOGIN = "The login timed out or is invalid.";
    private static final String ERR_TYPE = "error";

    @Qualifier("commentRepository")
    @Autowired
    private CommentRepository commentDao = null;

    @Qualifier("entryRepository")
    @Autowired
    private EntryRepository entryRepository = null;

    @Qualifier("securityRepository")
    @Autowired
    private SecurityRepository securityDao;

    @Qualifier("locationRepository")
    @Autowired
    private LocationRepository locationDao;

    @Qualifier("moodRepository")
    @Autowired
    private MoodRepository moodDao;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntryService entryService;


    /**
     * Get the private list of recent blog entries. If logged in, get the private list otherwise only public entries.
     * /api/entry/{username}/recent
     *
     * @param username username
     * @param response HttpResponse
     * @param session  HttpSession
     * @return list of recent entries
     */
    @GetMapping(value = "{username}/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RecentEntry>> getRecentEntries(@PathVariable("username") final String username,
                                                              final HttpServletResponse response, final HttpSession session) {
        final io.reactivex.Observable<RecentEntry> entries;
        try {
            if (Login.isAuthenticated(session) && Login.isUserName(username)) {
                entries = entryService.getRecentEntries(username);
            } else {
                entries = entryService.getRecentEntriesPublic(username);
            }

            final List<RecentEntry> e = entries.toList().blockingGet();

            return ResponseEntity
                    .ok()
                    //     .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                    .eTag(Integer.toString(e.hashCode()))
                    .body(e);
        } catch (final ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "{username}/size/{size}/page/{page}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Entry> getEntries(@PathVariable("username") final String username,
                                  @PathVariable("size") final int size,
                                  @PathVariable("page") final int page,
                                  final HttpServletResponse response, final HttpSession session) {
        final Page<Entry> entries;
        final Pageable pageable = new PageRequest(page, size, new Sort(
                new Sort.Order(Sort.Direction.DESC, "date")
        ));
        try {
            if (Login.isAuthenticated(session) && Login.isUserName(username)) {
                entries = entryService.getEntries(username, pageable);
            } else {
                entries = entryService.getPublicEntries(username, pageable);
            }

            if (entries == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            return entries;
        } catch (final ServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @GetMapping(value = "{username}/page/{page}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Entry> getEntries(@PathVariable("username") final String username, @PathVariable("page") final int page,
                                  final HttpServletResponse response, final HttpSession session) {

        return getEntries(username, DEFAULT_SIZE, page, response, session);
    }

    /**
     * Get an individual entry
     *
     * @param id entry id
     * @return entry
     */

    @GetMapping(value = "{username}/eid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Entry getById(@PathVariable("username") final String username,
                         @PathVariable("id") final int id,
                         final HttpServletResponse response) {
        try {
            final Entry entry = entryRepository.findById(id).orElse(null);

            if (entry == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            if (entry.getUser().getUsername().equalsIgnoreCase(username)) {
                if (entry.getSecurity().getId() == 2) // public
                    return entry;
                else
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    /**
     * Get all the blog entries for a user (public) /api/entry/username
     *
     * @param username
     * @param response
     * @return
     */
    @GetMapping(value = "{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<Entry> getEntries(@PathVariable("username") final String username,
                                        final HttpServletResponse response) {
        Collection<Entry> entries = null;
        try {
            entries = entryService.getPublicEntries(username);

            if (entries == null || entries.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return Collections.emptyList();
            }

        } catch (final ServiceException e) {
            log.error(e.getMessage(), e);
        }
        return entries;
    }

    @Transactional
    @GetMapping(value = "", params = "username", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<Entry> getEntriesByUsername(@RequestParam("username") final String username,
                                                  final HttpServletResponse response) {
        Collection<Entry> entries = new ArrayList<>();
        log.warn("in entriesByUsername with " + username);

        if (username == null || username.isEmpty()) {
            log.trace("Username was null or empty ");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return entries;
        }

        try {
            entries = entryService.getPublicEntries(username);

            if (entries == null || entries.isEmpty()) {
                log.warn("entries is null or empty");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (final Exception e) {
            log.error("Unable to get public entries. " + e.getMessage(), e);
        }

        return entries;
    }


    /**
     * Creates a new entry resource
     *
     * @param entryTo  EntryTo
     * @param session  HttpSession
     * @param response HttpServletResponse
     * @return status ok or error
     */
    @CacheEvict(value = "recentblogs", allEntries = true)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> post(@RequestBody final EntryTo entryTo,
                                    final HttpSession session,
                                    final HttpServletResponse response,
                                    final Model model) {

        if (!Login.isAuthenticated(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Collections.singletonMap(ERR_TYPE, "The login timed out or is invalid.");
        }

        final User user = userRepository.findById(Login.currentLoginId(session)).orElse(null);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.singletonMap(ERR_TYPE, "User not found");
        }

        final Entry entry = new Entry();
        if (entryTo.getBody() == null || entryTo.getBody().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.singletonMap(ERR_TYPE, "Entry does not contain a body.");
        }
        entry.setBody(entryTo.getBody());
        log.trace("Entry contains body " + entryTo.getBody());

        entry.setSubject(entryTo.getSubject());

        entry.setUser(user);

        entry.setLocation(locationDao.findById(entryTo.getLocation()).orElse(null));
        entry.setSecurity(securityDao.findById(entryTo.getSecurity()).orElse(null));

        if (entryTo.getMood() == 0)
            entryTo.setMood(12); // DEFAULT NOT SPECIFIED
        entry.setMood(moodDao.findById(entryTo.getMood()).orElse(null));

        entry.setDraft(entryTo.getDraft() ? PrefBool.Y : PrefBool.N);
        entry.setAllowComments(entryTo.getAllowComments() ? PrefBool.Y : PrefBool.N);
        if (entryTo.getFormat().equals("MARKDOWN")) {
            entry.setFormat(FormatType.MARKDOWN);
            entry.setAutoFormat(PrefBool.N);
        } else if (entryTo.getFormat().equals("HTML")) {
            entry.setFormat(FormatType.HTML);
            entry.setAutoFormat(PrefBool.N);
        } else {
            entry.setFormat(FormatType.TEXT);
            entry.setAutoFormat(PrefBool.Y);
        }
     
        if (entryTo.getDate() == null)
            entry.setDate(new Date());
        else
            entry.setDate(entryTo.getDate());

        final Entry saved = entryRepository.saveAndFlush(entry);

        if (saved.getId() < 1) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return Collections.singletonMap(ERR_TYPE, "Could not save entry");
        }

        entryService.applyTags(saved, entryTo.getTags());

        model.addAttribute("status", "ok");
        model.addAttribute("id", saved.getId());

        final HashMap<String, String> map = new HashMap<>();
        map.put("status", "ok");
        map.put("id", Integer.toString(saved.getId()));
        return map;
    }

    /**
     * PUT generally allows for add or edit in REST.
     *
     * @param entryTo  User's Blog entry
     * @param session  HttpSession
     * @param response HttpServletResponse
     * @return
     */
    @CacheEvict(value = "recentblogs")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> put(@RequestBody EntryTo entryTo, HttpSession session, HttpServletResponse response) {
        if (!Login.isAuthenticated(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Collections.singletonMap(ERR_TYPE, ERR_INVALID_LOGIN);
        }
        final User user = userRepository.findById(Login.currentLoginId(session)).orElse(null);
        Entry entry = new Entry();
        entry.setUser(user);

        entry.setSubject(entryTo.getSubject());
        entry.setBody(entryTo.getBody());

        entry.setLocation(locationDao.findById(entryTo.getLocation()).orElse(null));
        entry.setSecurity(securityDao.findById(entryTo.getSecurity()).orElse(null));
        entry.setMood(moodDao.findById(entryTo.getMood()).orElse(null));

        if (entryTo.getFormat().equals("MARKDOWN")) {
            entry.setFormat(FormatType.MARKDOWN);
            entry.setAutoFormat(PrefBool.N);
        } else if (entryTo.getFormat().equals("HTML")) {
            entry.setFormat(FormatType.HTML);
            entry.setAutoFormat(PrefBool.N);
        } else {
            entry.setFormat(FormatType.TEXT);
            entry.setAutoFormat(PrefBool.Y);
        }

        if (entryTo.getDate() == null)
            entry.setDate(Calendar.getInstance().getTime());
        else
            entry.setDate(entryTo.getDate());

        final Entry entry2 = entryRepository.findById(entryTo.getId()).orElse(null);

        if (entry2 != null && entry2.getId() > 0 && entry2.getUser().getId() == user.getId()) {
            entry.setId(entry2.getId());
        }

        entry = entryRepository.save(entry);
        entryService.applyTags(entry, entryTo.getTags());

        return Collections.singletonMap("id", Integer.toString(entry.getId()));
    }

    /**
     * @param entryId  entry id
     * @param session  HttpSession
     * @param response HttpServletResponse
     * @return errors or entry id if success
     */
    @CacheEvict(value = "recentblogs")
    @DeleteMapping(value = "/{entryId}")
    @ResponseBody
    public Map<String, String> delete(@PathVariable("entryId") final int entryId,
                                      final HttpSession session, final HttpServletResponse response) {

        if (!Login.isAuthenticated(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Collections.singletonMap(ERR_TYPE, ERR_INVALID_LOGIN);
        }

        if (entryId < 1)
            return Collections.singletonMap(ERR_TYPE, "The entry id was invalid.");

        try {
            final User user = userRepository.findById(Login.currentLoginId(session)).orElse(null);
            final Entry entry = entryRepository.findById(entryId).orElse(null);

            if (user != null && entry != null && user.getId() == entry.getUser().getId()) {
                final Iterable<Comment> comments = entry.getComments();
                commentDao.deleteAll(comments);
                entryRepository.deleteById(entryId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return Collections.singletonMap(ERR_TYPE, "Could not delete entry.");
            }

            return Collections.singletonMap("id", Integer.toString(entryId));
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.singletonMap(ERR_TYPE, "Could not delete the comment.");
        }
    }
}
