package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Products;
import Prevalent.Prevalent;

public class ProductDetailsActivity extends AppCompatActivity
{
    private Button addToCartBtn;
    private ImageView productImageDetail;

    private TextView productNameDetail, productDescriptionDetail, productPriceDetail;
    private ElegantNumberButton quantityBtn;

    private String productID = "", state = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        addToCartBtn = (Button) findViewById(R.id.addProductToCartBtn);
        productImageDetail = (ImageView) findViewById(R.id.productImageDetail);

        productNameDetail = (TextView) findViewById(R.id.productNameDetail);
        productDescriptionDetail = (TextView) findViewById(R.id.productDescriptionDetail);

        productPriceDetail = (TextView) findViewById(R.id.productPriceDetail);
        quantityBtn = (ElegantNumberButton) findViewById(R.id.quantityBtn);

        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(state.equals("Order Placed!") || state.equals("Order Shipped!"))
                {
                    Toast.makeText(ProductDetailsActivity.this,"You can add purchae more products, Once your order is shipped or confirmed.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    addingToCart();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        checkOrderState();
    }

    private void addingToCart()
    {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> objectHashMap = new HashMap<>();

        objectHashMap.put("pid", productID);
        objectHashMap.put("name", productNameDetail.getText().toString());

        objectHashMap.put("price",  productPriceDetail.getText().toString());
        objectHashMap.put("date", saveCurrentDate);

        objectHashMap.put("time", saveCurrentTime);
        objectHashMap.put("quantity", quantityBtn.getNumber());

        objectHashMap.put("discount", " ");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                .child("Products").child(productID).updateChildren(objectHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getNumber())
                                    .child("Products").child(productID).updateChildren(objectHashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(ProductDetailsActivity.this, "Added To Cart List",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);

                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(ProductDetailsActivity.this,"Error, could not added to carts", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    productNameDetail.setText(products.getName());
                    productDescriptionDetail.setText(products.getDescription());

                    productPriceDetail.setText("Rs. " + products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImageDetail);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOrderState()
    {
        DatabaseReference orderRef =  FirebaseDatabase.getInstance().getReference().
                child("Orders").child(Prevalent.currentOnlineUser.getNumber());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        state = "Order shipped!";
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        state = "Order Placed!";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
