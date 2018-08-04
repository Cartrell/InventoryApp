package com.gameplaycoder.cartrell.inventoryapp;

import android.database.Cursor;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

public final class CursorUtils {
  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  private static final String LOG_NAME = CursorUtils.class.getName();

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // GetCursorInt
  //------------------------------------------------------------------------------
  public static int GetCursorInt(Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    String sValue = cursor.getString(columnIndex);
    int value = 0;

    try {
      value = Integer.parseInt(sValue);
    } catch (NumberFormatException e) {
      Log.e(LOG_NAME, "GetCursorInt", e);
    }

    return(value);
  }

  //------------------------------------------------------------------------------
  // GetCursorText
  //------------------------------------------------------------------------------
  public static String GetCursorText(Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    return(cursor.getString(columnIndex));
  }

  //------------------------------------------------------------------------------
  // SetCheckBox
  //------------------------------------------------------------------------------
  public static void SetCheckBox(CheckBox checkBox, Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    checkBox.setChecked(cursor.getInt(columnIndex) != 0);
  }

  //------------------------------------------------------------------------------
  // SetEditText
  //------------------------------------------------------------------------------
  public static void SetEditText(EditText editText, Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    editText.setText(cursor.getString(columnIndex));
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // ctor
  //------------------------------------------------------------------------------
  private CursorUtils() {
  }
}
