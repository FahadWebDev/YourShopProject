package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText shipmentName, shipmentNumber, shipmentCity, shipmentAddress;
    private Button confirmOrderBtn;

    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this,"Total Price = " + totalAmount ,Toast.LENGTH_SHORT).show();

        shipmentName = (EditText) findViewById(R.id.shipment_name);
        shipmentNumber = (EditText) findViewById(R.id.shipment_phone_number);

        shipmentCity = (EditText) findViewById(R.id.shipment_city);
        shipmentAddress = (EditText) findViewById(R.id.shipment_address);

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    check();
            }
        });
    }

    private void check()
    {
        if(TextUtils.isEmpty(shipmentName.getText().toString()))
        {
            Toast.makeText(this,"Please provide your full name...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(shipmentNumber.getText().toString()))
        {
            Toast.makeText(this,"Please provide your number...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(shipmentCity.getText().toString()))
        {
            Toast.makeText(this,"Please provide your city...",Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(shipmentAddress.getText().toString()))
        {
            Toast.makeText(this,"Please provide your full addres...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            confirmOrder();
        }
    }

    private void confirmOrder()
    {
        final String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getNumber());

        HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", shipmentName.getText().toString());

        ordersMap.put("number",  shipmentNumber.getText().toString());
        ordersMap.put("city",  shipmentCity.getText().toString());

        ordersMap.put("address",  shipmentAddress.getText().toString());
        ordersMap.put("date", saveCurrentDate);

        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getNumber())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"Your order has been placed successfully.",Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}
