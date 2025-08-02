package com.example.contactproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "Contact", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Contacts");
        onCreate(db);
    }

    public boolean ajouterContact(String nom, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", nom);
        values.put("phone", phone);

        long result = db.insert("Contacts", null, values);
        return result != -1;
    }

    public boolean modifierContact(int id, String nom, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", nom);
        values.put("phone", phone);
        int rows = db.update("Contacts", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean supprimerContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Contacts", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public List<Contact> listerContacts() {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Contacts", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String tel = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                list.add(new Contact(id, nom, tel));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public List<Contact> filterContactsByName(String searchQuery) {
        List<Contact> filteredList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Contacts WHERE name LIKE ?",
                new String[]{"%" + searchQuery + "%"}
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String tel = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                filteredList.add(new Contact(id, nom, tel));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return filteredList;
    }

}
