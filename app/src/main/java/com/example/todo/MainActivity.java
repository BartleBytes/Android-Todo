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

    Button addLoanButton;
    AlertDialog loanDialog;
    LinearLayout loanLayout;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLoanButton = findViewById(R.id.add);
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
    }

    public void buildLoanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText amountEdit = view.findViewById(R.id.amountEdit);
        final EditText interestEdit = view.findViewById(R.id.interestEdit);
        final EditText termEdit = view.findViewById(R.id.termEdit);
        builder.setView(view);

        builder.setTitle("Enter Loan Details")
                .setPositiveButton("Save Loan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double amount = Double.parseDouble(amountEdit.getText().toString());
                        double interest = Double.parseDouble(interestEdit.getText().toString());
                        int term = Integer.parseInt(termEdit.getText().toString());

                        double monthlyPayment = calculateMonthlyPayment(amount, interest, term);

                        dbHelper.addLoan(amount, interest, term, monthlyPayment);
                        addLoanCard(amount, interest, term, monthlyPayment);
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
        int amountIndex = cursor.getColumnIndex("amount");
        int interestIndex = cursor.getColumnIndex("interest");
        int termIndex = cursor.getColumnIndex("term");
        int monthlyPaymentIndex = cursor.getColumnIndex("monthly_payment");

        if (amountIndex == -1 || interestIndex == -1 || termIndex == -1 || monthlyPaymentIndex == -1) {
            cursor.close();
            throw new IllegalStateException("Database columns not found.");
        }

        while (cursor.moveToNext()) {
            double amount = cursor.getDouble(amountIndex);
            double interest = cursor.getDouble(interestIndex);
            int term = cursor.getInt(termIndex);
            double monthlyPayment = cursor.getDouble(monthlyPaymentIndex);
            addLoanCard(amount, interest, term, monthlyPayment);
        }
        cursor.close();
    }

    private void addLoanCard(double amount, double interest, int term, double monthlyPayment) {
        final View card = getLayoutInflater().inflate(R.layout.card, null);
        TextView detailsView = card.findViewById(R.id.name);

        String details = "Amount: $" + amount + "\n" +
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
                openLoanDetailView(amount, interest, term, monthlyPayment);
            }
        });

        loanLayout.addView(card);
    }
    // Method to open the LoanDetailActivity and pass the loan details
    private void openLoanDetailView(double amount, double interest, int term, double monthlyPayment) {
        Intent intent = new Intent(this, LoanDetailActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("interest", interest);
        intent.putExtra("term", term);
        intent.putExtra("monthlyPayment", monthlyPayment);
        startActivity(intent);
    }
}
