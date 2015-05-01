package com.example.smunow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")

//handles all actions and attributes for the registration screen
public class Register extends Activity{

	private Session s;
	private Button register;
	private EditText email, password, confirm;
	private String e, pass;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		//sets up all private data and layout attributes
		s= Session.getSession();
		register = (Button)findViewById(R.id.register);
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);
		password.setTypeface(Typeface.SERIF);
		password.setTransformationMethod(new PasswordTransformationMethod());
		confirm = (EditText)findViewById(R.id.confirm);
		confirm.setTypeface(Typeface.SERIF);
		confirm.setTransformationMethod(new PasswordTransformationMethod());

		//waits for the register button to be clicked
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				//determines if the passwords match and if they are legitimate passwords
				if(password.getText().toString().compareTo(confirm.getText().toString()) !=0 || password.getText().toString().length() < 1)
					Toast.makeText(Register.this, "Please enter Valid Passwords", Toast.LENGTH_LONG).show();
				else{
					pass = password.getText().toString();
					e = email.getText().toString();
					s.destroySession();
					s = Session.getSession();
					registered();
				}
			}
		});
	}
	public void registered() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		//only executes download if there is a connection to the internet
		if (networkInfo != null && networkInfo.isConnected()){
			new register().execute();
		}
		else 
			Toast.makeText(Register.this, "Could not connect to the internet", Toast.LENGTH_SHORT).show();
	}
	//asynchronous task used to create a user account
	class register extends AsyncTask<Void, Void, Void> {

		private httpHandler jParse;
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			jParse = s.getParser();

			//shows the user a message that their account is being created
			pDialog = new ProgressDialog(Register.this);
			pDialog.setMessage("Creating Your Account ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... args) {

			//sets up parameters for posting
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("request","register"));
			params.add(new BasicNameValuePair("email", e));
			params.add(new BasicNameValuePair("password", pass));
			String json = jParse.makeRequest("http://52.10.7.245/api.php", "POST", params);

			//parses returned string into a JSONArray if possible
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//if it was possible to create an array, then the users ID is extracted
			if(jArr != null){
				try {
					JSONObject jObj = new JSONObject(jArr.getString(0));
					s.setUid(jObj.getInt("userID"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
		@Override
		protected void onPostExecute(Void v) {
			pDialog.dismiss();

			//runs code on main thread depending on ability to create account
			runOnUiThread(new Runnable() {
				public void run() {

					//if a User ID was returned then the account creation was successful
					if(s.getUid() !=  0){
						startActivity(new Intent(Register.this,tabs.class));
						finish();
					}
					else{

						//resets password fields and makes them try again
						confirm.setText("");
						password.setText("");
						Toast.makeText(getBaseContext(),"That Email is already in use", Toast.LENGTH_LONG).show();
					}
				}
			});

		}
	}

}
