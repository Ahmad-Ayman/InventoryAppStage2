package com.freelance.ahmed.invappstage2final;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ahmed on 2/24/2018.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    public TextView mProductName;
    public TextView mProductPrice;
    public TextView mProductQuantity;
    public Button mSaleButton;
    private int productNameIndex;
    private int productPriceIndex;
    private int productQuantityIndex;
    private int supplierNameIndex;
    private int supplierPhoneIndex;
    private int idIndex;
    private String productnameString;
    private int quantityInteger;
    private double priceDouble;
    private String supplierNameString;
    private String supplierPhoneString;
    private long id;
    private static final String PRODUCT_NAME_KEY = "PRODUCT_NAME_KEY";
    private static final String PRODUCT_PRICE_KEY = "PRODUCT_PRICE_KEY";
    private static final String PRODUCT_QUANTITY_KEY = "PRODUCT_QUANTITY_KEY";
    private static final String SUPPLIER_NAME_KEY = "SUPPLIER_NAME_KEY";
    private static final String SUPPLIER_PHONE_KEY = "SUPPLIER_PHONE_KEY";

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.products_list_item, viewGroup, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        mProductName = view.findViewById(R.id.tv_productName);
        mProductPrice = view.findViewById(R.id.tv_productPrice);
        mProductQuantity = view.findViewById(R.id.tv_productQuantity);
        mSaleButton = view.findViewById(R.id.saleBtn);
        productNameIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_NAME);
        productPriceIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_PRICE);
        productQuantityIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_QUANTITY);
        supplierNameIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME);
        supplierPhoneIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE);
        idIndex = cursor.getColumnIndex(InventoryAppContract.ProductEntry._ID);
        productnameString = cursor.getString(productNameIndex);
        quantityInteger = cursor.getInt(productQuantityIndex);
        priceDouble = cursor.getDouble(productPriceIndex);
        supplierNameString = cursor.getString(supplierNameIndex);
        supplierPhoneString = cursor.getString(supplierPhoneIndex);
        id=cursor.getLong(idIndex);
        mSaleButton.setFocusable(false);
        if (quantityInteger >= 1) {
            mSaleButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   ContentValues values = new ContentValues();
                                                   values.put(InventoryAppContract.ProductEntry.COLOUM_QUANTITY, quantityInteger - 1);
                                                   Uri uri = ContentUris.withAppendedId(InventoryAppContract.ProductEntry.CONTENT_URI, id);
                                                   context.getContentResolver().update(
                                                           uri,
                                                           values,
                                                           InventoryAppContract.ProductEntry._ID + "=?",
                                                           new String[]{String.valueOf(ContentUris.parseId(uri))});
                                               }
                                           }
            );
            mProductName.setText(productnameString);
            mProductQuantity.setText(String.format("Quantity: %s", String.valueOf(quantityInteger)));
            mProductPrice.setText(String.format("Price: $ %s", String.valueOf(priceDouble)));

        }
    }
}
