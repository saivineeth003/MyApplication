package com.example.eshwanth.myapplication;

abstract class GetUserCallback {

    /**
     * Invoked when background task is completed
     */

    public abstract void done(User returnedUser);
}
