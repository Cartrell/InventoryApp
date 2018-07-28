package com.gameplaycoder.cartrell.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

public class BooksDbHelper extends SQLiteOpenHelper {

  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  private static final int VERSION = 1;
  private static final String FILENAME = "booksInventory.db";

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // ctor
  //------------------------------------------------------------------------------
  public BooksDbHelper(Context context) {
    super(context, FILENAME, null, VERSION);
  }

  //------------------------------------------------------------------------------
  // onCreate
  //------------------------------------------------------------------------------
  @Override
  public void onCreate(SQLiteDatabase db) {
    // Create a String that contains the SQL statement to create the pets table
    String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " +
      BookEntry.TABLE_NAME + " (" +
      BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      BookEntry.COLUMN_NAME + " TEXT NOT NULL, " +
      BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, " +
      BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
      BookEntry.COLUMN_SUPPLIER + " TEXT, " +
      BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT, " +
      BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE + " BOOLEAN DEFAULT 0);";

    db.execSQL(SQL_CREATE_BOOKS_TABLE);
  }

  //------------------------------------------------------------------------------
  // onUpgrade
  //------------------------------------------------------------------------------
  @Override
  public void onUpgrade(SQLiteDatabase db, int i, int i1) {
  }
}
