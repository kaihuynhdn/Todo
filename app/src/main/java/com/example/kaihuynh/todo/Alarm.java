package com.example.kaihuynh.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.kaihuynh.todo.model.Todo;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra("id", -1);
        Todo todo = (Todo) intent.getSerializableExtra("todo");
        String s = intent.getStringExtra("action");
        if(s.equals("notify")){
            NotificationAction.showNotification(context, MainActivity.class, todo.getTitle(), todo.getContent(), (int) id);
        }
    }


}
