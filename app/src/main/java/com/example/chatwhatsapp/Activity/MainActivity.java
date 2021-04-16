package com.example.chatwhatsapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatwhatsapp.Adapter.TopStatusAdapter;
import com.example.chatwhatsapp.Adapter.Usersadapter;
import com.example.chatwhatsapp.Models.StaTus;
import com.example.chatwhatsapp.Models.User;
import com.example.chatwhatsapp.Models.UserStatus;
import com.example.chatwhatsapp.R;
import com.example.chatwhatsapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    Usersadapter usersadapter;
    TopStatusAdapter topStatusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.setCancelable(false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usersadapter = new Usersadapter(this,users);
        topStatusAdapter = new TopStatusAdapter(this,userStatuses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusview.setLayoutManager(layoutManager);

        binding.statusview.setAdapter(topStatusAdapter);
        binding.recycleview.setAdapter(usersadapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }

                usersadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userStatuses.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(snapshot1.child("name").getValue(String.class));
                        userStatus.setProImg(snapshot1.child("proImg").getValue(String.class));
                        userStatus.setLstUpd(snapshot1.child("lstUpd").getValue(Long.class));

                        ArrayList<StaTus> staTuses = new ArrayList<>();

                        for(DataSnapshot statusSnap: snapshot1.child("status").getChildren()){
                            StaTus sampleStatus = statusSnap.getValue(StaTus.class);
                            staTuses.add(sampleStatus);
                        }
                        userStatus.setStaTuses(staTuses);
                        userStatuses.add(userStatus);
                    }
                    topStatusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();
            if(data!=null){
                if(data.getData()!=null){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final Date date = new Date();
                    final StorageReference reference = storage.getReference().child("status").child(date.getTime()+"");

                    reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();
                                        UserStatus userStatus = new UserStatus();
                                        userStatus.setName(user.getName());
                                        userStatus.setProImg(user.getProfileImage());
                                        userStatus.setLstUpd(date.getTime());

                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("name",userStatus.getName());
                                        obj.put("proImg",userStatus.getProImg());
                                        obj.put("lstUpd",userStatus.getLstUpd());

                                        String imageUri = uri.toString();
                                        StaTus staTus = new StaTus(imageUri,userStatus.getLstUpd());

                                        database.getReference().child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(obj);

                                        database.getReference().child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("status")
                                                .push()
                                                .setValue(staTus);

                                    }
                                });
                            }
                        }
                    });
                }
            }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}