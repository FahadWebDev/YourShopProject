package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Prevalent.Prevalent;

public class resetPasswordActivity extends AppCompatActivity
{
    private String check ="";
    private TextView resetPassSlogan, questionsSlogan;

    private EditText resetPassPhone, resetPassQ1, resetPassQ2;
    private Button resetPassVerifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        resetPassSlogan = findViewById(R.id.resetPasswordSlogan);

        questionsSlogan = findViewById(R.id.answerQuestionsSlogan);
        resetPassPhone = findViewById(R.id.resetPassNumber);

        resetPassQ1 = findViewById(R.id.question1);
        resetPassQ2 = findViewById(R.id.question2);

        resetPassVerifyBtn = findViewById(R.id.verifyBtn);


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        resetPassPhone.setVisibility(View.GONE);
        if(check.equals("settings"))
        {
            resetPassVerifyBtn.setText("Set");
            resetPassSlogan.setText("Set Questions");

            questionsSlogan.setText("Please answer the following security questions");
            displayPreviousAnswers();

            resetPassVerifyBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setAnswers();
                }
            });
        }
        else if(check.equals("login"))
        {
            resetPassPhone.setVisibility(View.VISIBLE);
            resetPassVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    verifyQuestion();
                }
            });
        }
    }

    private void setAnswers()
    {
        String answer1 = resetPassQ1.getText().toString().toLowerCase();
        String answer2 = resetPassQ2.getText().toString().toLowerCase();

        if(resetPassQ1.equals("") && resetPassQ2.equals(""))
        {
            Toast.makeText(resetPasswordActivity.this, "Please answers both questions", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").
                    child(Prevalent.currentOnlineUser.getNumber());

            HashMap<String,Object> objectHashMap=new HashMap<>();
            objectHashMap.put("answer1",answer1);

            objectHashMap.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(resetPasswordActivity.this, "You have answers the security questions successfully", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(resetPasswordActivity.this,HomeActivity.class);

                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void displayPreviousAnswers()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").
                child(Prevalent.currentOnlineUser.getNumber());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();

                    resetPassQ1.setText(ans1);
                    resetPassQ2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void verifyQuestion()
    {
        final String phoneNumber= resetPassPhone.getText().toString();

        final String answer1 = resetPassQ1.getText().toString().toLowerCase();
        final String answer2 = resetPassQ2.getText().toString().toLowerCase();

        if(!phoneNumber.equals("") && !answer1.equals("") && !answer2.equals(""))
        {
            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").
                    child(phoneNumber);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        String mNumber = dataSnapshot.child("number").getValue().toString();

                        if(dataSnapshot.hasChild("Security Questions"))
                        {
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if(!ans1.equals(answer1))
                            {
                                Toast.makeText(resetPasswordActivity.this, "Your answer 1 is incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else if(!ans2.equals(answer2))
                            {
                                Toast.makeText(resetPasswordActivity.this, "Your answer 2 is incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(resetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(resetPasswordActivity.this);
                                newPassword.setHint("Enter New Password");

                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(!newPassword.getText().toString().equals(""))
                                        {
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(resetPasswordActivity.this, "Password has changed successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(resetPasswordActivity.this,LoginActivity.class);

                                                                startActivity(intent);
                                                            }

                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        }
                        else
                        {
                            Toast.makeText(resetPasswordActivity.this, "You have not set the security questions", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(resetPasswordActivity.this, "This Phone Number Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
        else
        {
            Toast.makeText(this, "Please fill the complete form", Toast.LENGTH_SHORT).show();
        }


    }
}
