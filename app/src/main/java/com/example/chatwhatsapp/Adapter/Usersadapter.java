package com.example.chatwhatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatwhatsapp.Activity.Chatactivity;
import com.example.chatwhatsapp.Models.User;
import com.example.chatwhatsapp.R;
import com.example.chatwhatsapp.databinding.RowconversationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Usersadapter extends RecyclerView.Adapter<Usersadapter.Usersviewholder>{

    Context context;
    ArrayList<User> users;

    public Usersadapter (Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Usersviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rowconversations,parent,false);
        return new Usersviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Usersviewholder holder, int position) {
        final User user = users.get(position);

        String senderUid = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderUid + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String lastmsg = snapshot.child("lstmsg").getValue(String.class);
                            long time = snapshot.child("lstmsgtime").getValue(long.class);
                            SimpleDateFormat simple = new SimpleDateFormat("hh:mm a");
                            String datetime = simple.format(time);

                            holder.binding.time.setText(datetime);
                            holder.binding.lstmsg.setText(lastmsg);
                        }
                        else{
                            holder.binding.lstmsg.setText("Tap to Chat");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.name.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.primg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chatactivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Usersviewholder extends RecyclerView.ViewHolder{

        RowconversationsBinding binding;

        public Usersviewholder(@NonNull View itemView) {
            super(itemView);
            binding = RowconversationsBinding.bind(itemView);
        }
    }

}
