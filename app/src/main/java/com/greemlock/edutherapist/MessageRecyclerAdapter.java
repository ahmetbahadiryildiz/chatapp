package com.greemlock.edutherapist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MessageRecyclerAdapter extends ArrayAdapter<ObjectMessage>{

    private Context contextActivity;
    private int otherMessageResource;
    private int myMessageResource;

    private static class ViewHolder {
        TextView name;
        TextView message;
        TextView date;
        ImageView imageView;
    }

    public MessageRecyclerAdapter(Context context, int resourceOtherMessages, int resourceMyMessages, ArrayList<ObjectMessage> objects) {
        super(context, resourceOtherMessages, resourceMyMessages, objects);
        contextActivity = context;
        otherMessageResource = resourceOtherMessages;
        myMessageResource = resourceMyMessages;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        String name = getItem(position).getMessage_name();
        String message = getItem(position).getMessage();
        String date = getItem(position).getMessage_date();

        ViewHolder holder;

        if(convertView == null){
            String username = SaveSharedPreferences.getPrefName(getContext());
            LayoutInflater inflater = LayoutInflater.from(contextActivity);

            if(username == name){
                convertView = inflater.inflate(myMessageResource, parent, false);
            }
            else{
                convertView = inflater.inflate(otherMessageResource, parent, false);
            }


            holder = new ViewHolder();
        }
        else {
            holder = (ViewHolder) convertView.getTag();
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

    public void notifyDataSetChanged(){

        super.notifyDataSetChanged();
    }
}
