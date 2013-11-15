package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.adapter.UserAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class UserSyncServiceTest extends ServiceTestCase<UserSyncService> {
    private static final String TAG = UserSyncServiceTest.class.getName();

    private UserAdapter userAdapter;
    private CountDownLatch signal;

    public UserSyncServiceTest() {
        super(UserSyncService.class);
    }

    private void cleanDatabase() {
        UserAdapter userAdapter = new UserAdapter(getContext());
        userAdapter.open();
        try {
            List<User> userList = userAdapter.getAll();
            for (User user : userList) {
                userAdapter.deleteUser(user);
            }
        } catch (Exception e) {
        } finally {
            userAdapter.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
        signal = new CountDownLatch(1);
    }

    public void testUserSyncIntent() throws InterruptedException {
        startService(new Intent(getContext(), UserSyncService.class));

        // Allow 20 secs for the async REST call to complete
        signal.await(20, TimeUnit.SECONDS);

        try {
            userAdapter = new UserAdapter(getContext());
            userAdapter.open();

            List<User> users = userAdapter.getAll();
            assertTrue(users.size() > 0);
        } finally {
            userAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
