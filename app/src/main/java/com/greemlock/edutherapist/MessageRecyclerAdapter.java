package com.greemlock.edutherapist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ObjectMessage> messages;

    public MessageRecyclerAdapter(Context context, List<ObjectMessage> messages){
        this.context = context;
        this.messages = messages;
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView date;
        public ImageView imageView;

        public ViewHolder0(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView date;
        public ImageView imageView;

        public ViewHolder1(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler,parent,false);
                return new ViewHolder0(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler_my_messages,parent,false);
                return new ViewHolder1(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ObjectMessage objectMessage = messages.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0)holder;
                viewHolder0.name.setText(objectMessage.getMessage_name());
                viewHolder0.message.setText(objectMessage.getMessage());
                viewHolder0.date.setText(objectMessage.getMessage_date());
                viewHolder0.imageView.setImageResource(R.drawable.ic_baseline_person_24);
                break;

            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.name.setText(objectMessage.getMessage_name());
                viewHolder1.message.setText(objectMessage.getMessage());
                viewHolder1.date.setText(objectMessage.getMessage_date());
                viewHolder1.imageView.setImageResource(R.drawable.ic_baseline_person_24);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        final ObjectMessage objectMessage = messages.get(position);
        String username = SaveSharedPreferences.getPrefName(context.getApplicationContext());

        if(username.equals(objectMessage.getMessage_name())){

            return 1;
        }
        else{
            return 0;
        }
    }

}
