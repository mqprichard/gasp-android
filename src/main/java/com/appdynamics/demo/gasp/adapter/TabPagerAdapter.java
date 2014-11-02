package com.appdynamics.demo.gasp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appdynamics.demo.gasp.fragment.RestaurantsFragment;
import com.appdynamics.demo.gasp.fragment.ReviewsFragment;
import com.appdynamics.demo.gasp.fragment.UsersFragment;

/**
 * Copyright (c) 2013 Mark Prichard
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

public class TabPagerAdapter extends FragmentPagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return new RestaurantsFragment();
            case 1: return new ReviewsFragment();
            case 2: return new UsersFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
