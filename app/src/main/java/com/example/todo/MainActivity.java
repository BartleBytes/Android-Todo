package com.example.todo;

import android.os.Bundle;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.database.Cursor;

public class MainActivity extends AppCompatActivity {

    Button add;
    AlertDialog dialog;
    LinearLayout layout;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        layout = findViewById(R.id.container);
        dbHelper = new DatabaseHelper(this);

        buildDialog();
        loadTasksFromDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);
        builder.setView(view);
        builder.setTitle("Enter your task!")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = name.getText().toString();
                        addCard(taskName);
                        dbHelper.addTask(taskName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
    }

    private void loadTasksFromDatabase() {
        Cursor cursor = dbHelper.getTasks();
        int nameIndex = cursor.getColumnIndex("name");

        if (nameIndex == -1) {
            cursor.close();
            throw new IllegalStateException("Database Column 'name' not found.");
        }

        while (cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            addCard(taskName);
        }
        cursor.close();
    }
    private void addCard(String name) {
        final View card = getLayoutInflater().inflate(R.layout.card, null);
        TextView nameView = card.findViewById(R.id.name);
        nameView.setText(name);

        Button button = card.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(card);

                Cursor cursor = dbHelper.getTasks();
                int idIndex = cursor.getColumnIndex("id");

                if(idIndex != -1 && cursor.moveToFirst()) {
                    long taskId = cursor.getLong(idIndex);
                    dbHelper.deleteTask(taskId);
                }
                cursor.close();
            }
        });

        layout.addView(card);
    }
}
