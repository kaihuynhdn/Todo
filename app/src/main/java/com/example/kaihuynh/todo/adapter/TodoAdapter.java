package com.example.kaihuynh.todo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaihuynh.todo.R;
import com.example.kaihuynh.todo.model.Todo;

import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<Todo> {
    private Context context;
    private int layout;
    private ArrayList<Todo> arrayList;

    public TodoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Todo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layout = resource;
        this.arrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTitle = convertView.findViewById(R.id.tv_title_todo_item);
            viewHolder.mDate =convertView.findViewById(R.id.tv_date_todo_item);
            viewHolder.imageView = convertView.findViewById(R.id.img_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTitle.setText(arrayList.get(position).getTitle());
        viewHolder.mDate.setText(arrayList.get(position).getDate());
        byte[] img = arrayList.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        viewHolder.imageView.setImageBitmap(bitmap);

        return convertView;
    }

    class ViewHolder{
        private TextView mTitle, mDate;
        private ImageView imageView;
    }
}
