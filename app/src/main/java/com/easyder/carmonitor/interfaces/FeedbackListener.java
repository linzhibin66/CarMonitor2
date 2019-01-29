package com.easyder.carmonitor.interfaces;

/**
 * Created by ljn on 2017-04-21.
 */
public interface FeedbackListener {

    void onBack();

    void feedbackCommit(String message);
}
