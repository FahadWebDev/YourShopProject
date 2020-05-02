package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistersActivity extends AppCompatActivity {

    private Button createAccountBtn;
    private EditText inputName, inputPhoneNumber, inputPassword;

    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registers);

        createAccountBtn=findViewById(R.id.register_btn);
        inputName=findViewById(R.id.registerNameInput);

        inputPhoneNumber=findViewById(R.id.registerPhoneNumberInput);
        inputPassword=findViewById(R.id.registerPasswordInput);

        loadingBar=new ProgressDialog(this);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount()
    {
        String name = inputName.getText().toString();
        String number = inputPhoneNumber.getText().toString();

        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Please Enter Your Name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(number))
        {
            Toast.makeText(this,"Please Enter Your Number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");

            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,number,password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String number, final String password)
    {
        final DatabaseReference objectDatabaseReference;
        objectDatabaseReference = FirebaseDatabase.getInstance().getReference();

        objectDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.child("Users").child(number).exists()))
                {
                    HashMap<String,Object> objectHashMap=new HashMap<>();
                    objectHashMap.put("name",name);

                    objectHashMap.put("number",number);
                    objectHashMap.put("password",password);

                    objectDatabaseReference.child("Users").child(number).updateChildren(objectHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegistersActivity.this, "Account has been created successfully! ",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent=new Intent(RegistersActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegistersActivity.this, "Opps! Account doesn't registered, Please try again! ",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegistersActivity.this, "This "+ number +" already exists",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Toast.makeText(RegistersActivity.this,"Please try with any other Phone Number",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegistersActivity.this,MainActivity.class);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
