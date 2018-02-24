package com.freelance.ahmed.invappstage2final;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ahmed on 2/24/2018.
 */

public class InventoryAppProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryAppProvider.class.getSimpleName();
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher mInventoryUriMatcher = buildProductsUriMatcher();
    public static UriMatcher buildProductsUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Directory All Products
        uriMatcher.addURI(InventoryAppContract.CONTENT_AUTHORITY, InventoryAppContract.PATH_PRODUCT, PRODUCT);
        // Single Product
        uriMatcher.addURI(InventoryAppContract.CONTENT_AUTHORITY, InventoryAppContract.PATH_PRODUCT + "/#", PRODUCT_ID);
        return uriMatcher;
    }
    private InventoryAppDbHelper inventoryAppDbHelper;
    @Override
    public boolean onCreate() {
        inventoryAppDbHelper = new InventoryAppDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = inventoryAppDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = mInventoryUriMatcher.match(uri);
        switch (match) {

            case PRODUCT:
                cursor = database.query(InventoryAppContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryAppContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryAppContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query because it is unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = mInventoryUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProductMethod(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion isn't supported with ur " + uri);
        }
    }

    private Uri insertProductMethod(Uri uri, ContentValues values) {
        SQLiteDatabase db = inventoryAppDbHelper.getWritableDatabase();
        String name = values.getAsString(InventoryAppContract.ProductEntry.COLOUM_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("product name is missing");
        }
        Double price = values.getAsDouble(InventoryAppContract.ProductEntry.COLOUM_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("product price is missing");
        }
        Integer quantity = values.getAsInteger(InventoryAppContract.ProductEntry.COLOUM_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("product quantity is missing");
        }
        String supplierName = values.getAsString(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("product Supplier Name is missing");
        }
        String supplierPhone = values.getAsString(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("product Supplier Phone is missing");
        }
        long id = db.insert(InventoryAppContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "It is failed to insert a row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);




    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = inventoryAppDbHelper.getWritableDatabase();
        int NoRowsDeleted;

        final int match = mInventoryUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                NoRowsDeleted = database.delete(InventoryAppContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryAppContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                NoRowsDeleted = database.delete(InventoryAppContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deleting is not supported here for " + uri);
        }
        if (NoRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return NoRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = mInventoryUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProductData(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = InventoryAppContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProductData(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Updating isn't supported here for " + uri);
        }
    }
    private int updateProductData(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = inventoryAppDbHelper.getWritableDatabase();
        if (contentValues.containsKey(InventoryAppContract.ProductEntry.COLOUM_NAME)) {
            String name = contentValues.getAsString(InventoryAppContract.ProductEntry.COLOUM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("product name is missing");
            }
        }

        if (contentValues.containsKey(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME)) {
            String supplier = contentValues.getAsString(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("supplier name is missing");
            }
        }

        if (contentValues.containsKey(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE)) {
            String supplierPhone = contentValues.getAsString(InventoryAppContract.ProductEntry.COLOUM_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("supplier Phone is missing");
            }
        }

        if (contentValues.containsKey(InventoryAppContract.ProductEntry.COLOUM_PRICE)) {
            Integer price = contentValues.getAsInteger(InventoryAppContract.ProductEntry.COLOUM_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("product price is missing");
            }
        }
        if (contentValues.containsKey(InventoryAppContract.ProductEntry.COLOUM_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryAppContract.ProductEntry.COLOUM_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("product quantity is missing");
            }
        }
        if (contentValues.size() == 0) {
            return 0;
        }
        int rowsUpdated = db.update(InventoryAppContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
