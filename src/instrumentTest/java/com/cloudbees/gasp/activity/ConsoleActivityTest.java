package com.cloudbees.gasp.activity;

import android.test.ActivityInstrumentationTestCase2;

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

public class ConsoleActivityTest extends ActivityInstrumentationTestCase2<ConsoleActivity> {
    static final String TAG = ConsoleActivityTest.class.getName();

    private ConsoleActivity mActivity;

    public ConsoleActivityTest() {
        super(ConsoleActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    public void testMainActivity() throws Throwable {
    }
}
