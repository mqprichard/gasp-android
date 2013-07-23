package com.cloudbees.gasp.model;

/**
 * Model class for Gasp! Users. The class is designed to be populated via a JSON call
 * to the Gasp! REST server: restaurant_id an user_id are parsed from the return data
 * to ensure that the on-device SQLite Database matches the gcm_demo Gasp! database.
 *
 * +-------+--------------+------+-----+---------+----------------+
 * | Field | Type         | Null | Key | Default | Extra          |
 * +-------+--------------+------+-----+---------+----------------+
 * | id    | int(11)      | NO   | PRI | NULL    | auto_increment |
 * | name  | varchar(255) | YES  |     | NULL    |                |
 * +-------+--------------+------+-----+---------+----------------+
 *
 * @author Mark Prichard
 */
public class User {
    private int id;
    private String name;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.setUrl("/users/" + id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString(){
        return "User #" + this.getId() + ": " + this.getName();
    }
}
