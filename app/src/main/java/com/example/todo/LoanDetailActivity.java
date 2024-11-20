package com.example.todo;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.Description;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LoanDetailActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_detail);

        // Initialize the chart
        lineChart = findViewById(R.id.lineChart);

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

        // Prepare the data for the chart (Loan schedule)
        ArrayList<Entry> entries = new ArrayList<>();

        // Populate the graph with loan data
        for (int i = 1; i <= term * 12; i++) { // Assuming term is in years and we're converting to months
            double principalPaid = calculatePrincipalPaid(amount, interest, term, i); // Method to calculate principal paid
            entries.add(new Entry(i, (float) principalPaid)); // Add data point to graph
        }

        // Create a dataset for the line chart
        LineDataSet dataSet = new LineDataSet(entries, "Loan Repayment");
        dataSet.setColor(0xFF6200EE); // Example color for the line (Material Design Purple)
        dataSet.setValueTextSize(10f); // Adjust value text size
        dataSet.setCircleRadius(3f); // Adjust data point size
        dataSet.setDrawCircles(true); // Show circles on data points
        dataSet.setLineWidth(2f); // Line thickness

        // Create the chart data and set it to the LineChart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Optional: Customize the chart appearance
        Description description = new Description();
        description.setText("Loan Repayment Over Time");
        lineChart.setDescription(description);

        lineChart.getXAxis().setDrawLabels(true); // Ensure X-axis labels are visible
        lineChart.getAxisLeft().setDrawGridLines(false); // Optionally remove grid lines
        lineChart.getAxisRight().setEnabled(false); // Disable right axis
        lineChart.invalidate(); // Refresh the chart
    }

    // Method to calculate the principal paid for each month (simplified amortization calculation)
    private double calculatePrincipalPaid(double amount, double interest, int term, int month) {
        double monthlyInterestRate = interest / 100 / 12;
        double numberOfPayments = term * 12;
        double monthlyPayment = (amount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));
        double principalPaid = monthlyPayment - (amount * monthlyInterestRate * (month - 1));
        return principalPaid > 0 ? principalPaid : 0; // Ensure no negative principal values
    }
}
