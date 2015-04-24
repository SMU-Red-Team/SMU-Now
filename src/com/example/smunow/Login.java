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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Login extends Activity{
	private Session s;
	private Button login;
	private EditText email;
	private EditText password;
	private String em;
	private String pass;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		s = Session.getSession();
		login = (Button)findViewById(R.id.loginButton);
		email = (EditText)findViewById(R.id.emailGetter);
		password = (EditText)findViewById(R.id.passwordGetter);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(password.getText().toString().length() < 1)
					Toast.makeText(Login.this, "No password entered", Toast.LENGTH_LONG).show();
				else{
					pass = password.getText().toString();
					em = email.getText().toString();
					new authenticate().execute();
				}
			}
		});

	}
	class authenticate extends AsyncTask<Void, Void, Void> {
		private httpHandler jParse;
		private ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			jParse = s.getParser();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Confirming Identity ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("request","login"));
			params.add(new BasicNameValuePair("email", em));
			params.add(new BasicNameValuePair("password", pass));
			String json = jParse.makeRequest("http://52.10.7.245/api.php", "POST", params);
			Log.d("Returned string", json);
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jArr != null){
				try {
					JSONObject jObj = new JSONObject(jArr.getString(0));
					s.setUid(jObj.getInt("userID"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();

			//updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					if(s.getUid() !=  0){
						startActivity(new Intent(Login.this, tabs.class));
						finish();
					}
					else{
						password.setText("");
						Toast.makeText(getBaseContext(),"Not the correct Email/Password Combo", Toast.LENGTH_LONG).show();
					}
				}
			});

		}
	}

}
