package com.gameplaycoder.cartrell.inventoryapp.data;

import android.provider.BaseColumns;

public final class BookContract {

  ////////////////////////////////////////////////////////////////////////////////
  // BookEntry
  ////////////////////////////////////////////////////////////////////////////////
  public static final class BookEntry implements BaseColumns {
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
