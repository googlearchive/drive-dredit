// Copyright 2012 Google Inc. All Rights Reserved.

package com.example.android.notepad;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * This Activity allows the user to edit a note's title. It displays a floating
 * window containing an EditText.
 * 
 * NOTE: Notice that the provider operations in this Activity are taking place
 * on the UI thread. This is not a good practice. It is only done here to make
 * the code more readable. A real application should use the
 * {@link android.content.AsyncQueryHandler} or {@link android.os.AsyncTask}
 * object to perform operations asynchronously on a separate thread.
 */
public class TitleEditor extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

  /**
   * This is a special intent action that means "edit the title of a note".
   */
  public static final String EDIT_TITLE_ACTION = "com.android.notepad.action.EDIT_TITLE";

  // Creates a projection that returns the note ID and the note contents.
  private static final String[] PROJECTION = new String[] {NotePad.Notes._ID, // 0
      NotePad.Notes.COLUMN_NAME_TITLE, // 1
  };

  // The position of the title column in a Cursor returned by the provider.
  private static final int COLUMN_INDEX_TITLE = 1;

  // A Cursor object that will contain the results of querying the provider for
  // a note.
  private Cursor mCursor;

  // An EditText object for preserving the edited title.
  private EditText mText;

  // A URI object for the note whose title is being edited.
  private Uri mUri;

  private ProgressDialog mProgressBar = null;

  /**
   * This method is called by Android when the Activity is first started. From
   * the incoming Intent, it determines what kind of editing is desired, and
   * then does it.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Set the View for this Activity object's UI.
    setContentView(R.layout.title_editor);

    // Get the Intent that activated this Activity, and from it get the URI of
    // the note whose
    // title we need to edit.
    mUri = getIntent().getData();

    // Gets the View ID for the EditText box
    mText = (EditText) this.findViewById(R.id.title);
  }

  /**
   * This method is called when the Activity is about to come to the foreground.
   * This happens when the Activity comes to the top of the task stack, OR when
   * it is first starting.
   * 
   * Displays the current title for the selected note.
   */
  @Override
  protected void onResume() {
    super.onResume();
    getLoaderManager().initLoader(0, null, this);
    mProgressBar = ProgressDialog.show(this, null, "Loading title...", true);
  }

  /**
   * This method is called when the Activity loses focus.
   * 
   * For Activity objects that edit information, onPause() may be the one place
   * where changes are saved. The Android application model is predicated on the
   * idea that "save" and "exit" aren't required actions. When users navigate
   * away from an Activity, they shouldn't have to go back to it to complete
   * their work. The act of going away should save everything and leave the
   * Activity in a state where Android can destroy it if necessary.
   * 
   * Updates the note with the text currently in the text box.
   */
  @Override
  protected void onPause() {
    super.onPause();
  }

  public void onClickOk(View v) {
    // Verifies that the query made in onCreate() actually worked. If it worked,
    // then the  Cursor object is not null. If it is *empty*, then
  	// mCursor.getCount() == 0.
    if (mCursor != null) {
      // Creates a values map for updating the provider.
      ContentValues values = new ContentValues();

      // In the values map, sets the title to the current contents of the
      // edit
      // box.
      values.put(NotePad.Notes.COLUMN_NAME_TITLE, mText.getText().toString());
      values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

      /*
       * Updates the provider with the note's new title.
       * 
       * Note: This is being done on the UI thread. It will block the thread
       * until the update completes. In a sample app, going against a simple
       * provider based on a local database, the block will be momentary, but in
       * a real app you should use android.content.AsyncQueryHandler or
       * android.os.AsyncTask.
       */
      getContentResolver().update(mUri, // The URI for the note to update.
          values, // The values map containing the columns to update and the values to use.
          null, // No selection criteria is used, so no "where" columns are needed.
          null // No "where" columns are used, so no "where" values are needed.
          );

      finish();
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    /*
     * Using the URI passed in with the triggering Intent, gets the note.
     * 
     * Note: This is being done on the UI thread. It will block the thread until
     * the query completes. In a sample app, going against a simple provider
     * based on a local database, the block will be momentary, but in a real app
     * you should use android.content.AsyncQueryHandler or android.os.AsyncTask.
     */
    return new CursorLoader(this, mUri, PROJECTION, // The columns to retrieve
        null, // No selection criteria are used, so no where columns are needed.
        null, // No where columns are used, so no where values are needed.
        null // No sort order is needed.
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    mCursor = cursor;
    // Verifies that the query made in onCreate() actually worked. If it worked,
    // then the Cursor object is not null. If it is *empty*, then
    // mCursor.getCount() == 0.
    if (mCursor != null) {

      // The Cursor was just retrieved, so its index is set to one record
      // *before* the first record retrieved. This moves it to the first record.
      mCursor.moveToFirst();

      // Displays the current title text in the EditText object.
      mText.setText(mCursor.getString(COLUMN_INDEX_TITLE));
    }
    if (mProgressBar != null) {
      mProgressBar.dismiss();
      mProgressBar = null;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> arg0) {
  }
}
