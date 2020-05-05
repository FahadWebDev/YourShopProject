package Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminEditProductActivity extends AppCompatActivity
{
    private Button applyEditBtn, applyDeleteBtn;
    private TextView editProductName, editProductPrice, editProductDescription;

    private ImageView editProductImage;
    private String productID = "";

    private DatabaseReference productRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        productID = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        setContentView(R.layout.activity_admin_edit_product);
        applyDeleteBtn = findViewById(R.id.applyDeleteProductsBtn);

        applyEditBtn = findViewById(R.id.applyEditProductsBtn);
        editProductName = findViewById(R.id.editProductName);

        editProductPrice = findViewById(R.id.editProductPrice);
        editProductDescription = findViewById(R.id.editProductDescription);

        editProductImage = findViewById(R.id.editProductImage);

        displaySpecificProductInfo();

        applyEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                applyChanges();
            }
        });

        applyDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteThisProduct();

            }
        });
    }

    private void deleteThisProduct()
    {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(AdminEditProductActivity.this, "Product is deleted successfully!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminEditProductActivity.this, AdminCategoryActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }

    private void applyChanges()
    {
        String productName = editProductName.getText().toString();
        String productPrice = editProductPrice.getText().toString();

        String productDescription = editProductDescription.getText().toString();
        if(productName.equals(""))
        {
            Toast.makeText(this, "Write the Product Name", Toast.LENGTH_SHORT).show();
        }
        else if(productPrice.equals(""))
        {
            Toast.makeText(this, "Write the Product Price", Toast.LENGTH_SHORT).show();
        }
        else if(productDescription.equals(""))
        {
            Toast.makeText(this, "Write the Product Description", Toast.LENGTH_SHORT).show();
        }
        else 
        {
            HashMap<String,Object> objectHashMap = new HashMap<>();
            objectHashMap.put("pid" , productID);

            objectHashMap.put("description",productDescription);
            objectHashMap.put("price",productPrice);

            objectHashMap.put("name",productName);

            productRef.updateChildren(objectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminEditProductActivity.this, "Changes Applied Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminEditProductActivity.this,AdminCategoryActivity.class);

                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo()
    {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String productName = dataSnapshot.child("name").getValue().toString();
                    String productPrice = dataSnapshot.child("price").getValue().toString();

                    String productDescription = dataSnapshot.child("description").getValue().toString();
                    String productImage = dataSnapshot.child("image").getValue().toString();

                    editProductName.setText(productName);
                    editProductDescription.setText(productDescription);

                    editProductPrice.setText(productPrice);
                    Picasso.get().load(productImage).into(editProductImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
