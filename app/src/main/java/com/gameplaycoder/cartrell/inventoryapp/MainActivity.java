package com.gameplaycoder.cartrell.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
IBookCursorAdapterCallbacks {

  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  private static final int LOADER_ID = 1;
  private static final String LOG_NAME = MainActivity.class.getName();

  ////////////////////////////////////////////////////////////////////////////////
  // members
  ////////////////////////////////////////////////////////////////////////////////
  private BookCursorAdapter mAdapter;

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // bookCursorAdapterOnSale
  //------------------------------------------------------------------------------
  @Override
  public void bookCursorAdapterOnSale(BookCursorAdapter adapter, int position) {
    Cursor cursor = adapter.getCursor();

    //check for valid cursor and move to correct row in the table
    if (cursor == null || cursor.getCount() < 1 || !cursor.moveToFirst() || !cursor.move(position)) {
      Log.i(LOG_NAME, "bookCursorAdapterOnSale. Invalid cursor");
      return;
    }

    //get the current quantity
    int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
    if (quantity <= 0) {
      Log.i(LOG_NAME, "bookCursorAdapterOnSale. Invalid quantity");
      return;
    }

    //add new quantity to content values
    quantity--;
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_QUANTITY, quantity);

    //update the table row
    int rowId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
    Uri uri = Uri.withAppendedPath(BookEntry.CONTENT_URI, String.valueOf(rowId));
    int rowsAffected = getContentResolver().update(uri, values, null, null);
    if (rowsAffected > 0) {
      Toast.makeText(this, R.string.sold, Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, R.string.error_updating_book, Toast.LENGTH_SHORT).show();
    }
  }

  //------------------------------------------------------------------------------
  // onCreateLoader
  //------------------------------------------------------------------------------
  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return(new CursorLoader(this, BookEntry.CONTENT_URI, null, null,
      null, null));
  }

  //------------------------------------------------------------------------------
  // onLoaderReset
  //------------------------------------------------------------------------------
  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    mAdapter.changeCursor(null);
  }

  //------------------------------------------------------------------------------
  // onLoadFinished
  //------------------------------------------------------------------------------
  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    mAdapter.changeCursor(data);
  }

  //------------------------------------------------------------------------------
  // onCreateOptionsMenu
  //------------------------------------------------------------------------------
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return(true);
  }

  //------------------------------------------------------------------------------
  // onOptionsItemSelected
  //------------------------------------------------------------------------------
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // User clicked on a menu option in the app bar overflow menu
    switch (item.getItemId()) {
      case R.id.menu_item_add:
        handleMenuItemAdd();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  ////////////////////////////////////////////////////////////////////////////////
  // protected
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // onCreate
  //------------------------------------------------------------------------------
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initListView();

    //async load the data from the db
    getSupportLoaderManager().initLoader(LOADER_ID, null, this);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // handleMenuItemAdd
  //------------------------------------------------------------------------------
  private void handleMenuItemAdd() {
    //go to "create mode" to create a new entry for the db
    Intent intent = new Intent(this, ProductDetailsActivity.class);
    startActivity(intent);
  }

  //------------------------------------------------------------------------------
  // initListView
  //------------------------------------------------------------------------------
  private void initListView() {
    ListView booksListView = findViewById(R.id.books_list);

    //assign the empty view to the list view
    View emptyView = findViewById(R.id.empty_view);
    booksListView.setEmptyView(emptyView);

    //set up the cursor adapter to be used by the list view
    mAdapter = new BookCursorAdapter(this, null, this);
    booksListView.setAdapter(mAdapter);

    //set up the list view items, so that when one is clicked, it goes to "edit mode" for
    // the data of that item
    booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      //----------------------------------------------------------------------------
      // onItemClick
      //----------------------------------------------------------------------------
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String idString = String.valueOf(id);
        Uri uri = Uri.withAppendedPath(BookEntry.CONTENT_URI, idString);

        Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
        intent.setData(uri);
        startActivity(intent);
      }
    });
  }
}