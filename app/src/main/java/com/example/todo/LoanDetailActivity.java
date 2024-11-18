package com.example.todo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoanDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_detail);

        // Get the loan details from the Intent
        double amount = getIntent().getDoubleExtra("amount", 0);
        double interest = getIntent().getDoubleExtra("interest", 0);
        int term = getIntent().getIntExtra("term", 0);
        double monthlyPayment = getIntent().getDoubleExtra("monthlyPayment", 0);

        // Find the TextViews to display the loan details
        TextView amountView = findViewById(R.id.amountView);
        TextView interestView = findViewById(R.id.interestView);
        TextView termView = findViewById(R.id.termView);
        TextView monthlyPaymentView = findViewById(R.id.monthlyPaymentView);

        // Set the loan details into the TextViews
        amountView.setText("Amount: $" + amount);
        interestView.setText("Interest: " + interest + "%");
        termView.setText("Term: " + term + " years");
        monthlyPaymentView.setText("Monthly Payment: $" + String.format("%.2f", monthlyPayment));
    }
}
