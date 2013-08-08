package com.cloudbees.gasp.model;

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
 *
 * Model class for Gasp! Restaurants. The class is designed to be populated via a JSON call
 * to the Gasp! REST server: restaurant_id an user_id are parsed from the return data
 * to ensure that the on-device SQLite Database matches the gcm_demo Gasp! database.
 *
 * +---------+--------------+------+-----+---------+----------------+
 * | Field   | Type         | Null | Key | Default | Extra          |
 * +---------+--------------+------+-----+---------+----------------+
 * | id      | int(11)      | NO   | PRI | NULL    | auto_increment |
 * | address | varchar(255) | YES  |     | NULL    |                |
 * | name    | varchar(255) | YES  |     | NULL    |                |
 * | website | varchar(255) | YES  |     | NULL    |                |
 * +---------+--------------+------+-----+---------+----------------+
 *
 * @author Mark Prichard
 */
public class Restaurant {
    private int id;
    private String name;
    private String website;
    private String address;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.setUrl("/restaurants/" + id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString(){
        return "Restaurant #" + this.getId() + ": "
                + this.getName() + " (" + this.getWebsite() + ")";
    }
}
