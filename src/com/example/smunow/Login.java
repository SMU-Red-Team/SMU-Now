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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")

//Creates and manages Login screen
public class Login extends Activity{
	private Session s;
	private Button login;
	private EditText email, password;
	private String em, pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//sets up private data
		s = Session.getSession();
		login = (Button)findViewById(R.id.loginButton);
		email = (EditText)findViewById(R.id.emailGetter);
		password = (EditText)findViewById(R.id.passwordGetter);
		
		//waits for login button to be pressed
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				//only executes authentication if password is entered
				if(password.getText().toString().length() < 1)
					Toast.makeText(Login.this, "No password entered", Toast.LENGTH_LONG).show();
				else{
					pass = password.getText().toString();
					em = email.getText().toString();
					s.destroySession();
					s = Session.getSession();
					new authenticate().execute();
				}
				
			}
		});

	}
	
	//class authenticates the users login information asynchronously
	class authenticate extends AsyncTask<Void, Void, Void> {
		private httpHandler jParse;
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			jParse = s.getParser();
			
			//creates and shows a dialog box to inform user
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Confirming Identity ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... args) {
			
			//creates the parameters array to post information
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("request","login"));
			params.add(new BasicNameValuePair("email", em));
			params.add(new BasicNameValuePair("password", pass));
			String json = jParse.makeRequest("http://52.10.7.245/api.php", "POST", params);
			
			//attempts to break the returned string into a JSON Array
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//if the parsing was successful, get information
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
			
			//updates the content on the main thread
			runOnUiThread(new Runnable() {
				public void run() {
					if(s.getUid() !=  0){
						s.setUnVisited();
						startActivity(new Intent(Login.this, tabs.class));
					}
					else{
						password.setText("");
						Toast.makeText(getBaseContext(),"Not the correct Email/Password Combo", Toast.LENGTH_SHORT).show();
					}
				}
			});

		}
	}

}
