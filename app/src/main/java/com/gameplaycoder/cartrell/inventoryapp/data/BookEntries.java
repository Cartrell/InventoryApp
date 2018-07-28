package com.gameplaycoder.cartrell.inventoryapp.data;

import android.content.ContentValues;
import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

public final class BookEntries {

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // Captivate
  //------------------------------------------------------------------------------
  public static ContentValues Captivate() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "Captivate - The Science of Succeeding with People");
    values.put(BookEntry.COLUMN_PRICE, 22);
    values.put(BookEntry.COLUMN_QUANTITY, 90);
    values.put(BookEntry.COLUMN_SUPPLIER, "Penguin Random House");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-5678");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, false);
    return(values);
  }

  //------------------------------------------------------------------------------
  // CompassionateSamurai
  //------------------------------------------------------------------------------
  public static ContentValues CompassionateSamurai() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "The Compassionate Samurai");
    values.put(BookEntry.COLUMN_PRICE, 19);
    values.put(BookEntry.COLUMN_QUANTITY, 30);
    values.put(BookEntry.COLUMN_SUPPLIER, "Hay House");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-1234");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, false);
    return(values);
  }

  //------------------------------------------------------------------------------
  // ConfidentYou
  //------------------------------------------------------------------------------
  public static ContentValues ConfidentYou() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "Confident You");
    values.put(BookEntry.COLUMN_PRICE, 12);
    values.put(BookEntry.COLUMN_QUANTITY, 50);
    values.put(BookEntry.COLUMN_SUPPLIER, "Oldtown");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-2345");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, true);
    return(values);
  }

  //------------------------------------------------------------------------------
  // Hilda
  //------------------------------------------------------------------------------
  public static ContentValues Hilda() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "Hilda");
    values.put(BookEntry.COLUMN_PRICE, 40);
    values.put(BookEntry.COLUMN_QUANTITY, 11);
    values.put(BookEntry.COLUMN_SUPPLIER, "The Audacity Coach");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-4567");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, true);
    return(values);
  }

  //------------------------------------------------------------------------------
  // HowToWinFriends
  //------------------------------------------------------------------------------
  public static ContentValues HowToWinFriends() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "How to Win Friends & Influence People");
    values.put(BookEntry.COLUMN_PRICE, 100);
    values.put(BookEntry.COLUMN_QUANTITY, 25);
    values.put(BookEntry.COLUMN_SUPPLIER, "Simon & Schuster");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-3456");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, true);
    return(values);
  }

  //------------------------------------------------------------------------------
  // Quiet
  //------------------------------------------------------------------------------
  public static ContentValues Quiet() {
    ContentValues values = new ContentValues();
    values.put(BookEntry.COLUMN_NAME, "Quiet");
    values.put(BookEntry.COLUMN_PRICE, 63);
    values.put(BookEntry.COLUMN_QUANTITY, 24);
    values.put(BookEntry.COLUMN_SUPPLIER, "Crown Publishers");
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "555-6789");
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, false);
    return(values);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // ctor
  //------------------------------------------------------------------------------
  private BookEntries() {
    //objects of this class aren't meant to be instantiated
  }
}
