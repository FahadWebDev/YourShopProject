package Admin;

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

import com.example.yourshop.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.AdminOrders;

public class AdminNewOrdersActivity extends AppCompatActivity
{
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_List);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> option = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,adminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, adminOrdersViewHolder>(option) {
                    @Override
                    protected void onBindViewHolder(@NonNull adminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {
                        holder.orderUserName.setText("Name: " + model.getName() );
                        holder.orderUserNumber.setText("Phone: " + model.getNumber() );

                        holder.orderUserAddress.setText("Shipping Address: " + model.getAddress() + " " + model.getCity() );
                        holder.orderTotalPrice.setText("Total Amount = Rs. " + model.getTotalAmount() );

                        holder.orderDateTime.setText("Order at : " + model.getDate() + " " + model.getTime());
                        holder.showOrderProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String uID = getRef(position).getKey();
                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);

                                startActivity(intent);

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have You Shipped This Order?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which == 0)
                                        {
                                            String uID = getRef(position).getKey();
                                            removeOrder(uID);
                                        }
                                        else
                                        {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public adminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                        return new adminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class adminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView orderUserName, orderUserNumber,  orderUserAddress, orderTotalPrice, orderDateTime;
        public Button showOrderProductsBtn;

        public adminOrdersViewHolder(@NonNull View itemView)
        {
            super(itemView);

            showOrderProductsBtn = itemView.findViewById(R.id.showOrderProductsBtn);
            orderUserName = itemView.findViewById(R.id.orderUserName);

            orderUserNumber = itemView.findViewById(R.id.orderPhoneNumber);
            orderUserAddress = itemView.findViewById(R.id.orderAddressCity);

            orderTotalPrice = itemView.findViewById(R.id.orderTotalPrice);
            orderDateTime = itemView.findViewById(R.id.orderDateTime);
        }
    }

    private void removeOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }
}
