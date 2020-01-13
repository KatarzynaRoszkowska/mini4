package pl.roszkowska.geokr;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ShopDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATA_BASE = "shops";
    private static final String TABLE_NAME = "shopTable";
    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_NAME +
            " (" +
            "NAME TEXT PRIMARY KEY, " +
            "DESCRIPTION TEXT, " +
            "R REAL, " +
            "LAT REAL, " +
            "LON REAL" +
            ");";

    public ShopDbHelper(Context context) {
        super(context, DATA_BASE, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Shop> getAllShops(){
        String sql = "select * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Shop> orders = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(0);
                String description = cursor.getString(1);
                double r = cursor.getDouble(2);
                double lat = cursor.getDouble(3);
                double lon = cursor.getDouble(4);

                orders.add(new Shop(name, description, r, lat, lon));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public void add(Shop shop){
        ContentValues values = new ContentValues();
        values.put("NAME", shop.getName());
        values.put("DESCRIPTION", shop.getDescription());
        values.put("R", shop.getR());
        values.put("LAT", shop.getLat());
        values.put("LON", shop.getLon());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public void update(Shop shop){
        ContentValues values = new ContentValues();
        values.put("NAME", shop.getName());
        values.put("DESCRIPTION", shop.getDescription());
        values.put("R", shop.getR());
        values.put("LAT", shop.getLat());
        values.put("LON", shop.getLon());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME, values, "NAME	= ?", new String[] { String.valueOf(shop.getName())});
    }

    public void delete(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, 	"NAME = ?", new String[] { name});
    }
}
