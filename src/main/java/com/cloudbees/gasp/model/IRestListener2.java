package com.cloudbees.gasp.model;

/**
 * Created by markprichard on 7/24/13.
 */
public interface IRestListener2 {
    void onCompletedAll(String results);
    void onCompletedIndex(String result);
}
