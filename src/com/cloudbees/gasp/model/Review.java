package com.cloudbees.gasp.model;

/**
 * Model class for Gasp! Reviews. The class is designed to be populated via a JSON call
 * to the Gasp! REST server: the id, restaurant_id an user_id are parsed from the return
 * data to ensure that the on-device SQLite Database matches the main Gasp! database.
 *
 * @author Mark Prichard
 */
public class Review {
    int id;
    int restaurant_id;
    int user_id;
    int star;
    String comment;
    String restaurant;
    String user;
    String url;

    final String pathReviews = "/reviews/";
    final int lenReviews = "/reviews/".length();
    final String pathUsers = "/users/";
    final int lenUsers = "/users/".length();
    final String pathRestaurants = "/restaurants/";
    final int lenRestaurants = "/restaurants/".length();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
        this.setRestaurant(pathRestaurants + restaurant_id);
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
        this.setUser(pathUsers + user_id);
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
        setRestaurant_id(Integer.valueOf(this.getRestaurant().substring(lenRestaurants)));
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
        setUser_id(Integer.valueOf(this.getUser().substring(lenUsers)));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        setId(Integer.valueOf(this.getUrl().substring(lenReviews)));
    }
}
