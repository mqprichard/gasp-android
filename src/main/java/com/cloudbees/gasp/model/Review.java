/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.model;

import com.google.gson.annotations.Expose;

public class Review {
    private int id;
    private String url;
    // Only expose these methods for HTTP POST calls to Gasp server
    // Requires GsonBuilder().excludeFieldsWithoutExposeAnnotation()
    @Expose
    private String restaurant;
    @Expose
    private String user;
    @Expose
    private int star;
    @Expose
    private String comment;

    private final int lenUsers = "/users/".length();
    private final int lenRestaurants = "/restaurants/".length();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.setUrl("/reviews/" + id);
    }

    public int getRestaurant_id() {
        return Integer.valueOf(this.getRestaurant().substring(lenRestaurants));
    }

    public void setRestaurant_id(int restaurant_id){
        this.setRestaurant("/restaurants/" + restaurant_id);
    }

    public int getUser_id() {
        return Integer.valueOf(this.getUser().substring(lenUsers));
    }

    public void setUser_id(int user_id){
        this.setUser("/users/" + user_id);
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString(){
        return "Review #" + this.getId()
                + ": (" + this.getStar() + " Stars) " + this.getComment();
    }
}
