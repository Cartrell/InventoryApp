package com.gameplaycoder.cartrell.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  public static final String CONTENT_AUTHORITY = "com.gameplaycoder.cartrell.inventoryapp";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
  public static final String PATH_BOOKS = "books";

  ////////////////////////////////////////////////////////////////////////////////
  // BookEntry
  ////////////////////////////////////////////////////////////////////////////////
  public static final class BookEntry implements BaseColumns {
    /**
     * The MIME type of the {@link #CONTENT_URI} for a list of books.
     */
    public static final String CONTENT_LIST_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    /**
     * The MIME type of the {@link #CONTENT_URI} for a single book.
     */
    public static final String CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

    public static final String TABLE_NAME = "books";

    public static final String _ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_SUPPLIER = "supplier";
    public static final String COLUMN_SUPPLIER_PHONE = "supplierPhone";
    public static final String COLUMN_IS_AUDIOBOOK_AVAILABLE = "isAudioBookAvailable";
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // ctor
  //------------------------------------------------------------------------------
  private BookContract() {
    //objects of this class aren't meant to be instantiated
  }
}
