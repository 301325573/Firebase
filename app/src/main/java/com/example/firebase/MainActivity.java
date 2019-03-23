package com.example.firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextTask;
    EditText editTextName;
    EditText editTextDueDate;
    Spinner spinnerSchool;
    Button buttonAddTask;

    DatabaseReference databaseTask;

    ListView lvTasks;
    List<Task> taskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseTask = FirebaseDatabase.getInstance().getReference("tasks");

        editTextTask = findViewById(R.id.editTextTask);
        editTextName = findViewById(R.id.editTextName);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        spinnerSchool = findViewById(R.id.spinnerTaskIsDone);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        lvTasks = findViewById(R.id.lvTasks);
        taskList = new ArrayList<Task>();

        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskList.get(position);

                showUpdateDialog(task.getTaskId(),
                        task.getTaskName(),
                        task.getTaskAssn(),
                        task.isDone(),
                        task.getDueDate());
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    taskList.add(task);
                }

                TaskListAdapter adapter = new TaskListAdapter(MainActivity.this, taskList);
                lvTasks.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addTask() {
        String taskName = editTextTask.getText().toString().trim();
        String taskAssn = editTextName.getText().toString().trim();
        boolean taskIsDone = false;
        String dueDateText = editTextDueDate.getText().toString().trim();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDate;
        try {
            dueDate = ft.parse(dueDateText);
        } catch (ParseException e) {
            Toast.makeText(this, "You must enter a vaild date.", Toast.LENGTH_LONG).show();
            return;
        }

        if (spinnerSchool.getSelectedItem().toString().trim().equals("Done"))
            taskIsDone = true;

        if (TextUtils.isEmpty(taskName)) {
            Toast.makeText(this, "You must enter a task.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(taskAssn)) {
            Toast.makeText(this, "You must enter a name.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = databaseTask.push().getKey();
        Task task = new Task(id, taskName, taskAssn, taskIsDone, dueDate);

        com.google.android.gms.tasks.Task setValueTask = databaseTask.child(id).setValue(task);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task added.", Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void updateTask(String id, String firstName, String lastName, boolean done, Date dueDate) {
        DatabaseReference dbRef = databaseTask.child(id);

        Task task = new Task(id, firstName, lastName, done, dueDate);

        com.google.android.gms.tasks.Task setValueTask = dbRef.setValue(task);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task Updated.", Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(final String taskID, String taskName, String taskAssn, boolean isDone, Date dueDate) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextTask = dialogView.findViewById(R.id.editTextTask);
        editTextTask.setText(taskName);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        editTextName.setText(taskAssn);

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        final EditText editTextDueDate = dialogView.findViewById(R.id.editTextDueDate);
        editTextDueDate.setText(ft.format(dueDate));

        final Spinner spinnerTaskIsDone = dialogView.findViewById(R.id.spinnerTaskIsDone);
        if (isDone)
            spinnerTaskIsDone.setSelection(((ArrayAdapter<String>) spinnerTaskIsDone.getAdapter()).getPosition("Done"));
        else
            spinnerTaskIsDone.setSelection(((ArrayAdapter<String>) spinnerTaskIsDone.getAdapter()).getPosition("In progressing"));


        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("Update Task " + taskName + " " + taskAssn);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = editTextTask.getText().toString().trim();
                String taskAssn = editTextName.getText().toString().trim();
                String dueDateText = editTextDueDate.getText().toString().trim();
                boolean taskIsDone = false;
                if (spinnerTaskIsDone.getSelectedItem().toString().trim().equals("Done"))
                    taskIsDone = true;

                Date dueDate;
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    dueDate = ft.parse(dueDateText);
                } catch (ParseException e) {
                    editTextDueDate.setError("You must enter a vaild date.");
                    return;
                }

                if (TextUtils.isEmpty(task)) {
                    editTextTask.setError("Task is required");
                    return;
                } else if (TextUtils.isEmpty(taskAssn)) {
                    editTextName.setError("Name is required");
                    return;
                }

                updateTask(taskID, task, taskAssn, taskIsDone, dueDate);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(taskID);

                alertDialog.dismiss();
            }
        });

    }

    private void deleteTask(String id) {
        DatabaseReference dbRef = databaseTask.child(id);

        com.google.android.gms.tasks.Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task Deleted.", Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
