package com.cloudbees.gasp.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.model.UserAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by markprichard on 7/15/13.
 */
public class UserListActivity extends ListActivity {
    private UserAdapter userAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasp_database_list);

        userAdapter = new UserAdapter(this);
        userAdapter.open();

        List<User> users = userAdapter.getAll();
        Collections.reverse(users);

        // Use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<User> adapter =
                new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users);
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        userAdapter.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        userAdapter.close();
        super.onPause();
    }
}