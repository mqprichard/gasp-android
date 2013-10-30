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

public class Query {
    private double lat;
    private double lng;
    private int radius;
    private String next_page_token = "";

    private String name = "";
    private String addressString = "";

    private String reference = "";

    public Query(double lat, double lng, int radius, String next_page_token) {
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.next_page_token = next_page_token;
    }

    public Query(String reference) {
        this.reference = reference;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getRadius() {
        return radius;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public String getName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public String getAddressString() {
        return addressString;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}