package com.example.smunow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class descriptionDialog extends DialogFragment{
	private AlertDialog.Builder builder;
	private String message;
	public descriptionDialog(String toastable) {
		message = toastable;
	}

	//private String message;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//nothing
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	public void setMessage(String format){
		builder.setMessage(format);
		return;
	}

}
