package com.example.smunow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//Basic activity that an APP uses, simply redirects to tabs
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//starts the tab activity immediately
		startActivity(new Intent(MainActivity.this, tabs.class));
	}
}