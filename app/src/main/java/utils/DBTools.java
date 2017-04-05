package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.session.PlaybackState;
import android.text.BoringLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Database tool for the phone's database
 */

public class DBTools extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "GoalsMadeAttainable.db";
    private static final int DATABASE_VERSION = 2;

    // Create setup for user representation in database
    private static final String USER_TABLE = "user";
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_COLUMN_FIRST_NAME = "first_name";
    private static final String USER_COLUMN_LAST_NAME = "last_name";
    private static final String USER_COLUMN_USERNAME = "username";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_TOKEN = "token";
    private static final String USER_COLUMN_ACTIVE = "active";
    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + USER_TABLE + " (" + USER_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + USER_COLUMN_FIRST_NAME + " TEXT, " + USER_COLUMN_LAST_NAME + " TEXT, "
                    + USER_COLUMN_USERNAME + " TEXT, " + USER_COLUMN_EMAIL + " TEXT, "
                    + USER_COLUMN_TOKEN + " TEXT UNIQUE, " + USER_COLUMN_ACTIVE + " BOOLEAN)";

    // Create setup for goal representation in database
    private static final String GOAL_TABLE = "goal";
    private static final String GOAL_COLUMN_ID = "id";
    private static final String GOAL_COLUMN_FUTURE_ID = "future_id";
    private static final String GOAL_COLUMN_TITLE = "title";
    private static final String GOAL_COLUMN_DESCRIPTION = "description";
    private static final String GOAL_COLUMN_COMMENT = "comment";
    private static final String GOAL_COLUMN_CREATED_AT = "created_at";
    private static final String GOAL_COLUMN_EXPECTED_COMPLETION = "expected_completion";
    private static final String GOAL_COLUMN_FINISHED_AT = "finished_at";
    private static final String GOAL_COLUMN_LAST_MODIFIED = "last_modified";
    private static final String GOAL_COLUMN_ARCHIVED = "archived";
    private static final String GOAL_TABLE_CREATE =
            "CREATE TABLE " + GOAL_TABLE + " (" + GOAL_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + GOAL_COLUMN_FUTURE_ID + " INTEGER, " + GOAL_COLUMN_TITLE
                    + " TEXT, " + GOAL_COLUMN_DESCRIPTION + " TEXT, " + GOAL_COLUMN_COMMENT + " TEXT, "
                    + GOAL_COLUMN_CREATED_AT + " DATETIME, "
                    + GOAL_COLUMN_EXPECTED_COMPLETION + " DATETIME, "
                    + GOAL_COLUMN_FINISHED_AT + " DATETIME, "
                    + GOAL_COLUMN_LAST_MODIFIED + " DATETIME, "
                    + GOAL_COLUMN_ARCHIVED + " BOOLEAN, FOREIGN KEY (" + GOAL_COLUMN_FUTURE_ID
                    + ") REFERENCES " + GOAL_TABLE + " (" + GOAL_COLUMN_ID + "))";

    // Create setup for user having goals in database
    private static final String USER_HAS_GOALS_TABLE = "user_has_goals";
    private static final String USER_HAS_GOALS_COLUMN_USER_ID = "user_id";
    private static final String USER_HAS_GOALS_COLUMN_GOAL_ID = "goal_id";
    private static final String USER_HAS_GOALS_TABLE_CREATE =
            "CREATE TABLE " + USER_HAS_GOALS_TABLE + " (" + USER_HAS_GOALS_COLUMN_USER_ID + " INTEGER REFERENCES "
                    + USER_TABLE + "(" + USER_COLUMN_ID + "), " + USER_HAS_GOALS_COLUMN_GOAL_ID
                    + " INTEGER REFERENCES " + GOAL_TABLE + "(" + GOAL_COLUMN_ID + "))";

    public DBTools(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USER_TABLE_CREATE);
        sqLiteDatabase.execSQL(GOAL_TABLE_CREATE);
        sqLiteDatabase.execSQL(USER_HAS_GOALS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_HAS_GOALS_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     * Creates the user inside of this database.
     * @param user represents the user returned by the web service
     * @throws SQLiteConstraintException
     */
    public void createUser(User user) throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_ID, user.userID);
        values.put(USER_COLUMN_TOKEN, user.token);
        values.put(USER_COLUMN_FIRST_NAME, user.firstName);
        values.put(USER_COLUMN_LAST_NAME, user.lastName);
        values.put(USER_COLUMN_USERNAME, user.username);
        values.put(USER_COLUMN_EMAIL, user.email);
        database.insertOrThrow(USER_TABLE, null, values);
        database.close();
    }

    /**
     * Checks to see if a token already exists in the database
     * @param token check this token in database
     * @return whether the token is already in the database or not
     * @throws SQLiteConstraintException
     */
    public boolean checkUserExistsByToken(String token) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(USER_TABLE, null, USER_COLUMN_TOKEN + "='" + token + "'",
                                       null, null, null, null, null);
        Boolean status = cursor.getCount() > 0;
        cursor.close();
        return status;
    }

    public boolean checkActiveUserExists() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(USER_TABLE, null, USER_COLUMN_ACTIVE + "=1",
                                       null, null, null, null, null);
        Boolean status = cursor.getCount() > 0;
        cursor.close();
        return status;
    }


    /**
     * Gets the active user out of the database
     * @return the active user from the database
     */
    public User getActiveUser() {
        // Get the token for the active user.
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(USER_TABLE, null, USER_COLUMN_ACTIVE + "=1",
                                       null, null, null, null, null);
        // Since token is unique, cursor should have only one row
        User user = new User();
        user.userID = cursor.getInt(0);
        user.firstName = cursor.getString(1);
        user.lastName = cursor.getString(2);
        user.username = cursor.getString(3);
        user.email = cursor.getString(4);
        user.token = cursor.getString(5);
        cursor.close();
        return user;
    }

    /**
     * Sets the active user in the database.
     * @param token of given user
     */
    public void setActiveUser(String token) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_ACTIVE, 1);
        database.update(USER_TABLE, values, USER_COLUMN_TOKEN + "='" + token + "'", null);
    }

    /**
     * Will remove all active users from the database
     */
    public void removeActiveUsers() {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_ACTIVE, 0);
        database.update(USER_TABLE, values, USER_COLUMN_ACTIVE + "=1", null);
    }

    /**
     * Gets the token of the active user out of the database
     * @return the string representation of the token of the active user
     */
    public String getToken() {
        SQLiteDatabase database = this.getReadableDatabase();
        // Get the active user out of the database
        Cursor cursor = database.query(USER_TABLE, null, USER_COLUMN_ACTIVE + "=1",
                                       null, null, null, null, null);
        // Move to the last row and get the value of the token
        cursor.moveToLast();
        String token;
        try {
            token = cursor.getString(5);
        } catch (CursorIndexOutOfBoundsException e) {
            token = "";
        }
        cursor.close();
        return token;
    }

    /**
     * Creates or updates the goal
     * @param goal goal to be updated
     * @throws SQLiteConstraintException
     */
    public void createOrUpdateGoal(Goal goal) throws SQLiteConstraintException {
        SQLiteDatabase database = this.getWritableDatabase();
        // Create goal in goal table
        ContentValues values = new ContentValues();
        values.put(GOAL_COLUMN_ID, goal.goalID);
        values.put(GOAL_COLUMN_FUTURE_ID, goal.futureGoalID);
        values.put(GOAL_COLUMN_TITLE, goal.title);
        values.put(GOAL_COLUMN_DESCRIPTION, goal.description);
        values.put(GOAL_COLUMN_COMMENT, goal.comment);
        values.put(GOAL_COLUMN_CREATED_AT, goal.createdAt);
        values.put(GOAL_COLUMN_EXPECTED_COMPLETION, goal.expectedCompletion);
        values.put(GOAL_COLUMN_FINISHED_AT, goal.finishedAt);
        values.put(GOAL_COLUMN_LAST_MODIFIED, goal.lastModified);
        values.put(GOAL_COLUMN_ARCHIVED, goal.archived);
        try {
            database.insertOrThrow(GOAL_TABLE, null, values);
            // Create link in user has goal table
            values = new ContentValues();
            values.put(USER_HAS_GOALS_COLUMN_USER_ID, goal.userID);
            values.put(USER_HAS_GOALS_COLUMN_GOAL_ID, goal.goalID);
            database.insertOrThrow(USER_HAS_GOALS_TABLE, null, values);
            database.close();
        } catch (SQLiteConstraintException e) {
            database.update(GOAL_TABLE, values, GOAL_COLUMN_ID + "=" + goal.goalID, null);
            database.close();
        }
    }

    public Goal getGoal(int goalID) throws SQLiteConstraintException {
        SQLiteDatabase database = this.getReadableDatabase();
        // Get the goal from the database
        Cursor cursor = database.query(GOAL_TABLE, null, GOAL_COLUMN_ID + "=" + goalID,
                null, null, null, null, null);
        cursor.moveToLast();
        Goal goal = new Goal();
        goal.goalID = cursor.getInt(0);
        if (cursor.isNull(1)) {
            goal.futureGoalID = null;
        } else {
            goal.futureGoalID = cursor.getInt(1);
        }
        goal.title = cursor.getString(2);
        goal.description = cursor.getString(3);
        if (cursor.isNull(4)) {
            goal.comment = null;
        } else {
            goal.comment = cursor.getString(4);
        }
        goal.createdAt = cursor.getString(5);
        goal.expectedCompletion = cursor.getString(6);
        if (cursor.isNull(7)) {
            goal.finishedAt = null;
        } else {
            goal.finishedAt = cursor.getString(7);
        }
        goal.lastModified = cursor.getString(8);
        goal.archived = cursor.getInt(9) == 1;
        cursor.close();
        return goal;
    }
}
