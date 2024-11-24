package com.example.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LoanCalculator.db";
    private static final int DATABASE_VERSION = 2;  // Incremented version due to schema change

    // Table and columns
    private static final String TABLE_NAME = "loans";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOAN_NAME = "loan_name";  // New column
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_INTEREST = "interest";
    private static final String COLUMN_TERM = "term";
    private static final String COLUMN_MONTHLY_PAYMENT = "monthly_payment";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;  // Save the context for later use
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for loans with the new loan_name column
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOAN_NAME + " TEXT, "  // New column for loan name
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_INTEREST + " REAL, "
                + COLUMN_TERM + " INTEGER, "
                + COLUMN_MONTHLY_PAYMENT + " REAL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle schema changes when database version is upgraded
        if (oldVersion < 2) {
            // Add the loan_name column if upgrading from version 1
            String ALTER_TABLE = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LOAN_NAME + " TEXT";
            db.execSQL(ALTER_TABLE);
        }
    }

    // Method to add a loan entry to the database
    public long addLoan(String loanName, double amount, double interest, int term, double monthlyPayment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOAN_NAME, loanName);  // Insert loan name
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_INTEREST, interest);
        values.put(COLUMN_TERM, term);
        values.put(COLUMN_MONTHLY_PAYMENT, monthlyPayment);
        return db.insert(TABLE_NAME, null, values);
    }

    // Method to retrieve all loans from the database
    public Cursor getLoans() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_LOAN_NAME, COLUMN_AMOUNT, COLUMN_INTEREST, COLUMN_TERM, COLUMN_MONTHLY_PAYMENT},
                null,
                null,
                null,
                null,
                null);
    }

    // Method to delete a loan entry by its ID
    public void deleteLoan(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Method to delete the entire database
    public void deleteDatabase() {
        // Use the context passed into the constructor to delete the database
        context.deleteDatabase(DATABASE_NAME);
    }

    // Method to clear all data in the loans table (optional)
    public void clearLoans() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);  // Deletes all rows from the loans table
    }
}
