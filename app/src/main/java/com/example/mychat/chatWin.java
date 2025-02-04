package com.example.mychat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    private static final String TAG = "chatWin";

    String receiverimg, receieverUid, reciverName, senderUID;
    CircleImageView profile;
    TextView receiverNName;
    ImageView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderIMG;
    public static String receiverIMG;

    String senderRoom, receiverRoom;
    RecyclerView mmsgAdapter;
    ArrayList<msgModelClass> messagesArrayList;
    messagesAdapter messagesAdapter;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        messagesArrayList = new ArrayList<>();
        mmsgAdapter = findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmsgAdapter.setLayoutManager(linearLayoutManager);
        messagesAdapter = new messagesAdapter(chatWin.this, messagesArrayList);
        mmsgAdapter.setAdapter(messagesAdapter);

        reciverName = getIntent().getStringExtra("nameeee");
        receiverimg = getIntent().getStringExtra("receiverimg");
        receieverUid = getIntent().getStringExtra("uid");

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.prifileimgg);
        receiverNName = findViewById(R.id.receivername);

        if (receiverimg != null) {
            Picasso.get().load(receiverimg).into(profile);
        } else {
            Log.e(TAG, "receiverimg is null");
        }

        if (reciverName != null) {
            receiverNName.setText(reciverName);
        } else {
            Log.e(TAG, "reciverName is null");
        }

        senderUID = firebaseAuth.getUid();
        if (senderUID == null) {
            Log.e(TAG, "senderUID is null");
            return;
        }

        senderRoom = senderUID + receieverUid;
        receiverRoom = receieverUid + senderUID;

        DatabaseReference reference = database.getReference().child("user").child(senderUID);
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelClass messages = dataSnapshot.getValue(msgModelClass.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "chatreference onCancelled: " + error.getMessage());
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderIMG = snapshot.child("profilepic").getValue(String.class);
                receiverIMG = receiverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "reference onCancelled: " + error.getMessage());
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(chatWin.this, "Text field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                textmsg.setText("");
                Date date = new Date();
                msgModelClass messagess = new msgModelClass(message, senderUID, date.getTime());
                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messagess);
                        } else {
                            Log.e(TAG, "Failed to send message: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }
    private void scrollToBottom() {
        mmsgAdapter.scrollToPosition(messagesArrayList.size() - 1);
    }
}