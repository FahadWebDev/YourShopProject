package Admin;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yourshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class adminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, productName, saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;
    private Button addProductBtn;

    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;

    private  static final int galleryPick = 1;
    private Uri imageUri;

    private StorageReference productImageRef;
    private DatabaseReference productReference;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        loadingBar= new ProgressDialog(this);

        productImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        productReference=FirebaseDatabase.getInstance().getReference().child("Products");

        addProductBtn=(Button) findViewById(R.id.addProductBtn);
        inputProductDescription=(EditText) findViewById(R.id.productDescriptionET);

        inputProductName=(EditText) findViewById(R.id.productNameET);
        inputProductPrice=(EditText) findViewById(R.id.productPriceET);

        inputProductImage=(ImageView) findViewById(R.id.select_productIV);
        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateProductData();
            }
        });
    }

    private void openGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    private void validateProductData()
    {
        description= inputProductDescription.getText().toString();
        price= inputProductPrice.getText().toString();

        productName= inputProductName.getText().toString();
        if(imageUri==null)
        {
            Toast.makeText(this,"Product image is mandatory...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this,"Please write product description...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this,"Please write product price...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productName))
        {
            Toast.makeText(this,"Please write product name...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            storeProductInformation();
        }

    }
    private void storeProductInformation()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait...");

        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender= Calendar.getInstance();

        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        productRandomKey = saveCurrentDate+saveCurrentTime;
        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask objectUploadTask = filePath.putFile(imageUri);

        objectUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(adminAddNewProductActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(adminAddNewProductActivity.this,"Product Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(adminAddNewProductActivity.this,"Got the Product Image Url Successfully!",Toast.LENGTH_SHORT).show();

                            saveProductInfoToDB();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDB()
    {
        HashMap<String,Object> objectHashMap = new HashMap<>();
        objectHashMap.put("pid",productRandomKey);

        objectHashMap.put("date",saveCurrentDate);
        objectHashMap.put("time",saveCurrentTime);

        objectHashMap.put("description",description);
        objectHashMap.put("image",downloadImageUrl);

        objectHashMap.put("category",categoryName);
        objectHashMap.put("price",price);

        objectHashMap.put("name",productName);
        productReference.child(productRandomKey).updateChildren(objectHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(adminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(adminAddNewProductActivity.this,"Product is added successfully!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();

                            String message = task.getException().toString();
                            Toast.makeText(adminAddNewProductActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
