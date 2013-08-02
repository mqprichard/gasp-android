package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.model.UserAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 8/2/13.
 */
public class UserSyncServiceTest extends ServiceTestCase<UserSyncService> {
    private static final String TAG = UserSyncServiceTest.class.getName();

    UserAdapter userAdapter;
    CountDownLatch signal;

    public UserSyncServiceTest() {
        super(UserSyncService.class);
        signal = new CountDownLatch(1);
    }

    private void cleanDatabase() {
        UserAdapter userAdapter = new UserAdapter(getContext());
        userAdapter.open();
        try {
            List<User> userList = userAdapter.getAll();
            for (User user : userList) {
                userAdapter.deleteUser(user);
            }
        }
        catch (Exception e) {}
        finally {
            userAdapter.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
    }

    public void testUserSyncIntent () throws InterruptedException {
        startService(new Intent(getContext(), UserSyncService.class));

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
