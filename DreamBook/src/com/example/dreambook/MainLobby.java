package com.example.dreambook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

public class MainLobby extends Activity {

	ParseUser currentUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "RDPrQnZpcvH8r3DOolpDjHKPI1cKdI2wXIYefXo0", "Izgv2fEsKKWIFxPT2QV6zn8F84Er8XZtnv3BhtJY"); 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_lobby);

		currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			String currentUserString = currentUser.getString("username");
			TextView currentUserTV = (TextView) findViewById(R.id.profile_name);
			currentUserTV.setText(currentUserString);
		}
		setFonts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_lobby, menu);
		return true;
	}

	private void setFonts() {
		Typeface tf = Typeface.createFromAsset(getAssets(), "BaroqueScript.ttf");
		TextView mainLobbyTitleTV = (TextView) findViewById(R.id.main_lobby_title);
		TextView profileNameTV = (TextView) findViewById(R.id.profile_name);
		TextView dreamDiaryString = (TextView) findViewById(R.id.view_dream_lobby);
		mainLobbyTitleTV.setTypeface(tf);
		profileNameTV.setTypeface(tf);
		dreamDiaryString.setTypeface(tf);
	}

	public void submitDream(View view) {
		EditText dreamET = (EditText) findViewById(R.id.dream_input);
		EditText dreamSubjectET = (EditText) findViewById(R.id.dream_subject_input);
		String dreamString = dreamET.getText().toString();
		String dreamSubjectString = dreamSubjectET.getText().toString();

		if (!dreamString.equals("")) {
			List<String> dreamList = currentUser.getList("dreamList");
			List<Date> dreamDate = currentUser.getList("dreamDate");
			if (dreamList == null) {
				dreamList = new ArrayList<String>();
			}
			if (dreamDate == null) {
				dreamDate = new ArrayList<Date>();
			}
			// attach the subject to the dreamText and deliminate it with :~:
			if (dreamSubjectString.equals("")) {
				dreamSubjectString = getResources().getString(R.string.no_subject_string);
			}
			String newDreamString = dreamSubjectString + getResources().getString(R.string.string_delim) + dreamString;
			dreamList.add(newDreamString);
			dreamDate.add(new Date());
			currentUser.put("dreamList", dreamList);
			currentUser.put("dreamDate", dreamDate);
			currentUser.saveInBackground();
			buildAlertDialog(R.string.dream_submit_title, R.string.dream_submit_message);
		}
	}

	public void logoutUser(View view) {
		// logout the user from parse
		ParseUser.logOut();
		currentUser = ParseUser.getCurrentUser();
		buildAlertDialog(R.string.logged_out_string, R.string.logged_out_message);
	}

	private void buildAlertDialog(final int title, final int message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		final Intent intent = new Intent(this, TitlePage.class);
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
				startActivity(intent);
				finish();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the message
		alertDialog.show();
	}

	public void goToDreamDiary(View view) {
		Intent intent = new Intent(this, DreamDiary.class);
		startActivity(intent);
	}
}