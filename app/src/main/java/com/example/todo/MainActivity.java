package com.example.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button addLoanButton, logoutButton;

    AlertDialog loanDialog;
    LinearLayout loanLayout;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLoanButton = findViewById(R.id.add);
        logoutButton = findViewById(R.id.logoutButton);
        loanLayout = findViewById(R.id.container);
        dbHelper = new DatabaseHelper(this);

        buildLoanDialog();
        loadLoansFromDatabase();

        addLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loanDialog.show();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    private void logout() {
        // Clear session or user preferences (if applicable)
        getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply();

        // Navigate back to the login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);

        // Optional: Show a toast or confirmation message
        // Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    public void buildLoanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText loanNameEdit = view.findViewById(R.id.loanNameEdit);
        final EditText amountEdit = view.findViewById(R.id.amountEdit);
        final EditText interestEdit = view.findViewById(R.id.interestEdit);
        final EditText termEdit = view.findViewById(R.id.termEdit);
        builder.setView(view);

        builder.setTitle("Enter Loan Details")
                .setPositiveButton("Save Loan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String loanName = loanNameEdit.getText().toString();
                        double amount = Double.parseDouble(amountEdit.getText().toString());
                        double interest = Double.parseDouble(interestEdit.getText().toString());
                        int term = Integer.parseInt(termEdit.getText().toString());

                        double monthlyPayment = calculateMonthlyPayment( amount, interest, term);

                        dbHelper.addLoan(loanName, amount, interest, term, monthlyPayment);
                        addLoanCard(loanName, amount, interest, term, monthlyPayment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        loanDialog = builder.create();
    }


    private double calculateMonthlyPayment(double amount, double annualInterestRate, int termInYears) {
        double monthlyInterestRate = annualInterestRate / 100 / 12;
        int termInMonths = termInYears * 12;

        return (amount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -termInMonths));
    }

    private void loadLoansFromDatabase() {
        Cursor cursor = dbHelper.getLoans();
        int loanNameIndex = cursor.getColumnIndex("loan_name");
        int amountIndex = cursor.getColumnIndex("amount");
        int interestIndex = cursor.getColumnIndex("interest");
        int termIndex = cursor.getColumnIndex("term");
        int monthlyPaymentIndex = cursor.getColumnIndex("monthly_payment");

        if (loanNameIndex == -1 || amountIndex == -1 || interestIndex == -1 || termIndex == -1 || monthlyPaymentIndex == -1) {
            cursor.close();
            throw new IllegalStateException("Database columns not found.");
        }

        while (cursor.moveToNext()) {
            String loanName = cursor.getString(loanNameIndex);
            double amount = cursor.getDouble(amountIndex);
            double interest = cursor.getDouble(interestIndex);
            int term = cursor.getInt(termIndex);
            double monthlyPayment = cursor.getDouble(monthlyPaymentIndex);
            addLoanCard(loanName, amount, interest, term, monthlyPayment);
        }
        cursor.close();
    }

    private void addLoanCard(String loanName, double amount, double interest, int term, double monthlyPayment) {

        final View card = getLayoutInflater().inflate(R.layout.card, null);
        TextView nameView = card.findViewById(R.id.name);
        nameView.setText(loanName);
        TextView detailsView = card.findViewById(R.id.details);

        String details =
                "Amount: $" + amount + "\n" +
                "Interest: " + interest + "%\n" +
                "Term: " + term + " years\n" +
                "Monthly Payment: $" + String.format("%.2f", monthlyPayment);
        detailsView.setText(details);

        Button deleteButton = card.findViewById(R.id.button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loanLayout.removeView(card);

                // Implement logic to delete the loan entry from the database
                // Here, you'll need to store and retrieve each loan's ID in the UI.
                // For simplicity, this example does not include this functionality.
            }
        });
        // Add click listener to the card to open a detailed view
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoanDetailView(loanName, amount, interest, term, monthlyPayment);
            }
        });

        loanLayout.addView(card);
    }
    // Method to open the LoanDetailActivity and pass the loan details
    private void openLoanDetailView(String loanName, double amount, double interest, int term, double monthlyPayment) {
        Intent intent = new Intent(this, LoanDetailActivity.class);
        intent.putExtra("loanName", loanName);
        intent.putExtra("amount", amount);
        intent.putExtra("interest", interest);
        intent.putExtra("term", term);
        intent.putExtra("monthlyPayment", monthlyPayment);
        startActivity(intent);
    }
}

