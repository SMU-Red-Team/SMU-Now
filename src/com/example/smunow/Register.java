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
public class Register extends Activity{
	private Session s;
	private Button register;
	private EditText email;
	private EditText password;
	private EditText confirm;
	private String e;
	private String pass;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		s= Session.getSession();
		register = (Button)findViewById(R.id.register);
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);
		confirm = (EditText)findViewById(R.id.confirm);
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(password.getText().toString().compareTo(confirm.getText().toString()) !=0 || password.getText().toString().length() < 1)
					Toast.makeText(Register.this, "Please enter Valid Passwords", Toast.LENGTH_LONG).show();
				else{
					pass = password.getText().toString();
					e = email.getText().toString();
					new register().execute();
				}
			}
		});
		
		
	}
	class register extends AsyncTask<Void, Void, Void> {
		private httpHandler jParse;
		private ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			jParse = s.getParser();
			pDialog = new ProgressDialog(Register.this);
			pDialog.setMessage("Confirming Identity ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("request","register"));
			params.add(new BasicNameValuePair("email", e));
			params.add(new BasicNameValuePair("password", pass));
			String json = jParse.makeRequest("http://52.10.7.245/api.php", "POST", params);
			Log.d("User Json", json);
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
						startActivity(new Intent(Register.this,tabs.class));
						finish();
					}
					else{
						confirm.setText("");
						password.setText("");
						Toast.makeText(getBaseContext(),"That Email is already in use", Toast.LENGTH_LONG).show();
					}
				}
			});

		}
	}

}
