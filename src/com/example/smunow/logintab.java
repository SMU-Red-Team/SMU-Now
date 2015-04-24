package com.example.smunow;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class logintab extends TabActivity{
	private static final String LOGIN_SPEC = "Log In";
	private static final String REGISTER_SPEC = "Register";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablayout);
        
        TabHost tabHost = getTabHost();
        
        // Twitter Tab
        TabSpec loginSpec = tabHost.newTabSpec(LOGIN_SPEC);
        loginSpec.setIndicator(LOGIN_SPEC);
        Intent loginIntent = new Intent(logintab.this, Login.class);
        loginSpec.setContent(loginIntent);
       
        // Event Stream Tab
        TabSpec registerSpec = tabHost.newTabSpec(REGISTER_SPEC);
        registerSpec.setIndicator(REGISTER_SPEC);
        Intent registerIntent = new Intent(logintab.this, Register.class);
        registerSpec.setContent(registerIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(loginSpec); 
        tabHost.addTab(registerSpec); 
    }
}
