package com.example.todo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

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
        ArrayList<Entry> remainingBalanceEntries = new ArrayList<>();
        ArrayList<Entry> principalPaidEntries = new ArrayList<>();
        ArrayList<Entry> interestPaidEntries = new ArrayList<>();

        double currentBalance = amount;
        double totalPrincipalPaid = 0;
        double totalInterestPaid = 0;

        // Loop through each month
        for (int month = 1; month <= term * 12; month++) {
            // Calculate the principal and interest for this month
            double principalPaid = calculatePrincipalPaid(currentBalance, interest, term, month, monthlyPayment);
            double interestPaid = calculateInterestPaid(currentBalance, interest, month);

            // Update the total principal and interest paid
            totalPrincipalPaid += principalPaid;
            totalInterestPaid += interestPaid;

            // Calculate the remaining balance
            currentBalance -= principalPaid;

            // Add entries for each line to the respective dataset
            remainingBalanceEntries.add(new Entry(month, (float) currentBalance));
            principalPaidEntries.add(new Entry(month, (float) totalPrincipalPaid));
            interestPaidEntries.add(new Entry(month, (float) totalInterestPaid));
        }

        // Create the datasets for remaining balance, principal, and interest
        LineDataSet remainingBalanceDataSet = new LineDataSet(remainingBalanceEntries, "Remaining Balance");
        LineDataSet principalPaidDataSet = new LineDataSet(principalPaidEntries, "Principal Paid");
        LineDataSet interestPaidDataSet = new LineDataSet(interestPaidEntries, "Interest Paid");

        // Customize each dataset's appearance
        remainingBalanceDataSet.setColor(Color.GREEN);
        remainingBalanceDataSet.setLineWidth(2.5f);
        remainingBalanceDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        principalPaidDataSet.setColor(Color.BLUE);
        principalPaidDataSet.setLineWidth(2.5f);
        principalPaidDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        interestPaidDataSet.setColor(Color.RED);
        interestPaidDataSet.setLineWidth(2.5f);
        interestPaidDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Create the chart data
        LineData lineData = new LineData(remainingBalanceDataSet, principalPaidDataSet, interestPaidDataSet);
        lineChart.setData(lineData);

        // Customize the chart appearance
        Description description = new Description();
        description.setText("Loan Amortization Over Time");
        description.setTextColor(Color.BLACK);
        description.setTextSize(14f);
        lineChart.setDescription(description);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.LTGRAY);
        lineChart.setBorderWidth(1f);

        // Customize X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(8f);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(term * 12); // Set axis maximum to number of months
        xAxis.setLabelCount(term * 12 / 12, true); // Set label count equal to number of months

        // Format X-axis to show month numbers
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Month " + (int) value;
            }
        });

        // Customize Y-axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setTextSize(12f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);

        // Disable Y-axis (Right)
        lineChart.getAxisRight().setEnabled(false);

        // Customize Legend
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.LINE);

        // Add animations
        lineChart.animateX(1500);
        lineChart.animateY(1500);

        // Refresh the chart
        lineChart.invalidate();
    }

    // Method to calculate the principal paid for each month (simplified amortization calculation)
    private double calculatePrincipalPaid(double balance, double interestRate, int term, int month, double monthlyPayment) {
        double monthlyInterestRate = interestRate / 100 / 12;
        double interestPaid = balance * monthlyInterestRate;
        double principalPaid = monthlyPayment - interestPaid;
        return principalPaid;
    }

    // Method to calculate the interest paid for each month
    private double calculateInterestPaid(double balance, double interestRate, int month) {
        double monthlyInterestRate = interestRate / 100 / 12;
        return balance * monthlyInterestRate;
    }
}
