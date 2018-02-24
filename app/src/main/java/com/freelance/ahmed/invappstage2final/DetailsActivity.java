package com.freelance.ahmed.invappstage2final;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    private TextInputEditText productName;
    private TextInputEditText productPrice;
    private TextInputEditText productQuantity;
    private TextInputEditText supplierName;
    private TextInputEditText supplierPhone;
    private Uri mCurrentProductUri;
    private Button insertingBtn;
    private Button callSupplier;
    private boolean productChanged = false;
    private Integer Quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
        productName = findViewById(R.id.produtName);
        productPrice = findViewById(R.id.produtPrice);
        productQuantity = findViewById(R.id.productQuantity);
        supplierName = findViewById(R.id.supplierName);
        supplierPhone = findViewById(R.id.supplierPhone);
        insertingBtn = findViewById(R.id.addtoDB);
        callSupplier = findViewById(R.id.callSupplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calling Intent
            }
        });
        insertingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // saving method
                if(validateData());
                {
                    saveProduct();

                }
            }
        });


    }

    private boolean validateData() {
        String pname, pprice, pquantity, suppliername, supplierphone;
        pname = productName.getText().toString().trim();
        pprice = productPrice.getText().toString();
        pquantity = productQuantity.getText().toString();
        suppliername = supplierName.getText().toString().trim();
        supplierphone = supplierPhone.getText().toString();
        boolean isValidate = true;
        if (pname.equals("") || TextUtils.isEmpty(pname)) {
            productName.setError(getResources().getString(R.string.error));
            isValidate = false;
        }
        if (pprice.equals("") || TextUtils.isEmpty(pprice)) {
            productPrice.setError(getResources().getString(R.string.error));
            isValidate = false;
        }else if (Integer.valueOf(productPrice.getText().toString().trim())<0){
            productPrice.setError(getResources().getString(R.string.negativePrice));
            isValidate=false;
        }
        if (pquantity.equals("") || TextUtils.isEmpty(pquantity)) {
            productQuantity.setError(getResources().getString(R.string.error));
            isValidate = false;
        } else if (Integer.valueOf(productQuantity.getText().toString().trim())<0){
            productQuantity.setError(getResources().getString(R.string.negativeQuantity));
            isValidate=false;
        }
        if (suppliername.equals("") || TextUtils.isEmpty(suppliername)) {
            supplierName.setError(getResources().getString(R.string.error));
            isValidate = false;
        }
        if (supplierphone.equals("") | TextUtils.isEmpty(supplierphone)) {
            supplierPhone.setError(getResources().getString(R.string.error));
            isValidate = false;
        }
        return isValidate;

    }

    private void saveProduct() {
        String nameString = productName.getText().toString().trim();
        String priceString = productPrice.getText().toString().trim();
        String quantityString = productQuantity.getText().toString().trim();
        String supplierString = supplierName.getText().toString().trim();
        String supplierPhoneString = supplierPhone.getText().toString().trim();
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryAppContract.ProductEntry.COLOUM_NAME, nameString);
        values.put(InventoryAppContract.ProductEntry.COLOUM_PRICE, priceString);
        values.put(InventoryAppContract.ProductEntry.COLOUM_QUANTITY, quantityString);
        values.put(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME, supplierString);
        values.put(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE, supplierPhoneString);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryAppContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                if (mCurrentProductUri != null) {
                    int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

                    if (rowsDeleted == 0) {
                        Toast.makeText(this, getString(R.string.delete_product_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.delete_product_success),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryAppContract.ProductEntry._ID,
                InventoryAppContract.ProductEntry.COLOUM_NAME,
                InventoryAppContract.ProductEntry.COLOUM_PRICE,
                InventoryAppContract.ProductEntry.COLOUM_QUANTITY,
                InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME,
                InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_NAME);
            int priceIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_PRICE);
            int supplierIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME);
            int supplierPhoneIndex=cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE);
            int quantityIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_QUANTITY);

            String name = cursor.getString(nameIndex);
            double price = cursor.getDouble(priceIndex);
            String supplier = cursor.getString(supplierIndex);
            int quantity = cursor.getInt(quantityIndex);
            String supplierPhoneStringforUpdate=cursor.getString(supplierPhoneIndex);
            productName.setText(name);
            productPrice.setText(String.valueOf(price));
            productQuantity.setText(String.valueOf(quantity));
            supplierName.setText(supplier);
            supplierPhone.setText(supplierPhoneStringforUpdate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        productQuantity.setText("");
        supplierName.setText("");
        supplierPhone.setText("");

    }
}
