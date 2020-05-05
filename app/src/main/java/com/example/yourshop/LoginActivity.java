package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Admin.AdminCategoryActivity;
import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText inputNumber, inputPassword;

    private ProgressDialog loadingBar;
    private String parentDbName = "Users";

    private CheckBox checkBoxRememberMe;
    private TextView adminLink, notAdminLink, forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn=findViewById(R.id.login_btn);
        inputNumber=findViewById(R.id.loginPhoneNumberInput);

        forgetPassword=findViewById(R.id.forget_password);

        inputPassword=findViewById(R.id.loginPasswordInput);
        loadingBar= new ProgressDialog(this);

        checkBoxRememberMe=(CheckBox) findViewById(R.id.rememberMeBox);
        Paper.init(this);

        adminLink= (TextView) findViewById(R.id.adminTV);
        notAdminLink= (TextView) findViewById(R.id.notAdminTV);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this,resetPasswordActivity.class);
                intent.putExtra("check", "login");

                startActivity(intent);
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginBtn.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);

                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName ="Admins";

                checkBoxRememberMe.setVisibility(View.INVISIBLE);

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginBtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);

                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName ="Users";

            }
        });

    }

    private void loginUser()
    {
        String phone = inputNumber.getText().toString();
        String password=inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait...");

            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone,password);

        }
    }

    private void allowAccessToAccount(final String phone, final String password)
    {
        if(checkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference objecDatabaseReference;
        objecDatabaseReference = FirebaseDatabase.getInstance().getReference();

        objecDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(usersData.getNumber().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this,"Welcome Admin, Login Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent=new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Prevalent.currentOnlineUser = usersData;
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Number or Password is incorrect!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Account with this "+ phone +" number is not registered!",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
