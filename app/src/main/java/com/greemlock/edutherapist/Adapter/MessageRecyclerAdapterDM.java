package com.greemlock.edutherapist.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greemlock.edutherapist.Objects.ObjectMessage;
import com.greemlock.edutherapist.R;

import java.util.List;

public class MessageRecyclerAdapterDM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ObjectMessage> messages;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MessageRecyclerAdapterDM(Context context, List<ObjectMessage> messages){
        this.context = context;
        this.messages = messages;
    }

    public class ViewHolderDM0 extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView message;
        public TextView date;
        public ViewHolderDM0(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
        }
    }

    public class ViewHolderDM1 extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView message;
        public TextView date;
        public ViewHolderDM1(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView;
        switch (viewType){

            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler_dm,parent,false);
                return new ViewHolderDM0(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout_recycler_my_messages_dm,parent,false);
                return new ViewHolderDM1(itemView);

        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        final ObjectMessage objectMessage = messages.get(position);

        switch (holder.getItemViewType()){

            case 1:
                ViewHolderDM1 viewHolderDM1 = (ViewHolderDM1) holder;
                viewHolderDM1.name.setText(user.getDisplayName());
                viewHolderDM1.message.setText(objectMessage.getMessage());
                viewHolderDM1.date.setText(objectMessage.getMessage_date());
                break;
            case 0:
                ViewHolderDM0 viewHolderDM0 = (ViewHolderDM0) holder;
                viewHolderDM0.name.setText(objectMessage.getMessage_name());
                viewHolderDM0.message.setText(objectMessage.getMessage());
                viewHolderDM0.date.setText(objectMessage.getMessage_date());
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = "";
        if (user.getUid() != null){
            userUid = user.getUid();
        }
        if(userUid.equals(objectMessage.getMessage_uid())){
            return 1;
        }
        else{
            return 0;
        }
    }
}
