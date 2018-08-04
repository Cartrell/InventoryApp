package com.gameplaycoder.cartrell.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

import java.util.Locale;

public class BookCursorAdapter extends CursorAdapter {

  ////////////////////////////////////////////////////////////////////////////////
  // members
  ////////////////////////////////////////////////////////////////////////////////
  private View.OnClickListener mButtonClickListener;
  private IBookCursorAdapterCallbacks mCallbacks;

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // ctor
  //------------------------------------------------------------------------------
  public BookCursorAdapter(Context context, Cursor cursor, IBookCursorAdapterCallbacks callbacks) {
    super(context, cursor, 0);
    mCallbacks = callbacks;
    initButtonClickListener();
  }

  //------------------------------------------------------------------------------
  // bindView
  //------------------------------------------------------------------------------
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    setBookName(view, cursor);
    setBookPrice(view, context, cursor);
    int quantity = setBookQuantity(view, context, cursor);

    Button button = view.findViewById(R.id.btn_sale);
    button.setEnabled(quantity > 0); //button is disabled if out of stock
    button.setTag(cursor.getPosition());
  }

  //------------------------------------------------------------------------------
  // newView
  //------------------------------------------------------------------------------
  @Override
  public View newView(Context context, final Cursor cursor, ViewGroup parent) {
    View listItem = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

    Button button = listItem.findViewById(R.id.btn_sale);
    button.setOnClickListener(mButtonClickListener);
    return(listItem);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // buildPriceString
  //------------------------------------------------------------------------------
  private String buildPriceString(Context context, int price) {
    String format = context.getString(R.string.price_format);
    return(String.format(Locale.getDefault(), format, price));
  }

  //------------------------------------------------------------------------------
  // buildQuantityString
  //------------------------------------------------------------------------------
  private String buildQuantityString(Context context, int quantity) {
    String format = context.getString(R.string.quantity_format);
    return(String.format(Locale.getDefault(), format, quantity));
  }

  //------------------------------------------------------------------------------
  // initButtonClickListener
  //------------------------------------------------------------------------------
  private void initButtonClickListener() {
    final BookCursorAdapter adapter = this;

    mButtonClickListener = new View.OnClickListener() {

      @Override
      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      public void onClick(View view) {
        if (mCallbacks != null) {
          int position = (int)view.getTag();
          mCallbacks.bookCursorAdapterOnSale(adapter, position);
        }
      }
    };
  }

  //------------------------------------------------------------------------------
  // setBookName
  //------------------------------------------------------------------------------
  private void setBookName(View view, Cursor cursor) {
    setTextView(view, R.id.txt_book_name, cursor, BookEntry.COLUMN_NAME);
  }

  //------------------------------------------------------------------------------
  // setBookPrice
  //------------------------------------------------------------------------------
  private void setBookPrice(View view, Context context, Cursor cursor) {
    int price = CursorUtils.GetCursorInt(cursor, BookEntry.COLUMN_PRICE);
    if (price > 0) {
      String text = buildPriceString(context, price);
      setTextView(view, text, R.id.txt_book_price);
    } else {
      setTextView(view, context.getString(R.string.free), R.id.txt_book_price);
    }
  }

  //------------------------------------------------------------------------------
  // setBookQuantity
  //------------------------------------------------------------------------------
  private int setBookQuantity(View view, Context context, Cursor cursor) {
    int quantity = CursorUtils.GetCursorInt(cursor, BookEntry.COLUMN_QUANTITY);
    if (quantity > 0) {
      String text = buildQuantityString(context, quantity);
      setTextView(view, text, R.id.txt_book_quantity);
    } else {
      setTextView(view, context.getString(R.string.out_of_stock), R.id.txt_book_quantity);
    }

    return(quantity);
  }

  //------------------------------------------------------------------------------
  // setTextView
  //------------------------------------------------------------------------------
  private void setTextView(View view, String value, int textViewResourceId) {
    TextView textView = view.findViewById(textViewResourceId);
    if (textView != null) {
      textView.setText(value);
    }
  }

  //------------------------------------------------------------------------------
  // setTextView
  //------------------------------------------------------------------------------
  private void setTextView(View view, int textViewResourceId, Cursor cursor, String columnName) {
    String value = CursorUtils.GetCursorText(cursor, columnName);
    setTextView(view, value, textViewResourceId);
  }
}