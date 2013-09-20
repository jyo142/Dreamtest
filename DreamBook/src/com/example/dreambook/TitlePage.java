package com.example.dreambook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TitlePage extends Activity {

	private PopupWindow password_ppw;
	private ProgressDialog progressDialog;

	private enum Result {
		BOTH_INCORRECT, USERNAME_INCORRECT, PASSWORD_INCORRECT, UNEXPECTED_ERROR,
		EMAIL_NOT_VERIFIED;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "RDPrQnZpcvH8r3DOolpDjHKPI1cKdI2wXIYefXo0", "Izgv2fEsKKWIFxPT2QV6zn8F84Er8XZtnv3BhtJY"); 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title_page);
		setFonts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_title_page, menu);
		return true;
	}

	private void setFonts() {
		Typeface tf = Typeface.createFromAsset(getAssets(), "BaroqueScript.ttf");
		TextView titleTV = (TextView) findViewById(R.id.main_title);
		TextView usernameTV = (TextView) findViewById(R.id.username_tv);
		TextView passwordTV = (TextView) findViewById(R.id.password_tv);
		titleTV.setTypeface(tf);
		usernameTV.setTypeface(tf);
		passwordTV.setTypeface(tf);
	}

	public void goToRegister(View view) {
		Intent intent = new Intent(this, RegisterPage.class);
		startActivity(intent);
	}

	public void buildResetPopup(View view) {
		// set up the layout inflater to inflate the popup layout
		LayoutInflater layoutInflater =
				(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

		// the parent layout to put the layout in
		ViewGroup parentLayout = (ViewGroup) findViewById(R.id.title_page_layout);

		// Build the reset password popup
		View popupView = layoutInflater.inflate(R.layout.reset_pw_layout, null);
		password_ppw = new PopupWindow(popupView,
		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		password_ppw.showAtLocation(parentLayout, Gravity.TOP, 10, 50);
		final View contentView = password_ppw.getContentView();
		Typeface tf = Typeface.createFromAsset(getAssets(), "BaroqueScript.ttf");
		TextView forgotPWTitle = (TextView) contentView.findViewById(R.id.email_forgot_password);
		TextView forgotPWReset = (TextView) contentView.findViewById(R.id.email_reset);
		forgotPWTitle.setTypeface(tf);
		forgotPWReset.setTypeface(tf);
	}

	/**
	 * Handles what happens when user wants to their reset password.
	 * @param alertDialogBuilder The AlertDialog.Builder
	 */
	public final void resetPassword(View view) {
		// get the contents of the popup window and get the email the user typed in
		final View contentView = password_ppw.getContentView();
		EditText emailReset = (EditText) contentView.findViewById(R.id.email_forgot_password_input);
		final String emailString = emailReset.getText().toString();

		final TextView errorMessage = (TextView) contentView.findViewById(R.id.reset_error_message);
		// try to reset the password by sending an email
		try {
			ParseUser.requestPasswordReset(emailString);
			// success
			buildAlertDialog(R.string.pswd_reset_title, R.string.pswd_reset_msg);
		} catch (ParseException e) {
			// failure
			int errorCode = e.getCode();
			if (errorCode == ParseException.INVALID_EMAIL_ADDRESS) {
				errorMessage.setText(R.string.error_reset_email);
			} else {
				errorMessage.setText(R.string.error_reset_fail);
			}
		}
		password_ppw.dismiss();
	}

	/**
	 * Handles what happens when user clicks the login button.
	 * @param view Button that is pressed
	 */
	public final void loginButton(final View view) {
		try {
			loginUser();
		} catch (InternetConnectionException e) {
			//TODO: figure out error status for no internet
		}
	}

	/**
	 * Handles what happens when user clicks the login button.
	 * If this is "", the activity should NOT continue to the multiplayer screen.
	 * @throws InternetConnectionException 
	 */
	public void loginUser() throws InternetConnectionException {
		// Get the username and password inputs
		EditText usernameInput = (EditText) findViewById(R.id.username_et_title);
		EditText passwordInput = (EditText) findViewById(R.id.password_et_title);
		String usernameString = usernameInput.getText().toString().trim();
		final String passwordString = passwordInput.getText().toString();

		Map<String, String> argMap = new HashMap<String, String>();
		argMap.put("username", usernameString);
		argMap.put("password", passwordString);
		LoadTask task = new LoadTask(this);
		task.execute(argMap);
	}

	private void buildAlertDialog(final int title, final int message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * Exits the login popup window.
	 * @param view the button clicked
	 */
	public final void exitPasswordPopup(final View view) {
		password_ppw.dismiss();
	}

	private class LoadTask extends AsyncTask<Map<String, String>, Integer, Void> {


		// called before running code in a separate thread

		private Result resultCode;
		private boolean isSuccess;

		public LoadTask(Activity activity) {}
		@Override
		protected void onPreExecute() {
			//progressDialog = new ProgressDialog(activity);
			progressDialog = ProgressDialog.show(TitlePage.this, 
			getString(R.string.login_progress_title),  
			getString(R.string.login_progress_message), false, false);
		}

		@Override
		protected Void doInBackground(Map<String, String>... arg0) {
			// Try to login with the given inputs
			ParseUser user = null;
			Map<String, String> argMap = arg0[0];
			try {
				user = ParseUser.logIn(argMap.get("username"), argMap.get("password"));
			} catch (ParseException e) {
				e.fillInStackTrace();
				boolean errorOccured = false;
				List<ParseObject> usernameResults = new ArrayList<ParseObject>();
				List<ParseObject> passwordResults = new ArrayList<ParseObject>();
				ParseQuery query = ParseUser.getQuery();
				// try to find the username that the user typed in
				query.whereEqualTo("username", argMap.get("username"));
				try {
					query.count();
					usernameResults = query.find();
				} catch (ParseException e1) {
					// error occured trying to find the username
					errorOccured = true;
					e1.printStackTrace();
				} catch (NullPointerException e1) {
					errorOccured = true;
					e1.printStackTrace();
				}

				// try to find the password that the user typed in
				// associated with that username
				query.whereEqualTo("username", argMap.get("username"));
				query.whereEqualTo("password", argMap.get("password"));
				try {
					query.count();
					passwordResults = query.find();
				} catch (ParseException e1) {
					// error occured trying to find the password
					errorOccured = true;
					e1.printStackTrace();
				} catch (NullPointerException e1) {
					errorOccured = true;
					e1.printStackTrace();
				}

				// figure out the error
				if (errorOccured) {
					resultCode = Result.UNEXPECTED_ERROR;
				//	buildAlertDialog(R.string.error_login_title, R.string.error_login_unexp);
				}
				if ((usernameResults.size() == 0) && (passwordResults.size() == 0)) {
					resultCode = Result.BOTH_INCORRECT;
				//	buildAlertDialog(R.string.error_login_title, R.string.error_login_combo);
				} else if ((usernameResults.size() == 0) && (passwordResults.size() != 0)) {
					resultCode = Result.USERNAME_INCORRECT;
					//buildAlertDialog(R.string.error_login_title, R.string.error_login_uname);
				} else if ((usernameResults.size() != 0) && (passwordResults.size() == 0)) {
					resultCode = Result.PASSWORD_INCORRECT;
					//buildAlertDialog(R.string.error_login_title, R.string.error_login_pswd);
				} else {
					// unexpected error
					resultCode = Result.UNEXPECTED_ERROR;
				//	buildAlertDialog(R.string.error_login_title, R.string.error_login_unexp);
				}
				isSuccess = false;
				return null;
			}
			// Check for verified email
			boolean emailVerified = user.getBoolean("emailVerified");
			if (!emailVerified) {
				resultCode = Result.EMAIL_NOT_VERIFIED;
				ParseUser.logOut();
			} 
			isSuccess = true;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			System.out.println("is Success " + isSuccess);
			if (isSuccess) {
				Intent mainlobbyIntent = new Intent(TitlePage.this, MainLobby.class);
				startActivity(mainlobbyIntent);
				finish();
			} else {
				if (resultCode == Result.UNEXPECTED_ERROR) {
					buildAlertDialog(R.string.error_login_title, R.string.error_login_unexp);
				} else if (resultCode == Result.BOTH_INCORRECT) {
					buildAlertDialog(R.string.error_login_title, R.string.error_login_combo);
				} else if (resultCode == Result.USERNAME_INCORRECT) {
					buildAlertDialog(R.string.error_login_title, R.string.error_login_uname);
				} else if (resultCode == Result.PASSWORD_INCORRECT) { 
					buildAlertDialog(R.string.error_login_title, R.string.error_login_pswd);
				} else {
					buildAlertDialog(R.string.error_login_title, R.string.error_login_verif);
				}
			}
		}
	}
}
