package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.model.UserAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class UserUpdateServiceTest extends ServiceTestCase<UserUpdateService> {
    private static final String TAG = UserUpdateServiceTest.class.getName();

    private UserAdapter userAdapter;
    private CountDownLatch signal;

    public UserUpdateServiceTest() {
        super(UserUpdateService.class);
        signal = new CountDownLatch(1);
    }

    private void cleanDatabase() {
        UserAdapter userData = new UserAdapter(getContext());
        userData.open();
        try {
            List<User> userList = userData.getAll();
            for (User user : userList) {
                userData.deleteUser(user);
            }
        }
        catch(Exception e){}
        finally {
            userData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
        signal = new CountDownLatch(1);
    }

    public void testUserUpdateIntent () throws InterruptedException {
        startService(new Intent(getContext(), UserUpdateService.class)
                .putExtra(SyncIntentParams.PARAM_ID, 1));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        try {
            userAdapter = new UserAdapter(getContext());
            userAdapter.open();

            List<User> users = userAdapter.getAll();
            assertTrue(users.size() > 0);
        }
        finally {
            userAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
