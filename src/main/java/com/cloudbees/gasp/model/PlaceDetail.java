package com.cloudbees.gasp.model;

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

/**
 * User: Mark Prichard (mprichard@cloudbees.com))
 * Date: 8/28/13
 */
public class PlaceDetail {
    private String name;
    private String website;
    private String formatted_address;
    private String formatted_phone_number;
    private String international_phone_number;
    private Geometry geometry;
    private String id;

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getId() {
        return id;
    }
}
