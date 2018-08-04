package com.gameplaycoder.cartrell.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  private static final String LOG_TAG = BookProvider.class.getSimpleName();

  private static final int BOOKS = 100;
  private static final int BOOK_ID = 101;

  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
    sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
  }

  private BooksDbHelper mDbHelper;

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // delete
  //------------------------------------------------------------------------------
  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase database = mDbHelper.getWritableDatabase();

    int match = sUriMatcher.match(uri);
    if (match != BOOK_ID) {
      throw(new IllegalArgumentException("Deletion is not supported for " + uri));
    }

    // Delete a single row given by the ID in the URI
    selection = BookEntry._ID + "=?";
    selectionArgs = new String[] {
      String.valueOf(ContentUris.parseId(uri))
    };

    int rowsAffecred = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
    if (rowsAffecred > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return(rowsAffecred);
  }

  //------------------------------------------------------------------------------
  // getType
  //------------------------------------------------------------------------------
  @Override
  public String getType(@NonNull Uri uri) {
    int match = sUriMatcher.match(uri);
    switch (match) {
      case BOOKS:
        return(BookEntry.CONTENT_LIST_TYPE);

      case BOOK_ID:
        return(BookEntry.CONTENT_ITEM_TYPE);

      default:
        throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
    }
  }

  //------------------------------------------------------------------------------
  // insert
  //------------------------------------------------------------------------------
  @Override
  public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case BOOKS:
        return(insertBook(uri, contentValues));

      default:
        throw(new IllegalArgumentException("Insertion is not supported for " + uri));
    }
  }

  //------------------------------------------------------------------------------
  // onCreate
  //------------------------------------------------------------------------------
  @Override
  public boolean onCreate() {
    mDbHelper = new BooksDbHelper(getContext());
    return(true);
  }

  //------------------------------------------------------------------------------
  // query
  //------------------------------------------------------------------------------
  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
  String sortOrder) {
    SQLiteDatabase database = mDbHelper.getReadableDatabase();
    Cursor cursor = null;

    // Figure out if the URI matcher can match the URI to a specific code
    int match = sUriMatcher.match(uri);
    switch (match) {
      case BOOKS:
        // The cursor could contain multiple rows of the table.
        cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
          null, null, sortOrder);
        break;

      case BOOK_ID:
        // For the BOOK_ID code, extract out the ID from the URI.
        // the selection will be "_id=?" and the selection argument will be a
        // String array containing the actual ID
        //
        // For every "?" in the selection, we need to have an element in the selection
        // arguments that will fill in the "?". Since we have 1 question mark in the
        // selection, we have 1 String in the selection arguments' String array.
        selection = BookEntry._ID + "=?";
        selectionArgs = new String[] {
          String.valueOf(ContentUris.parseId(uri))
        };

        // the cursor will contain one row of the table.
        cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
          null, null, sortOrder);
        break;

      default:
        throw(new IllegalArgumentException("Cannot query unknown URI " + uri));
    }

    // Set notification URI on the Cursor,
    // so we know what content URI the Cursor was created for.
    // If the data at this URI changes, then we know we need to update the Cursor.
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return(cursor);
  }

  //------------------------------------------------------------------------------
  // update
  //------------------------------------------------------------------------------
  @Override
  public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
    final int match = sUriMatcher.match(uri);
    int rowsAffecred;

    switch (match) {
      case BOOKS:
        rowsAffecred = updateBook(uri, contentValues, selection, selectionArgs);
        if (rowsAffecred > 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return(rowsAffecred);

      case BOOK_ID:
        selection = BookEntry._ID + "=?";
        selectionArgs = new String[] {
          String.valueOf(ContentUris.parseId(uri))
        };

        rowsAffecred = updateBook(uri, contentValues, selection, selectionArgs);
        if (rowsAffecred > 0) {
          getContext().getContentResolver().notifyChange(uri, null);
        }
        return(rowsAffecred);

      default:
        throw(new IllegalArgumentException("Update is not supported for " + uri));
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // insertBook
  //------------------------------------------------------------------------------
  private Uri insertBook(Uri uri, ContentValues values) {
    String name = values.getAsString(BookEntry.COLUMN_NAME);
    if (name == null) {
      throw(new IllegalArgumentException("Book requires a name"));
    }

    Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
    if (price != null && price < 0) {
      throw(new IllegalArgumentException("Book requires a valid price"));
    }

    String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);
    if (supplier == null) {
      throw(new IllegalArgumentException("Book requires a supplier"));
    }

    String supplierPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
    if (supplierPhone == null) {
      throw(new IllegalArgumentException("Book requires a supplier phone"));
    }

    Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
    if (quantity != null && quantity < 0) {
      throw(new IllegalArgumentException("Book requires a valid quantity"));
    }

    Boolean isAudiobookAvailable = values.getAsBoolean(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE);
    if (isAudiobookAvailable == null) {
      isAudiobookAvailable = false;
    }

    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    long id = db.insert(BookEntry.TABLE_NAME, null, values);
    if (id == -1) {
      Log.e(LOG_TAG, "Failed to insert row for " + uri);
      return(null);
    }

    // Notify all listeners that the data has changed for the pet content URI
    getContext().getContentResolver().notifyChange(uri, null);

    // Once we know the ID of the new row in the table,
    // return the new URI with the ID appended to the end of it
    return(ContentUris.withAppendedId(uri, id));
  }

  //------------------------------------------------------------------------------
  // updateBook
  //------------------------------------------------------------------------------
  private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    if (values.size() == 0) {
      //sanity check
      return(0);
    }

    if (values.containsKey(BookEntry.COLUMN_NAME)) {
      String value = values.getAsString(BookEntry.COLUMN_NAME);
      if (value == null) {
        throw(new IllegalArgumentException("Book requires a valid name"));
      }
    }

    if (values.containsKey(BookEntry.COLUMN_PRICE)) {
      Integer value = values.getAsInteger(BookEntry.COLUMN_PRICE);
      if (value == null || value < 0) {
        throw(new IllegalArgumentException("Book requires valid gender"));
      }
    }

    if (values.containsKey(BookEntry.COLUMN_SUPPLIER)) {
      String value = values.getAsString(BookEntry.COLUMN_SUPPLIER);
      if (value == null) {
        throw(new IllegalArgumentException("Book requires a valid supplier"));
      }
    }

    if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
      String value = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
      if (value == null) {
        throw(new IllegalArgumentException("Book requires a valid supplier phone"));
      }
    }

    if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
      Integer value = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
      if (value == null || value < 0) {
        throw(new IllegalArgumentException("Book requires valid quantity"));
      }
    }

    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    return(db.update(BookEntry.TABLE_NAME, values, selection, selectionArgs));
  }
}