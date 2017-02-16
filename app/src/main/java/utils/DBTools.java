package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database tool for the phone's database
 */

public class DBTools extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "GoalsMadeAttainable.db";
    private static final int DATABASE_VERSION = 1;

    // Create table to store token
    private static final String TABLE_TOKEN = "token";
    private static final String COLUMN_TOKEN_ID = "id";
    private static final String COLUMN_TOKEN_VALUE = "value";
    private static final String TABLE_TOKEN_CREATE =
            "CREATE TABLE " + TABLE_TOKEN + " (" + COLUMN_TOKEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TOKEN_VALUE + " TEXT NOT NULL)";

    public DBTools(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_TOKEN_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
        onCreate(sqLiteDatabase);
    }

    /**
     * Creates a token entry in the database for the user of this phone.
     * @param value represents the value of the token returned by the web service
     * @return returns the token back after the call
     * @throws SQLiteConstraintException
     */
    public String createToken(String value) throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOKEN_VALUE, value);
        database.insertOrThrow(TABLE_TOKEN, null, values);
        database.close();
        return value;
    }

    public String getToken() {
        SQLiteDatabase database = this.getReadableDatabase();
        // Get everything out of the table
        Cursor cursor = database.query(TABLE_TOKEN, null, null, null, null, null, null, null);
        // Move to the last row and get the value of the token
        cursor.moveToLast();
        String token = cursor.getString(1);
        cursor.close();
        return token;
    }

    /**
     * Deletes all of the tokens in the table.
     * @throws SQLiteConstraintException
     */
    public void deleteTokens() throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TOKEN, null, null);
        database.close();
    }
}
