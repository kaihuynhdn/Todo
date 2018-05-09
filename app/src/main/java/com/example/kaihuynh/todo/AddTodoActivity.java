package com.example.kaihuynh.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kaihuynh.todo.data.DBManager;
import com.example.kaihuynh.todo.model.Todo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

public class AddTodoActivity extends AppCompatActivity {

    private EditText mTitle, mContent, mDate;
    private ImageView imageView;
    private Button mAddButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private byte[] img;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        initialize();
        addEvents();
    }


    private void initialize() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        img = new byte[]{};
        dbManager = new DBManager(this);
    }

    private void addComponents() {
        mTitle = findViewById(R.id.et_title_add);
        mContent = findViewById(R.id.et_content_add);
        mDate = findViewById(R.id.et_date_add);
        mAddButton = findViewById(R.id.btn_add);
        imageView = findViewById(R.id.img_upload_add);
    }

    private void addEvents() {
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        mDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showDatePickerDialog();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 123);
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    Todo todo = new Todo();
                    todo.setTitle(mTitle.getText().toString());
                    todo.setContent(mContent.getText().toString());
                    todo.setDate(mDate.getText().toString());
                    todo.setImage(img);

                    long id = dbManager.addTodo(todo);

                    Calendar calendar1 = Calendar.getInstance();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth-1, mDay, mHour, mMinute,0);


                    finish();
                }
            }
        });
    }

    private boolean isValid(){
        if(mTitle.getText().toString().equals("")){
            Toast.makeText(AddTodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mTitle.requestFocus();
            return false;
        }else if(mContent.getText().toString().equals("")){
            Toast.makeText(AddTodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mContent.requestFocus();
            return false;
        }else if(mDate.getText().toString().equals("")){
            Toast.makeText(AddTodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mDate.requestFocus();
            return false;
        }
        return true;
    }

    private void setAlarm(long timeinMillis, long id, Todo todo) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AddTodoActivity.this, Alarm.class);
        intent.putExtra("id", id);
        intent.putExtra("todo", todo);
        intent.putExtra("action", "notify");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddTodoActivity.this, (int) id, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeinMillis, pendingIntent);
    }

    private void showTimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mHour = selectedHour;
                mMinute = selectedMinute;

            }
        }, mHour, mMinute, true);//Yes 24 hour time
        mTimePicker.setTitle("Chọn giờ");
        mTimePicker.show();

    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = year; mMonth = month+1; mDay = day;
                String a = String.valueOf(mMinute);
                String b = String.valueOf(mMonth);
                String c = String.valueOf(mDay);
                String d = String.valueOf(mHour);
                if(mDay<10){
                    c = "0"+c;
                }
                if(mHour<10){
                    d = "0"+d;
                }
                if(mMinute<10){
                    a = "0"+a;
                }
                if(mMonth<10){
                    b = "0"+b;
                }
                mDate.setText(d + ":" + a + " " + c + "-" + b + "-" + mYear );
            }
        };

        int dayOfMonth, month, year;

        String[] dateSplit = mDate.getText().toString().split(" ");
        if (dateSplit[0].equals("")){
            Calendar calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }else{
            String[] date = dateSplit[1].split("-");
            dayOfMonth = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]) - 1;
            year = Integer.parseInt(date[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, callback, year, month, dayOfMonth);
        datePickerDialog.show();
        showTimePickerDialog();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArr);
                img = byteArr.toByteArray();
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddTodoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
    }
}
