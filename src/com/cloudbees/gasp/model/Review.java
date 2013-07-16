package com.cloudbees.gasp.model;

/**
 * Model class for Gasp! Reviews. The class is designed to be populated via a JSON call
 * to the Gasp! REST server: restaurant_id an user_id are parsed from the return data
 * to ensure that the on-device SQLite Database matches the gcm_demo Gasp! database.
 *
 * +---------------+--------------+------+-----+---------+----------------+
 * | Field         | Type         | Null | Key | Default | Extra          |
 * +---------------+--------------+------+-----+---------+----------------+
 * | id            | int(11)      | NO   | PRI | NULL    | auto_increment |
 * | comment       | varchar(255) | YES  |     | NULL    |                |
 * | star          | int(11)      | YES  |     | NULL    |                |
 * | restaurant_id | int(11)      | NO   | MUL | NULL    |                |
 * | user_id       | int(11)      | NO   | MUL | NULL    |                |
 * +---------------+--------------+------+-----+---------+----------------+

 *
 * @author Mark Prichard
 */
public class Review {
    private int id;
    private String url;
    private int restaurant_id;
    private String restaurant;
    private int user_id;
    private String user;
    private int star;
    private String comment;

    private final int lenUsers = "/users/".length();
    private final int lenRestaurants = "/restaurants/".length();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurant_id() {
        return Integer.valueOf(this.getRestaurant().substring(lenRestaurants));
    }

    public void setRestaurant_id(int restaurant_id){
        this.restaurant_id = restaurant_id;
    }

    public int getUser_id() {
        return Integer.valueOf(this.getUser().substring(lenUsers));
    }

    public void setUser_id(int user_id){
        this.user_id = user_id;
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

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString(){
        return "Review #" + this.getId()
                + ": (" + this.getStar() + " Stars) " + this.getComment();
    }
}
