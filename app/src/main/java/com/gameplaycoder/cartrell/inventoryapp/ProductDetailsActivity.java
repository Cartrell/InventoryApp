package com.gameplaycoder.cartrell.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gameplaycoder.cartrell.inventoryapp.data.BookContract.BookEntry;

import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
  ////////////////////////////////////////////////////////////////////////////////
  // static / const
  ////////////////////////////////////////////////////////////////////////////////
  private static final int LOADER_ID = 1;
  private static final String TEL_URI_PREFIX = "tel:";
  private static final String EMAIL_URI_PREFIX = "mailto:";

  ////////////////////////////////////////////////////////////////////////////////
  // members
  ////////////////////////////////////////////////////////////////////////////////
  private View.OnTouchListener mTouchListener;

  private EditText mEditBookName;
  private EditText mEditBookPrice;
  private EditText mEditBookSupplier;
  private EditText mEditBookSupplierPhone;
  private EditText mEditBookQuantity;
  private CheckBox mChkIsAudiobookAvailable;

  private Uri mUri;
  private int mQuantity;
  private boolean mHasDataChanged;

  ////////////////////////////////////////////////////////////////////////////////
  // public
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // onBackPressed
  //------------------------------------------------------------------------------
  public void onBackPressed() {
    // If the book hasn't changed, continue with handling back button press
    if (!mHasDataChanged) {
      super.onBackPressed();
      return;
    }

    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
    // Create a click listener to handle the user confirming that changes should be discarded.
    DialogInterface.OnClickListener discardButtonClickListener =
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          // User clicked "Discard" button, close the current activity.
          finish();
        }
      };

    // Show dialog that there are unsaved changes
    showUnsavedChangesDialog(discardButtonClickListener);
  }

  //------------------------------------------------------------------------------
  // onCreateLoader
  //------------------------------------------------------------------------------
  @Override
  public Loader onCreateLoader(int id, Bundle args) {
    return(new CursorLoader(this, mUri, null, null,
      null, null));
  }

  //------------------------------------------------------------------------------
  // onCreateOptionsMenu
  //------------------------------------------------------------------------------
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_product_details, menu);
    return(true);
  }

  //------------------------------------------------------------------------------
  // onLoadFinished
  //------------------------------------------------------------------------------
  @Override
  public void onLoadFinished(@NonNull Loader loader, Cursor cursor) {
    if (cursor == null || cursor.getCount() < 1 || !cursor.moveToFirst()) {
      return;
    }

    setViewFromCursor(R.id.edit_book_name, cursor, BookEntry.COLUMN_NAME);
    setViewFromCursor(R.id.edit_book_price, cursor, BookEntry.COLUMN_PRICE);
    setViewFromCursor(R.id.edit_book_supplier, cursor, BookEntry.COLUMN_SUPPLIER);
    setViewFromCursor(R.id.edit_book_supplier_phone, cursor, BookEntry.COLUMN_SUPPLIER_PHONE);
    initQuantity(cursor);
    setViewFromCursor(R.id.chk_is_audiobook, cursor, BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE);
  }

  //------------------------------------------------------------------------------
  // onLoaderReset
  //------------------------------------------------------------------------------
  @Override
  public void onLoaderReset(@NonNull Loader loader) {
    mEditBookName.setText("");
    mEditBookPrice.setText("");
    mEditBookSupplier.setText("");
    mEditBookSupplierPhone.setText("");
    mEditBookQuantity.setText("");
    mChkIsAudiobookAvailable.setChecked(false);
  }

  //------------------------------------------------------------------------------
  // onOptionsItemSelected
  //------------------------------------------------------------------------------
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // User clicked on a menu option in the app bar overflow menu
    switch (item.getItemId()) {
      case R.id.action_save:
        if (isDataOK()) {
          saveBookData();
          finish();
        } else {
          Toast.makeText(this, R.string.all_data_fields, Toast.LENGTH_SHORT).show();
        }

        return(true);

      case R.id.action_delete:
        showDeleteConfirmationDialog();
        return(true);

      case android.R.id.home:
        if (!mHasDataChanged) {
          NavUtils.navigateUpFromSameTask(this);
          return(true);
        }

        DialogInterface.OnClickListener discardButtonClickListener =
          new DialogInterface.OnClickListener() {

            //--------------------------------------------------------------------------
            // onClick
            //--------------------------------------------------------------------------
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              // User clicked "Discard" button, navigate to parent activity.
              NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
            }
          };

        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  //------------------------------------------------------------------------------
  // onPrepareOptionsMenu
  //------------------------------------------------------------------------------
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    //if this is a new book, hide the delete menu item.
    if (mUri == null) {
      MenuItem menuItem = menu.findItem(R.id.action_delete);
      menuItem.setVisible(false);
    }
    return(true);
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
    setContentView(R.layout.activity_product_details);
    initViewOnTouchListener();

    mEditBookName = initEditText(R.id.edit_book_name);
    mEditBookQuantity = initEditText(R.id.edit_book_quantity);
    mEditBookSupplierPhone = initEditText(R.id.edit_book_supplier_phone);
    mEditBookSupplier = initEditText(R.id.edit_book_supplier);
    mEditBookPrice = initEditText(R.id.edit_book_price);
    mChkIsAudiobookAvailable = initCheckBox(R.id.chk_is_audiobook);

    initAddQuantityButton();
    initRemoveQuantityButton();
    initOrderButton();

    Intent intent = getIntent();
    mUri = intent.getData();
    if (mUri == null) {
      //the intent doesnt contain a db URI, so we're creating a new book
      setTitle(getString(R.string.add_a_new_book));
      invalidateOptionsMenu();
    } else {
      //the intent contains a db URI, so we're editing an existing book
      setTitle(getString(R.string.edit_this_book));
      getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // private
  ////////////////////////////////////////////////////////////////////////////////

  //------------------------------------------------------------------------------
  // alertCantContactSupplier
  //------------------------------------------------------------------------------
  private void alertCantContactSupplier() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.cant_contact_supplier);
    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

      //------------------------------------------------------------------------------
      // onClick
      //------------------------------------------------------------------------------
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  //------------------------------------------------------------------------------
  // contactViaEmail
  //------------------------------------------------------------------------------
  private boolean contactViaEmail() {
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse(EMAIL_URI_PREFIX));
    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_request));

    String emailMessage = formatEmailMessage();
    intent.putExtra(Intent.EXTRA_TEXT, emailMessage);

    if (intent.resolveActivity(getPackageManager()) == null) {
      return(false);
    }
    startActivity(intent);
    return(true);
  }

  //------------------------------------------------------------------------------
  // contactViaPhone
  //------------------------------------------------------------------------------
  private boolean contactViaPhone() {
    String phoneNumber = mEditBookSupplierPhone.getEditableText().toString().trim();
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse(TEL_URI_PREFIX + phoneNumber));
    if (intent.resolveActivity(getPackageManager()) == null) {
      return(false);
    }
    startActivity(intent);
    return(true);
  }

  //------------------------------------------------------------------------------
  // deleteBook
  //------------------------------------------------------------------------------
  private void deleteBook() {
    if (mUri == null) {
      return;
    }

    int rowsDeleted = getContentResolver().delete(mUri, null, null);
    if (rowsDeleted > 0) {
      Toast.makeText(this, R.string.book_deleted, Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, R.string.couldnt_delete_book, Toast.LENGTH_SHORT).show();
    }

    finish();
  }

  //------------------------------------------------------------------------------
  // formatEmailMessage
  //------------------------------------------------------------------------------
  private String formatEmailMessage() {
    String formatString = getString(R.string.order_book_email_msg_format);
    String bookName = mEditBookName.getText().toString().trim();
    return(String.format(Locale.getDefault(), formatString, bookName));
  }

  //------------------------------------------------------------------------------
  // initAddQuantityButton
  //------------------------------------------------------------------------------
  private void initAddQuantityButton() {
    Button button = findViewById(R.id.btn_add_quantity);
    button.setOnClickListener(new View.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      @Override
      public void onClick(View v) {
        mQuantity++;
        mEditBookQuantity.setText(String.valueOf(mQuantity));
        mHasDataChanged = true;
      }
    });
  }

  //------------------------------------------------------------------------------
  // initCheckBox
  //------------------------------------------------------------------------------
  private CheckBox initCheckBox(int checkBoxResourceId) {
    CheckBox checkBox = findViewById(checkBoxResourceId);
    checkBox.setOnTouchListener(mTouchListener);
    return(checkBox);
  }

  //------------------------------------------------------------------------------
  // initEditText
  //------------------------------------------------------------------------------
  private EditText initEditText(int editTextResourceId) {
    EditText editText = findViewById(editTextResourceId);
    editText.setOnTouchListener(mTouchListener);
    return(editText);
  }

  //------------------------------------------------------------------------------
  // initOrderButton
  //------------------------------------------------------------------------------
  private void initOrderButton() {
    Button button = findViewById(R.id.btn_order);
    button.setOnClickListener(new View.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      @Override
      public void onClick(View v) {
        if (contactViaPhone()) {
          return;
        }

        if (contactViaEmail()) {
          return;
        }

        alertCantContactSupplier();
      }
    });
  }

  //------------------------------------------------------------------------------
  // initRemoveQuantityButton
  //------------------------------------------------------------------------------
  private void initRemoveQuantityButton() {
    Button button = findViewById(R.id.btn_remove_quantity);
    button.setOnClickListener(new View.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      @Override
      public void onClick(View v) {
        //quantity cant dip below zero
        if (mQuantity > 0) {
          mQuantity--;
          mEditBookQuantity.setText(String.valueOf(mQuantity));
          mHasDataChanged = true;
        }
      }
    });
  }

  //------------------------------------------------------------------------------
  // initViewOnTouchListener
  //------------------------------------------------------------------------------
  private void initViewOnTouchListener() {
    mTouchListener = new View.OnTouchListener() {

      //--------------------------------------------------------------------------
      // onTouch
      //--------------------------------------------------------------------------
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        mHasDataChanged = true;
        return(false);
      }
    };
  }

  //------------------------------------------------------------------------------
  // isDataOK
  //------------------------------------------------------------------------------
  private boolean isDataOK() {
    //all fields are required, except for the audiobook setting
    return(isEditTextOk(mEditBookName) &&
      isEditTextOk(mEditBookPrice) &&
      isEditTextOk(mEditBookQuantity) &&
      isEditTextOk(mEditBookSupplier) &&
      isEditTextOk(mEditBookSupplierPhone));
  }

  //------------------------------------------------------------------------------
  // isEditTextOk
  //------------------------------------------------------------------------------
  private boolean isEditTextOk(EditText editText) {
    return(!TextUtils.isEmpty(editText.getText().toString().trim()));
  }

  //------------------------------------------------------------------------------
  // initQuantity
  //------------------------------------------------------------------------------
  private void initQuantity(Cursor cursor) {
    mQuantity = CursorUtils.GetCursorInt(cursor, BookEntry.COLUMN_QUANTITY);

    EditText editText = findViewById(R.id.edit_book_quantity);
    editText.setText(String.valueOf(mQuantity));
    editText.addTextChangedListener(new TextWatcher() {

      //------------------------------------------------------------------------------
      // afterTextChanged
      //------------------------------------------------------------------------------
      @Override
      public void afterTextChanged(Editable editable) {
        try {
          mQuantity = Integer.parseInt(editable.toString());
        } catch (NumberFormatException e) {
          mQuantity = 0;
        }
      }

      //------------------------------------------------------------------------------
      // beforeTextChanged
      //------------------------------------------------------------------------------
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      //------------------------------------------------------------------------------
      // onTextChanged
      //------------------------------------------------------------------------------
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
  }

  //------------------------------------------------------------------------------
  // saveBookData
  //------------------------------------------------------------------------------
  private void saveBookData() {
    ContentValues values = new ContentValues();

    //add simple ui content to content values
    values.put(BookEntry.COLUMN_NAME, mEditBookName.getText().toString().trim());
    values.put(BookEntry.COLUMN_SUPPLIER, mEditBookSupplier.getText().toString().trim());
    values.put(BookEntry.COLUMN_SUPPLIER_PHONE, mEditBookSupplierPhone.getText().toString().trim());
    values.put(BookEntry.COLUMN_IS_AUDIOBOOK_AVAILABLE, mChkIsAudiobookAvailable.isChecked());

    //add price to content values
    String priceString = mEditBookPrice.getText().toString().trim();
    int price = 0;
    if (!TextUtils.isEmpty(priceString)) {
      price = Integer.parseInt(priceString);
    }

    values.put(BookEntry.COLUMN_PRICE, price);

    //add quantity to content values
    String quantityString = mEditBookQuantity.getText().toString().trim();
    int quantity = 0;
    if (!TextUtils.isEmpty(quantityString)) {
      quantity = Integer.parseInt(quantityString);
    }

    values.put(BookEntry.COLUMN_QUANTITY, quantity);

    //modify the db, depending on uri
    if (mUri == null) {
      //inserting a new book to the db
      Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
      if (uri != null) {
        Toast.makeText(this, R.string.new_book_inserted, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, R.string.error_inserting_new_book, Toast.LENGTH_SHORT).show();
      }
    } else {
      //editing an existing book in the db
      int rowsAffected = getContentResolver().update(mUri, values, null, null);
      if (rowsAffected > 0) {
        Toast.makeText(this, R.string.book_updated, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, R.string.error_updating_book, Toast.LENGTH_SHORT).show();
      }
    }
  }

  //------------------------------------------------------------------------------
  // setViewFromCursor
  //------------------------------------------------------------------------------
  private void setViewFromCursor(int viewResourceId, Cursor cursor, String columnName) {
    View view = findViewById(viewResourceId);
    if (view instanceof EditText) {
      CursorUtils.SetEditText((EditText)view, cursor, columnName);
    } else if (view instanceof CheckBox) {
      CursorUtils.SetCheckBox((CheckBox)view, cursor, columnName);
    }
  }

  //------------------------------------------------------------------------------
  // showDeleteConfirmationDialog
  //------------------------------------------------------------------------------
  private void showDeleteConfirmationDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.delete_this_book);
    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      public void onClick(DialogInterface dialog, int id) {
        deleteBook();
      }
    });

    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      public void onClick(DialogInterface dialog, int id) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  //------------------------------------------------------------------------------
  // showUnsavedChangesDialog
  //------------------------------------------------------------------------------
  private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.discard_changes);
    builder.setPositiveButton(R.string.discard, discardButtonClickListener);
    builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {

      //--------------------------------------------------------------------------
      // onClick
      //--------------------------------------------------------------------------
      public void onClick(DialogInterface dialog, int id) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    // Create and show the AlertDialog
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }
}