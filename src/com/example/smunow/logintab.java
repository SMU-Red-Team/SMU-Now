package com.example.smunow;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")

//handles creating the tabs for the login/registration screen
public class logintab extends TabActivity{
	private static final String LOGIN_SPEC = "Log In";
	private static final String REGISTER_SPEC = "Register";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout);
		TabHost tabHost = getTabHost();
		
		//creates login tab
		TabSpec loginSpec = tabHost.newTabSpec(LOGIN_SPEC);
		View loginView = LayoutInflater.from(logintab.this).inflate(R.layout.logintab, null);
		loginSpec.setIndicator(loginView);
		Intent loginIntent = new Intent(logintab.this, Login.class);
		loginSpec.setContent(loginIntent);
		
		//creates Register tab
		TabSpec registerSpec = tabHost.newTabSpec(REGISTER_SPEC);
		View registerView = LayoutInflater.from(logintab.this).inflate(R.layout.registertab, null);
		registerSpec.setIndicator(registerView);
		Intent registerIntent = new Intent(logintab.this, Register.class);
		registerSpec.setContent(registerIntent);

		// Adding tabs to view
		tabHost.addTab(loginSpec); 
		tabHost.addTab(registerSpec);
	}
}
