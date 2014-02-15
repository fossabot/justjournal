/*
 * Copyright (c) 2014 Lucas Holt
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

package com.justjournal.repository;

import com.justjournal.Util;
import com.justjournal.model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

/**
 * @author Lucas Holt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/test/resources/mvc-dispatcher-servlet.xml")
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @BeforeClass
    public static void setup() throws Exception {
        Util.setupDb();
    }

    @Test
    public void list() throws Exception {
        Iterable<User> list = userRepository.findAll();
        assertNotNull(list);
        assertTrue(userRepository.count() > 0);
    }

    @Test
    public void getById() throws Exception {
        User user = userRepository.findOne(1);
        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    public void getByUsername() throws Exception {
        User user = userRepository.findByUsername("jjsite");
        assertNotNull(user);
        assertEquals(16, user.getId());
    }

    @Test
    public void getByUsernameInvalid() throws Exception {
        User user = userRepository.findByUsername("iamnotareal");
        assertNull(user);
    }

    @Test
    public void getByUserAndPasswordInvalid() {
        User user = userRepository.findByUsernameAndPassword("jjsite", "wrongpassword");
        assertNull(user);
    }

    @Test
    public void exists() {
        assertTrue(userRepository.exists(1));
    }
}