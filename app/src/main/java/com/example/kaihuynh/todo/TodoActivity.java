package com.example.kaihuynh.todo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
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

public class TodoActivity extends AppCompatActivity {

    private EditText mTitle, mContent, mDate;
    private ImageView imageView;
    private Button mUpdateButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private byte[] img;
    private DBManager dbManager;
    private Todo currentTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addComponents();
        initialize();
        addEvents();
    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        Intent intent = getIntent();
        currentTodo = (Todo) intent.getSerializableExtra("todo");
        dbManager = new DBManager(this);

        String[] dateSplit = currentTodo.getDate().split(" ");
        String[] time = dateSplit[0].split(":");
        mHour = Integer.parseInt(time[0]);
        mMinute  = Integer.parseInt(time[1]);

        String[] date = dateSplit[1].split("-");
        mDay = Integer.parseInt(date[0]);
        mMonth = Integer.parseInt(date[1]);
        mYear = Integer.parseInt(date[2]);
        mTitle.setText(currentTodo.getTitle());
        mContent.setText(currentTodo.getContent());
        mDate.setText(mHour + ":" +mMinute + " " + mDay + "-" + (mMonth) + "-" + mYear );
        img = currentTodo.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(currentTodo.getImage(), 0, currentTodo.getImage().length);
        if(bitmap == null){
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.upload));
        }else {
            imageView.setImageBitmap(bitmap);

        }

    }

    private void addComponents() {
        mTitle = findViewById(R.id.et_title_view);
        mContent = findViewById(R.id.et_content_view);
        mDate = findViewById(R.id.et_date_view);
        imageView = findViewById(R.id.img_upload_view);
        mUpdateButton = findViewById(R.id.btn_update);
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

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mUpdateButton.getVisibility() == View.INVISIBLE){
                    mUpdateButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mUpdateButton.getVisibility() == View.INVISIBLE){
                    mUpdateButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mUpdateButton.getVisibility() == View.INVISIBLE){
                    mUpdateButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    Todo td = new Todo(currentTodo.getId(), mTitle.getText().toString(), mContent.getText().toString(), mDate.getText().toString(), img);
                    dbManager.updateTodo(td);
                    mUpdateButton.setVisibility(View.INVISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth-1, mDay, mHour, mMinute,0);
                    updateAlarm(calendar.getTimeInMillis(), td);
                    Toast.makeText(TodoActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }

    private void showTimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(TodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, callback, mYear, mMonth-1, mDay);
        datePickerDialog.show();
        showTimePickerDialog();

    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn muốn xóa 'To Do' này không?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelAlarm(currentTodo.getId());
                dbManager.deleteTodo(currentTodo.getId());
                finish();
            }
        });
        builder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn Cập nhật lại 'Todo' này không?");
        builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(isValid()){
                    Todo td = new Todo(currentTodo.getId(), mTitle.getText().toString(), mContent.getText().toString(), mDate.getText().toString(), img);
                    dbManager.updateTodo(td);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth-1, mDay, mHour, mMinute,0);
                    Toast.makeText(TodoActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    updateAlarm(calendar.getTimeInMillis(), td);
                    finish();
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isValid(){
        if(mTitle.getText().toString().equals("")){
            Toast.makeText(TodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mTitle.requestFocus();
            return false;
        }else if(mContent.getText().toString().equals("")){
            Toast.makeText(TodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mContent.requestFocus();
            return false;
        }else if(mDate.getText().toString().equals("")){
            Toast.makeText(TodoActivity.this, "Cần nhập đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            mDate.requestFocus();
            return false;
        }
        return true;
    }

    private void setAlarm(long timeinMillis, long id, Todo todo) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(TodoActivity.this, Alarm.class);
        intent.putExtra("id", id);
        intent.putExtra("todo", todo);
        intent.putExtra("action", "notify");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeinMillis, pendingIntent);
    }

    private void updateAlarm(long timeinMillis, Todo t){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(TodoActivity.this, Alarm.class);
        myIntent.putExtra("id", t.getId());
        myIntent.putExtra("todo", t);
        myIntent.putExtra("action", "cancle");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, t.getId(), myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Intent intent = new Intent(TodoActivity.this, Alarm.class);
        intent.putExtra("id", t.getId());
        intent.putExtra("todo", t);
        intent.putExtra("action", "notify");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(TodoActivity.this, t.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeinMillis, pendingIntent1);
    }

    private void cancelAlarm(int REQUEST_CODE)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(TodoActivity.this, Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, REQUEST_CODE, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_action:
                showDeleteDialog();
                break;
            case android.R.id.home:
                if(mUpdateButton.getVisibility() == View.VISIBLE){
                    showUpdateDialog();
                }else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArr);
                img = byteArr.toByteArray();
                imageView.setImageBitmap(selectedImage);
                mUpdateButton.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(TodoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
    }
}
