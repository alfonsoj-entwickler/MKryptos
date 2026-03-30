package com.example.quoson.mkryptos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataKeys — SQLite database helper for persistent cryptographic key storage.
 *
 * Manages a single table called {@code KeysCipher} with the following schema:
 *
 *   id       INTEGER PRIMARY KEY AUTOINCREMENT  — Unique row identifier
 *   name     TEXT                               — User-defined label for the key
 *   type     TEXT                               — Algorithm type (e.g. "AES", "RSA", "EC")
 *   yourKey  TEXT                               — Base64-encoded key material
 *   myDate   TEXT                               — Timestamp when the key was saved
 *
 * Usage:
 *   DataKeys db = new DataKeys(context, "DBKeys", null, 4);
 *   SQLiteDatabase writableDb = db.getWritableDatabase();
 *
 * The database file is named "DBKeys" and the schema version is 4.
 * On version upgrade, the existing table is dropped and recreated
 * (all stored keys are lost on upgrade — no migration is performed).
 */
public class DataKeys extends SQLiteOpenHelper {

    // SQL statement used to create the KeysCipher table on first run or after an upgrade
    private String sqlKeysCreate = "CREATE TABLE KeysCipher( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "type TEXT, " +
            "yourKey TEXT, " +
            "myDate TEXT )";

    /**
     * Constructs a new DataKeys helper.
     *
     * @param c Context required by SQLiteOpenHelper.
     * @param n Database file name (e.g. "DBKeys").
     * @param f Optional CursorFactory (pass null for the default).
     * @param v Database schema version number.
     */
    public DataKeys( Context c, String n, SQLiteDatabase.CursorFactory f, int v ) {
        super( c, n, f, v );
    }

    /**
     * Called when the database is created for the first time.
     * Executes the CREATE TABLE statement for KeysCipher.
     *
     * @param db The newly created database.
     */
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL(this.sqlKeysCreate);
    }

    /**
     * Called when the database schema version has changed.
     * Drops the existing KeysCipher table and recreates it from scratch.
     *
     * WARNING: This destroys all previously stored keys. No data migration
     * is performed between versions.
     *
     * @param db          The database to upgrade.
     * @param version_old The old schema version.
     * @param version_new The new schema version.
     */
    @Override
    public void onUpgrade( SQLiteDatabase db, int version_old, int version_new ) {
        db.execSQL( "DROP TABLE IF EXISTS KeysCipher" );
        db.execSQL( this.sqlKeysCreate );
    }
}
