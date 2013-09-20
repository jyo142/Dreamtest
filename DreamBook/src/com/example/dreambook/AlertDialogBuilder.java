package com.example.dreambook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertDialogBuilder {

	public static void buildAlertDialog(Activity activity, final int title, final int message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton(R.string.close_alert, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				// if this button is clicked, close the dialog box
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the message
		alertDialog.show();
	}

	public static void buildAlertDialogConfirm(Activity activity, final int title, final int message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setNegativeButton(R.string.yes_string, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			} )
			.setPositiveButton(R.string.no_string, new DialogInterface.OnClickListener() {
				@Override
			public void onClick(final DialogInterface dialog, final int id) {
				// if this button is clicked, close the dialog box
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the message
		alertDialog.show();

	}
}