package com.cloudbees.demo.gasp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class SearchResult implements Serializable {
    private List<Place> mPlaces;

    public List<Place> getPlaces() {
        return mPlaces;
    }

    public SearchResult() {
        mPlaces = new ArrayList<Place>();
    }

    public SearchResult(List<Place> placeList) {
        mPlaces = placeList;
    }

    public SearchResult(Place[] places) {
        mPlaces = new ArrayList<Place>(Arrays.asList(places));
    }

    public void add(Places places) {
        for (Place place: places.getResults()) {
            mPlaces.add(place);
        }
    }
}
