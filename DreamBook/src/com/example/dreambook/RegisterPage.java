package com.example.dreambook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 *
 * Handles new user registration
 *
 */
public class RegisterPage extends Activity {

	private String currentUser;
	private Intent titleIntent; // used to go back to title

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		Parse.initialize(this, "RDPrQnZpcvH8r3DOolpDjHKPI1cKdI2wXIYefXo0", "Izgv2fEsKKWIFxPT2QV6zn8F84Er8XZtnv3BhtJY"); 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setContentView(R.layout.activity_register_page);
		setFonts();
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register_page, menu);
		return true;
	}

	/**
	 * Cancel registering and go back to the title page.
	 * @param view The button clicked.
	 */
	public final void cancel(final View view) {
		Log.i("Register", "back to title page from register");
		finish();
	}

	/**
	 * Handles what happens when the submit button is pressed.
	 * @param view The button that is pressed
	 */
	public final void submitRegister(final View view) {
		Log.i("Register", "submitting registration");

		// get the inputs and prompts from the user
		TextView usernameText = (TextView) findViewById(R.id.username_register_tv);
		EditText usernameEdit = (EditText) findViewById(R.id.username_register_et);
		String usernameString = usernameEdit.getText().toString().trim();

		TextView passwordText = (TextView) findViewById(R.id.password_register_tv);
		EditText passwordEdit = (EditText) findViewById(R.id.password_register_et);
		String passwordString = passwordEdit.getText().toString();

		TextView confirmPWText = (TextView) findViewById(R.id.confirm_pw_register_tv);
		EditText confirmPWEdit = (EditText) findViewById(R.id.confirm_pw_register_et);
		String confirmPWString = confirmPWEdit.getText().toString();

		TextView emailText = (TextView) findViewById(R.id.email_register_tv);
		EditText emailEdit = (EditText) findViewById(R.id.email_register_et);
		String emailString = emailEdit.getText().toString();

		if ((((usernameString.length() == 0) || (passwordString.length() == 0))
		|| (confirmPWString.length() == 0))
		|| (emailString.length() == 0)) {
			// case where everything is not filled out
			Log.w("Register", "missing information for registration");

			buildAlertDialog(R.string.missing_reg_title, R.string.missing_reg_msg, false);
			// indicate which ones were not filled out
			if (usernameString.length() == 0) {
				indicateError(usernameText, true);
			} else {
				indicateError(usernameText, false);
			}

			if (passwordString.length() == 0) {
				indicateError(passwordText, true);
			} else {
				indicateError(passwordText, false);
			}

			if (confirmPWString.length() == 0) {
				indicateError(confirmPWText, true);
			} else {
				indicateError(confirmPWText, false);
			}

			if (emailString.length() == 0) {
				indicateError(emailText, true);
			} else {
				indicateError(emailText, false);
			}
		} else {
			// the fields arent empty, check if the passwords match
			if (!passwordString.equals(confirmPWString)) {

				buildAlertDialog(R.string.umatch_pw_title, R.string.umatch_pw_msg, false);
				indicateError(passwordText, true);
				indicateError(confirmPWText, true);
				// all other text fields should not be red
				indicateError(usernameText, false);
				indicateError(emailText, false);
			} else {
				if ((usernameString.length() >= 4) && (passwordString.length() >= 4)) {
					// make sure username and pw is reasonable length, for security purposes
					// need to put the color back to normal
					indicateError(usernameText, false);
					indicateError(passwordText, false);
					indicateError(confirmPWText, false);
					indicateError(emailText, false);
					// inputs are valid put into the database
					setupDatabase(usernameString, passwordString, emailString);
				} else {
					if (usernameString.length() < 4) {
						Log.w("Register", "username do not contain enough characters");
						indicateError(usernameText, true);
					} else {
						indicateError(usernameText, false);
					}
					if (passwordString.length() < 4) {
						Log.w("Register", "password do not contain enough characters");
						indicateError(passwordText, true);
					} else {
						indicateError(passwordText, false);
					}
					buildAlertDialog(R.string.bad_combination_title, R.string.bad_combination_msg, false);
				}

			}
		}
	}

	/**
	 * Builds an alertdialog(popup) for a given title, message, and
	 * optional multiplayer option.
	 * @param title String representing the title of the popup
	 * @param message String representing the message displayed in popup
	 * @param goToTitle indicates whether popup should handle
	 * case where it goes back to titlepage
	 */
	private void buildAlertDialog(final int title, final int message,
	final boolean goToTitle) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		if (goToTitle) {
			titleIntent = new Intent(this, TitlePage.class);
			titleIntent.putExtra("current user", currentUser);
		}
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
				if (goToTitle) {
					// allowed to go back to title page
					startActivity(titleIntent);
				}
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the message
		alertDialog.show();
	}

	private void setFonts() {
		Typeface tf = Typeface.createFromAsset(getAssets(), "BaroqueScript.ttf");

		// get all the text views to change the font for
		TextView registerTitle = (TextView) findViewById(R.id.register_title);
		TextView usernameRegisterTV = (TextView) findViewById(R.id.username_register_tv);
		TextView passwordRegisterTV = (TextView) findViewById(R.id.password_register_tv);
		TextView confirmPWRegisterTV = (TextView) findViewById(R.id.confirm_pw_register_tv);
		TextView emailRegisterTV = (TextView) findViewById(R.id.email_register_tv);

		// set the fonts for all of the textviews
		registerTitle.setTypeface(tf);
		usernameRegisterTV.setTypeface(tf);
		passwordRegisterTV.setTypeface(tf);
		confirmPWRegisterTV.setTypeface(tf);
		emailRegisterTV.setTypeface(tf);
	}
	/**
	 * Highlights the given textview depending on if it is an error.
	 * @param text TextView to be highlighted
	 * @param errorFlag indicates whether text should be highlighted
	 * to indicate an error or not
	 */
	private void indicateError(final TextView text, final boolean errorFlag) {
		if (errorFlag) {
			// highlight error
			text.setTextColor(getResources().getColor(R.color.red));
		} else {
			// put back to original color
			text.setTextColor(getResources().getColor(R.color.title_color));
		}
	}

	/**
	 * Puts the user information into database.
	 * @param username String representing user's username
	 * @param password String representing user's password
	 * @param email String representing user's email
	 */
	private void setupDatabase(final String username, final String password,
	final String email) {

		ParseUser user = new ParseUser();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);

		user.signUpInBackground(new SignUpCallback() {
			public void done(final ParseException e) {
				if (e == null) {
					// sign up succeeded so go to multiplayer screen
					// store the username of the current player
					currentUser = username;
					ParseUser.logOut();
					buildAlertDialog(R.string.acc_created_title, R.string.acc_created_msg, true);
				} else {
					Log.i("Register", "registration unsuccessful");

					// sign up didn't succeed
					// TODO: figure out how do deal with error
					int errorCode = e.getCode();
					// figure out what the error was
					int message;
					if (errorCode == ParseException.ACCOUNT_ALREADY_LINKED) {
						message = R.string.reg_faila_msg;
					} else if (errorCode == ParseException.EMAIL_TAKEN) {
						message = R.string.reg_faile_msg;
					} else if (errorCode == ParseException.USERNAME_TAKEN) {
						message = R.string.reg_failu_msg;
					} else if (errorCode == ParseException.INVALID_EMAIL_ADDRESS) {
						message = R.string.reg_faili_msg;
					} else {
						e.printStackTrace();
						message = R.string.reg_failo_msg;
					}
					buildAlertDialog(R.string.reg_fail_title, message, false);
				}
			}
		});
	}
}