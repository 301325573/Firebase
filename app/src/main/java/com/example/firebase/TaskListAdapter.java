package com.example.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private Activity context;
    private List<Task> taskList;

    public TaskListAdapter(Activity context, List<Task> taskList) {
        super(context, R.layout.list_layout, taskList);
        this.context = context;
        this.taskList = taskList;
    }

    public TaskListAdapter(Context context, int resource, List<Task> objects, Activity context1, List<Task> taskList) {
        super(context, resource, objects);
        this.context = context1;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView taskName = listViewItem.findViewById(R.id.textViewTask);
        TextView taskAssn = listViewItem.findViewById(R.id.textViewName);
        TextView dueDate = listViewItem.findViewById(R.id.textViewDueDate);
        TextView tvSchool = listViewItem.findViewById(R.id.textViewSchool);

        Task task = taskList.get(position);
        taskName.setText(task.getTaskName());
        taskAssn.setText("Assigned to: " + task.getTaskAssn());
        dueDate.setText(task.getDueDate().toString());

        if (task.isDone())
            tvSchool.setText("Done");
        else
            tvSchool.setText("In progressing");

        return listViewItem;
    }

}
