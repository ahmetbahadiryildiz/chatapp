package com.greemlock.edutherapist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageListAdapter extends ArrayAdapter<ObjectMessage> {
    private Context contextActivity;
    private int otherMessageResource;

    private static class ViewHolder {
        TextView name;
        TextView message;
        TextView date;
        ImageView imageView;
    }
    public MessageListAdapter(Context context, int resourceOtherMessages, ArrayList<ObjectMessage> objects) {
        super(context, resourceOtherMessages, objects);
        contextActivity = context;
        otherMessageResource = resourceOtherMessages;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        String name = getItem(position).getMessage_name();
        String message = getItem(position).getMessage();
        String date = getItem(position).getMessage_date();

        MessageListAdapter.ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(contextActivity);
            convertView = inflater.inflate(otherMessageResource, parent, false);


            holder = new MessageListAdapter.ViewHolder();
        }
        else {
            holder = (MessageListAdapter.ViewHolder) convertView.getTag();
        }
        holder.name = convertView.findViewById(R.id.name);
        holder.message = convertView.findViewById(R.id.message);
        holder.date = convertView.findViewById(R.id.date);
        holder.imageView = convertView.findViewById(R.id.imageView);

        convertView.setTag(holder);

        holder.name.setText(name);
        holder.message.setText(message);
        holder.date.setText(date);
        holder.imageView.setImageResource(R.drawable.ic_baseline_person_24);

        return convertView;
    }
}
