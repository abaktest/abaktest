package com.example.abaktest.data;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "abakTest.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createProductsList =
			"CREATE TABLE " + DBData.ProductsTable.TABLE_NAME + " (" +
					DBData.ProductsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
					COMMA_SEP + DBData.ProductsTable.COLUMN_ID + INTEGER_TYPE + 
		    		COMMA_SEP + DBData.ProductsTable.COLUMN_NAME + TEXT_TYPE + 
		    		COMMA_SEP + DBData.ProductsTable.COLUMN_IMAGES_CNT + INTEGER_TYPE + 
		    " )";

		db.execSQL(createProductsList);
		
		String createImagesList =
				"CREATE TABLE " + DBData.ImagesTable.TABLE_NAME + " (" +
						DBData.ImagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
			    		COMMA_SEP + DBData.ImagesTable.COLUMN_ID + INTEGER_TYPE + 
			    		COMMA_SEP + DBData.ImagesTable.COLUMN_PRODUCT_ID + INTEGER_TYPE + 
			    		COMMA_SEP + DBData.ImagesTable.COLUMN_POSITION + INTEGER_TYPE + 
			    		COMMA_SEP + DBData.ImagesTable.COLUMN_PATH_THUMB + TEXT_TYPE + 
			    		COMMA_SEP + DBData.ImagesTable.COLUMN_PATH_BIG + TEXT_TYPE + 
			    " )";

		db.execSQL(createImagesList);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		manualReset(db);
	}
	
	public void manualResetBase(){
		manualReset(getWritableDatabase());
	}

	private void manualReset(SQLiteDatabase db)
	{
		dropAllTables(db);
	    onCreate(db);
	}
	
	private void dropAllTables(SQLiteDatabase db){
		db.execSQL( "DROP TABLE IF EXISTS " + DBData.ImagesTable.TABLE_NAME);
		db.execSQL( "DROP TABLE IF EXISTS " + DBData.ProductsTable.TABLE_NAME);
	}
	
	public void dropAllTables(){
		SQLiteDatabase db = getWritableDatabase();
		dropAllTables(db);
	}
	
	public void putProductsList(ArrayList<Product> productList) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		for (Product product : productList) {
			ContentValues values = new ContentValues();
			values.put(DBData.ProductsTable.COLUMN_ID, product.id);
			values.put(DBData.ProductsTable.COLUMN_IMAGES_CNT, product.imagesCount);
			values.put(DBData.ProductsTable.COLUMN_NAME, product.name);
			

			int updated = 0;

			updated = db.update(DBData.ProductsTable.TABLE_NAME, 
					values, 
					DBData.ProductsTable.COLUMN_ID + "=?",
					new String[] {String.valueOf(product.id)});
			
			if (updated == 0) {
				db.insert(DBData.ProductsTable.TABLE_NAME, null, values);
			}
				
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void putImagesList(ArrayList<Image> imagesList) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		for (Image image : imagesList) {
			ContentValues values = new ContentValues();
			values.put(DBData.ImagesTable.COLUMN_ID, image.id);
			values.put(DBData.ImagesTable.COLUMN_PRODUCT_ID, image.productId);
			values.put(DBData.ImagesTable.COLUMN_PATH_BIG, image.pathBig);
			values.put(DBData.ImagesTable.COLUMN_PATH_THUMB, image.pathThumb);
			values.put(DBData.ImagesTable.COLUMN_POSITION, image.position);
			

			int updated = 0;

			updated = db.update(DBData.ImagesTable.TABLE_NAME, 
					values, 
					DBData.ImagesTable.COLUMN_ID + "=?",
					new String[] {String.valueOf(image.id)});
			
			if (updated == 0) {
				db.insert(DBData.ImagesTable.TABLE_NAME, null, values);
			}
				
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public Image getFirstImageByProduct(Product product){
		SQLiteDatabase db = getReadableDatabase();
		
		ArrayList<Image> images = new ArrayList<Image>();
		
		String tableName = DBData.ImagesTable.TABLE_NAME;
		
        String select = "*";

        String queryString =
                "SELECT " + select +
                " FROM " + tableName;
        
        queryString += " WHERE " + " ( ( " + DBData.ImagesTable.COLUMN_PRODUCT_ID +" = " + product.id + " ) AND"
        		+ "(" + DBData.ImagesTable.COLUMN_POSITION + " = 1" + ") ) ";
 
        
        Cursor cursor = db.rawQuery(queryString, new String[]{});
        Image image = null;
        if (cursor != null && cursor.moveToFirst()) {
            images = ImageCursorToList(cursor);
            if(images.size() == 1){
            	image = images.get(0);
            }
        }
        return image;
	}
	
	public ArrayList<Product> getProducts(){
		SQLiteDatabase db = getReadableDatabase();
		
		ArrayList<Product> products = new ArrayList<Product>();
		
		String tableName = DBData.ProductsTable.TABLE_NAME;
		
        String select = "*";

        String queryString =
                "SELECT " + select +
                " FROM " + tableName;
 
        Cursor cursor = db.rawQuery(queryString, new String[]{});
        
        if (cursor != null && cursor.moveToFirst()) {
            products = ProductsCursorToList(cursor);
            
        }
        return products;
	}
	
	public ArrayList<Image> ImageCursorToList(Cursor cursor) {
		ArrayList<Image> result = new ArrayList<Image>();
		
		while (!cursor.isAfterLast()) {
			Image image = new Image();
			image.id = cursor.getInt(cursor.getColumnIndex(DBData.ImagesTable.COLUMN_ID));
			image.pathBig = cursor.getString(cursor.getColumnIndex(DBData.ImagesTable.COLUMN_PATH_BIG));
			image.pathThumb = cursor.getString(cursor.getColumnIndex(DBData.ImagesTable.COLUMN_PATH_THUMB));
			image.position = cursor.getInt(cursor.getColumnIndex(DBData.ImagesTable.COLUMN_POSITION));
			result.add(image);
			cursor.moveToNext();
		}
		return result;
	}
	
	public ArrayList<Product> ProductsCursorToList(Cursor cursor){
		ArrayList<Product> result = new ArrayList<Product>();
		
		while (!cursor.isAfterLast()) {
			Product product = new Product();
			product.id = cursor.getInt(cursor.getColumnIndex(DBData.ProductsTable.COLUMN_ID));
			product.imagesCount = cursor.getInt(cursor.getColumnIndex(DBData.ProductsTable.COLUMN_IMAGES_CNT));
			product.name = cursor.getString(cursor.getColumnIndex(DBData.ProductsTable.COLUMN_NAME));
			result.add(product);
			cursor.moveToNext();
		}
		return result;
	}
	
	

}
