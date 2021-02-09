package com.example.contactsapp;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class Application extends android.app.Application
{
    public static final String APPLICATION_ID = "DD3F39AB-8060-724F-FF8C-D2861B514900";
    public static final String API_KEY = "4EC6EECC-2482-4A5F-8B8B-A55989A49EF7";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> contacts; // getting a connection from the updated list from backendless


    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }
}
