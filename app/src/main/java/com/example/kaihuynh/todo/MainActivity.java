package com.example.kaihuynh.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kaihuynh.todo.adapter.TodoAdapter;
import com.example.kaihuynh.todo.data.DBManager;
import com.example.kaihuynh.todo.model.Todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ListView mTodayListView, mTomorrowListView, mUpcomingListView, mOldTodoListView;
    private ArrayList<Todo> mTodayList, mTomorrowList, mUpcomingList, mOldTodoList;
    private ArrayAdapter<Todo> mTodayAdapter, mTomorrowAdapter, mUpcomingAdapter, mOldTodoAdapter;
    private DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addComponent();
        initialize();
        addEvents();
    }

    private void addComponent() {
        mTodayListView = findViewById(R.id.lv_today);
        mTomorrowListView = findViewById(R.id.lv_tomorrow);
        mUpcomingListView = findViewById(R.id.lv_upcoming);
        mOldTodoListView = findViewById(R.id.lv_old);


    }

    private void initialize() {
        manager = new DBManager(this);
        mTodayList = new ArrayList<>();
        mTomorrowList = new ArrayList<>();
        mUpcomingList = new ArrayList<>();
        mOldTodoList = new ArrayList<>();
    }

    private void addEvents() {
        mTodayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                intent.putExtra("todo", mTodayList.get(i));
                startActivity(intent);
            }
        });
    }

    private void getList(ArrayList<Todo> list){
        mTomorrowList = new ArrayList<>();
        mTodayList= new ArrayList<>();
        mUpcomingList= new ArrayList<>();
        mOldTodoList = new ArrayList<>();
        for(int i = 0; i<list.size(); i++){
            Calendar a = toDate(list.get(i).getDate());
            Calendar b = Calendar.getInstance();
            if(a.getTimeInMillis() < b.getTimeInMillis()){
                mOldTodoList.add(list.get(i));
                continue;
            }
            if(a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.YEAR) == b.get(Calendar.YEAR)){
                mTodayList.add(list.get(i));
                continue;
            }else if((a.get(Calendar.DAY_OF_MONTH) - b.get(Calendar.DAY_OF_MONTH)==1) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.YEAR) == b.get(Calendar.YEAR)){
                mTomorrowList.add(list.get(i));
            }else{
                if(a.get(Calendar.YEAR) == b.get(Calendar.YEAR)){
                    if(a.get(Calendar.MONTH) == b.get(Calendar.MONTH)){
                        if (a.get(Calendar.DAY_OF_MONTH)-b.get(Calendar.DAY_OF_MONTH)>1){
                            mUpcomingList.add(list.get(i));
                        }else {
                            mOldTodoList.add(list.get(i));
                        }
                    }else if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)){
                        mUpcomingList.add(list.get(i));
                    }else {
                        mOldTodoList.add(list.get(i));
                    }
                }else if(a.get(Calendar.YEAR) > b.get(Calendar.YEAR)){
                    mUpcomingList.add(list.get(i));
                }else {
                    mOldTodoList.add(list.get(i));
                }
            }
        }

    }

    private ArrayList<Todo> getTomorrowList(ArrayList<Todo> list){
        ArrayList<Todo> today = new ArrayList<>();
        for(int i = 0; i<list.size()-1; i++){
            Calendar a = toDate(list.get(i).getDate());
            Calendar b = Calendar.getInstance();
            if(a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)){
                today.add(list.get(i));
            }
        }

        return today;
    }

    private Calendar toDate(String s){
        Calendar calendar = Calendar.getInstance();
        String[] dateSplit = s.split(" ");
        String[] time = dateSplit[0].split(":");
        int mHour = Integer.parseInt(time[0]);
        int mMinute  = Integer.parseInt(time[1]);

        String[] date = dateSplit[1].split("-");
        int mDay = Integer.parseInt(date[0]);
        int mMonth = Integer.parseInt(date[1]);
        int mYear = Integer.parseInt(date[2]);

        calendar.set(mYear, mMonth-1, mDay, mHour, mMinute);

        return calendar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_action:
                startActivity(new Intent(MainActivity.this, AddTodoActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update(){
        manager = new DBManager(this);
        getList(manager.getListData());
        sortTime(mOldTodoList);
        sortTime(mTodayList);
        sortTime(mTomorrowList);
        sortTime(mUpcomingList);
        mTodayAdapter = new TodoAdapter(this, R.layout.todo_item, mTodayList);
        mTodayListView.setAdapter(mTodayAdapter);

        mTomorrowAdapter = new TodoAdapter(this, R.layout.todo_item, mTomorrowList);
        mTomorrowListView.setAdapter(mTomorrowAdapter);

        mUpcomingAdapter = new TodoAdapter(this, R.layout.todo_item, mUpcomingList);
        mUpcomingListView.setAdapter(mUpcomingAdapter);

        mOldTodoAdapter = new TodoAdapter(this, R.layout.todo_item, mOldTodoList);
        mOldTodoListView.setAdapter(mOldTodoAdapter);
    }

    public void sortTime(ArrayList<Todo> list) {
        Collections.sort(list, new Comparator<Todo>() {
            @Override
            public int compare(Todo todo, Todo t1) {
                return toDate(todo.getDate()).compareTo(toDate(t1.getDate()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
