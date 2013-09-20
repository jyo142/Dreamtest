package com.example.dreambook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

public class DreamDiary extends Activity {

	public static final String DELIM = ":~:";
	ParseUser currentUser;
	List<DreamObject> dreamList; // stores all the information about inputed dreams
	QuickAction mQuickAction; // shows the different options for the dreams
	int listviewpos; // last clicked dream entrym
	PopupWindow dreamViewPPW;
	PopupWindow dreamEditPPW;
	List<String> dreamStringList;
	List<Date> dreamDateList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "RDPrQnZpcvH8r3DOolpDjHKPI1cKdI2wXIYefXo0", "Izgv2fEsKKWIFxPT2QV6zn8F84Er8XZtnv3BhtJY"); 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dream_diary);

		listviewpos = 0;
		dreamList = new ArrayList<DreamObject>();
		currentUser = ParseUser.getCurrentUser();
		dreamStringList = currentUser.getList("dreamList");
		dreamDateList = currentUser.getList("dreamDate");
		constructDreamList();
		setUpActionItems();
		populateListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dream_diary, menu);
		return true;
	}

	// construct the dream list by making dreamObjects of the dreamStringList and dreamDateList
	private void constructDreamList() {
		// both lists are guaranteed to be the same size
		if (dreamStringList != null && dreamDateList != null) {
			for (int i = 0; i < dreamStringList.size(); i++) {
				DreamObject currentDreamObject = new DreamObject();
				currentDreamObject.setDreamText(dreamStringList.get(i));
				currentDreamObject.setDreamDate(dreamDateList.get(i));
				dreamList.add(currentDreamObject);
			}
		}
	}

	private void setUpActionItems() {
		// create the entrys that appear in the list
		ActionItem deleteAction = new ActionItem();
		deleteAction.setTitle("Delete entry"); 

		ActionItem viewAction = new ActionItem();
		viewAction.setTitle("View entry");

		ActionItem postAction = new ActionItem();
		postAction.setTitle("Edit entry");
		mQuickAction = new QuickAction(this);
		mQuickAction.addActionItem(deleteAction);
		mQuickAction.addActionItem(viewAction);
		mQuickAction.addActionItem(postAction);

		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@SuppressLint("InlinedApi")
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				// TODO Auto-generated method stub
				if (pos == 0) {
					Toast.makeText(DreamDiary.this, "Delete item selected", Toast.LENGTH_SHORT).show();
					removeItemAtPosition(listviewpos);
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				} else if (pos == 1) {
					// in case user clicks on another view button
					if (dreamViewPPW != null) {
						dreamViewPPW.dismiss();
					}
					buildPopupWindow(false);
					// retrieve the views from the popup window
					final View contentView = dreamViewPPW.getContentView();
					TextView dreamSubjectPopup = (TextView) contentView.findViewById(R.id.dream_subject_popup);
					TextView dreamContentPopup = (TextView) contentView.findViewById(R.id.dream_content_popup);

					DreamObject currentDO = getDreamObjAt(listviewpos);

					// fill in the text for the views
					dreamSubjectPopup.setText(currentDO.getDreamSubject());
					dreamContentPopup.setText(currentDO.getDreamContent());
				} else {
					// in case user clicks on another edit button
					if (dreamEditPPW != null) {
						dreamEditPPW.dismiss();
					}
					buildPopupWindow(true);
					// retrieve the views from the popup window
					final View contentView = dreamEditPPW.getContentView();
					EditText dreamSubjectEP = (EditText) contentView.findViewById(R.id.dream_subject_ep);
					EditText dreamContentEP = (EditText) contentView.findViewById(R.id.dream_content_ep);

					DreamObject currentDO = getDreamObjAt(listviewpos);

					// fill in the text for the views
					dreamSubjectEP.setText(currentDO.getDreamSubject());
					dreamContentEP.setText(currentDO.getDreamContent());
				}
			}
		});
	}

	public void submitChanges(View view) {
		// retrieve the views from the edit popup window
		final View contentView = dreamEditPPW.getContentView();
		EditText dreamSubjectEP = (EditText) contentView.findViewById(R.id.dream_subject_ep);
		EditText dreamContentEP = (EditText) contentView.findViewById(R.id.dream_content_ep);

		// convert the text in the edit texts to strings
		String newDreamSubject = dreamSubjectEP.getText().toString();
		String newDreamContent = dreamContentEP.getText().toString();

		// get the current DreamObjectc
		String newDreamText = newDreamSubject + DELIM + newDreamContent;
		dreamStringList.set(listviewpos, newDreamText);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle(R.string.edit_confirm_title);
		// set dialog message
		alertDialogBuilder
			.setMessage(R.string.edit_confirm_message)
			.setCancelable(false)
			.setPositiveButton(R.string.yes_string, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					// if this button is clicked, close the dialog box
					// put the newly changed strings into the database
					currentUser.put("dreamList", dreamStringList);
					currentUser.saveInBackground();
					dialog.cancel();
					AlertDialogBuilder.buildAlertDialog(DreamDiary.this, R.string.edit_successful_title, R.string.edit_successful_message);
					dreamEditPPW.dismiss();
				}
			})
			.setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					AlertDialogBuilder.buildAlertDialog(DreamDiary.this ,R.string.edit_canceled_title, R.string.edit_canceled_message);
					dreamEditPPW.dismiss();
				}
			});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the message
		alertDialog.show();
	}

	public void dismissDreamVP(View view) {
		dreamViewPPW.dismiss();
	}

	public void dismissDreamEP(View view) {
		dreamEditPPW.dismiss();
	}

	private void buildPopupWindow(boolean isEdit) {
		// set up the layout inflater to inflate the popup layout
		LayoutInflater layoutInflater =
		(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

		// the parent layout to put the layout in
		ViewGroup parentLayout = (ViewGroup) findViewById(R.id.dream_diary_layout);

		// inflate either the login layout

		// Build the popup
		View popupView;
		if (isEdit) {
			popupView = layoutInflater.inflate(R.layout.edit_dream_popup, null);
			dreamEditPPW = new PopupWindow(popupView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			dreamEditPPW.showAtLocation(parentLayout, Gravity.TOP, 10, 50);
		} else {
			popupView = layoutInflater.inflate(R.layout.view_dream_popup, null);
			dreamViewPPW = new PopupWindow(popupView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			dreamViewPPW.showAtLocation(parentLayout, Gravity.TOP, 10, 50);
		}

	}
	private void populateListView() {
		ListView dreamLV = (ListView)findViewById(R.id.dream_list);
		DreamDiaryAdapter ddAdapter = new DreamDiaryAdapter(this, R.layout.diary_row, dreamList);
		dreamLV.setAdapter(ddAdapter);
		dreamLV.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				listviewpos = position;
				mQuickAction.show(v);
			}
		});
	}

	// retrieves the dream content for the DreamObject in the passed in position in the ListView
	private DreamObject getDreamObjAt(int pos) {
		DreamObject currentDO = dreamList.get(pos);
		return currentDO;
	}

	// removes the item at position pos in the dreamStringList and dreamDateList, and updates database
	private void removeItemAtPosition(int pos) {
		dreamStringList.remove(pos);
		dreamDateList.remove(pos);
		currentUser.put("dreamList", dreamStringList);
		currentUser.put("dreamDate", dreamDateList);
		currentUser.saveInBackground();
	}

	private class DreamDiaryAdapter extends ArrayAdapter<DreamObject> {
		private List<DreamObject> DDAList;

		public DreamDiaryAdapter(Context context, int textViewResourceId, List<DreamObject> list) {
			super(context, textViewResourceId, list);
			this.DDAList = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.diary_row, null);
			}

			DreamObject dObject = DDAList.get(position);
			if (dObject != null) {
				String dreamDateString = dObject.getDreamDate();
				String dreamTextString = dObject.getDreamSubject();
				TextView dreamDate = (TextView) v.findViewById(R.id.date_diary_text);
				TextView dreamSubject = (TextView) v.findViewById(R.id.dream_subject_text);

				dreamDate.setText("Submitted on : " + dreamDateString);
				dreamSubject.setText("Subject : " + dreamTextString);
			}
			return v;
		}
	}
}