package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import Prevalent.Prevalent;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private CircleImageView profileImage;
    private EditText settingFullName,settingPhoneNumber, settingAddress;

    private TextView profileChangeBtn, settingCloseBtn, settingSaveBtn;
    private Uri imageUri;

    private String myUrl="";
    private StorageReference storageProfilePictureReference;

    private String checker="";
    private StorageTask uploadTask;

    private Button securityQuestionsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImage = (CircleImageView) findViewById(R.id.settingProfileImg);
        settingFullName = (EditText) findViewById(R.id.settingsFullName);

        settingPhoneNumber = (EditText) findViewById(R.id.settingsPhoneNumber);
        settingAddress = (EditText) findViewById(R.id.settingsAddress);

        profileChangeBtn = (TextView) findViewById(R.id.profileImageChangeBtn);
        settingCloseBtn = (TextView) findViewById(R.id.closeSettingBtn);

        settingSaveBtn = (TextView) findViewById(R.id.updateAccountSettingsBtn);
        securityQuestionsBtn =(Button) findViewById(R.id.securityQuestionsBtn);

        securityQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this,resetPasswordActivity.class);
                intent.putExtra("check", "settings");

                startActivity(intent);
            }
        });

        storageProfilePictureReference= FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        userInfoDisplay(profileImage, settingFullName, settingPhoneNumber, settingAddress);

        settingCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        settingSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference objectDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> objectHashMap = new HashMap<>();

        objectHashMap.put("name",settingFullName.getText().toString());
        objectHashMap.put("address",settingAddress.getText().toString());

        objectHashMap.put("phoneOrder",settingPhoneNumber.getText().toString());
        objectDatabaseReference.child(Prevalent.currentOnlineUser.getNumber()).updateChildren(objectHashMap);

        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));

        Toast.makeText(SettingsActivity.this,"Account Info Updated Successfully",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImage.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this,"Error, Try Again",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if(TextUtils.isEmpty(settingFullName.getText().toString()))
        {
            Toast.makeText(this,"Name is mendatory",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(settingAddress.getText().toString()))
        {
            Toast.makeText(this,"Address is mendatory",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(settingPhoneNumber.getText().toString()))
        {
            Toast.makeText(this,"Number is mendatory",Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog objectProgressDialog = new ProgressDialog(this);
        objectProgressDialog.setTitle("Update Profile");

        objectProgressDialog.setMessage("Please wait, we are updating...");
        objectProgressDialog.setCanceledOnTouchOutside(false);

        objectProgressDialog.show();

        if(imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureReference
                    .child(Prevalent.currentOnlineUser.getNumber() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if(task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference objectDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> objectHashMap = new HashMap<>();
                                objectHashMap.put("name",settingFullName.getText().toString());
                                objectHashMap.put("address",settingAddress.getText().toString());

                                objectHashMap.put("phoneOrder",settingPhoneNumber.getText().toString());
                                objectHashMap.put("image",myUrl);

                                objectDatabaseReference.child(Prevalent.currentOnlineUser.getNumber()).updateChildren(objectHashMap);

                                objectProgressDialog.dismiss();
                                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));

                                Toast.makeText(SettingsActivity.this,"Account Info Updated Successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                objectProgressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(SettingsActivity.this,"Image is not selected!",Toast.LENGTH_SHORT).show();
        }


    }

    private void userInfoDisplay(final CircleImageView profileImage, final EditText settingFullName, final EditText settingPhoneNumber, final EditText settingAddress)
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getNumber());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image= dataSnapshot.child("image").getValue().toString();
                        String name= dataSnapshot.child("name").getValue().toString();

                        String number= dataSnapshot.child("number").getValue().toString();
                        String address= dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        settingFullName.setText(name);

                        settingPhoneNumber.setText(number);
                        settingAddress.setText(address);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
