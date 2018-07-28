package com.gameplaycoder.cartrell.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;
import com.gameplaycoder.cartrell.inventoryapp.data.BookEntries;
import com.gameplaycoder.cartrell.inventoryapp.data.BooksDbHelper;

public class MainActivity extends AppCompatActivity {

  ////////////////////////////////////////////////////////////////////////////////
  // members
  ////////////////////////////////////////////////////////////////////////////////
  private BooksDbHelper mDbHelper;

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

    mDbHelper = new BooksDbHelper(this);
    insertData();
    readData();
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // insertData
  //------------------------------------------------------------------------------
  private void insertData() {
    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    insertEntry(db, BookEntries.Captivate());
    insertEntry(db, BookEntries.CompassionateSamurai());
    insertEntry(db, BookEntries.ConfidentYou());
    insertEntry(db, BookEntries.Hilda());
    insertEntry(db, BookEntries.HowToWinFriends());
    insertEntry(db, BookEntries.Quiet());
  }

  //------------------------------------------------------------------------------
  // insertEntry
  //------------------------------------------------------------------------------
  private void insertEntry(SQLiteDatabase db, ContentValues values) {
    final String TABLE_NAME = BookEntry.TABLE_NAME;
    long id = db.insert(TABLE_NAME, null, values);

    TextView textView = findViewById(R.id.txt_log);
    if (id > -1) {
      textView.append("successfully inserted data at id " + String.valueOf(id) + "\n");
    } else {
      textView.append("error inserting data\n");
    }
  }

  //------------------------------------------------------------------------------
  // readData
  //------------------------------------------------------------------------------
  private void readData() {
    // Create and/or open a database to read from it
    SQLiteDatabase db = mDbHelper.getReadableDatabase();

    String[] columns = new String[]{
      BookEntry.COLUMN_NAME,
      BookEntry.COLUMN_PRICE,
      BookEntry.COLUMN_QUANTITY,
      BookEntry.COLUMN_SUPPLIER,
      BookEntry.COLUMN_SUPPLIER_PHONE,
      BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE
    };

    Cursor cursor = db.query(BookEntry.TABLE_NAME, null, null, null,
      null, null, null);

    TextView textView = findViewById(R.id.txt_log);

    try {
      textView.append("Number of books in the " + BookEntry.TABLE_NAME + "table: " + cursor.getCount());
      textView.append("\n" +
        BookEntry._ID + " - " +
        BookEntry.COLUMN_NAME + " - " +
        BookEntry.COLUMN_PRICE + " - " +
        BookEntry.COLUMN_QUANTITY + " - " +
        BookEntry.COLUMN_SUPPLIER + " - " +
        BookEntry.COLUMN_SUPPLIER_PHONE + " - " +
        BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE);

      // Figure out the index of each column
      int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
      int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
      int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
      int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
      int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
      int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
      int audiobookColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE);

      // Iterate through all the returned rows in the cursor
      while (cursor.moveToNext()) {
        int currentID = cursor.getInt(idColumnIndex);
        String currentName = cursor.getString(nameColumnIndex);
        int currentPrice = cursor.getInt(priceColumnIndex);
        int currentQuantity = cursor.getInt(quantityColumnIndex);
        String currentSupplier = cursor.getString(supplierColumnIndex);
        String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
        int currentAudiobook = cursor.getInt(audiobookColumnIndex);

        textView.append("\n" +
          currentID + " - " +
          currentName + " - " +
          currentPrice + " - " +
          currentQuantity + " - " +
          currentSupplier + " - " +
          currentSupplierPhone + " - " +
          (currentAudiobook == 1 ? "yes" : "no"));
      }
    } finally {
      cursor.close();
    }
  }
}
