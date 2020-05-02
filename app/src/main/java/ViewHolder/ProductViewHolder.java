package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourshop.R;

import Interface.ItemClickListner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productName, productDescription, productPrice;

    public ImageView productImage;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.productName);
        productDescription = (TextView) itemView.findViewById(R.id.productDescription);

        productImage = (ImageView) itemView.findViewById(R.id.productImage);
        productPrice = (TextView) itemView.findViewById(R.id.productPrice);
    }

    public void setItemClickListener(ItemClickListner listner)
    {
        this.listner=listner;
    }

    @Override
    public void onClick(View v)
    {
        listner.onClick(v, getAdapterPosition(), false);
    }
}
