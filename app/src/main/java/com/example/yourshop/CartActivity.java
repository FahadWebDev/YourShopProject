package com.example.yourshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Cart;
import Prevalent.Prevalent;
import ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView objectRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button nextProcessBtn;
    private TextView totalAmount, msg1;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        objectRecyclerView = findViewById(R.id.cart_List);
        objectRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        objectRecyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) findViewById(R.id.next_Process_Btn);
        totalAmount = (TextView) findViewById(R.id.total_price);

        msg1 = (TextView) findViewById(R.id.msg1);

        nextProcessBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));

                startActivity(intent);
                finish();

            }
        });
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        overTotalPrice = 0;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                        .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.productQuantity.setText("Quantity = " + model.getQuantity());
                holder.productName.setText(model.getName());
                holder.productPrice.setText("Price : " + model.getPrice());

                int oneTypeProductTPrice=(Integer.parseInt(model.getPrice().replaceAll("\\D+","")))
                        * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                totalAmount.setText("Total Price = Rs. " + String.valueOf(overTotalPrice));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                    "Edit",
                                    "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());

                                    startActivity(intent);
                                }
                                if(which == 1)
                                {
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                                            .child("Products").child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this,"Item Removed Successfully!",Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        objectRecyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        totalAmount.setText("Dear " + userName + "\nYour order is shipped successfully!");
                        objectRecyclerView.setVisibility(View.GONE);

                        msg1.setText("Congratulation, Your final order has been shipped successfully. Soon you will receive your order at your doorstep");
                        msg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"You can purchase more products once you received your first final order!",Toast.LENGTH_LONG).show();

                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        totalAmount.setText("Shipping State = Not Shipped");
                        objectRecyclerView.setVisibility(View.GONE);

                        msg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"You can purchase more products once you received your first final order!",Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
