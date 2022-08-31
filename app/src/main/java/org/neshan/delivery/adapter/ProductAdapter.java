package org.neshan.delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.neshan.common.model.LatLng;
import org.neshan.delivery.R;
import org.neshan.delivery.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private OnProductItemListener onProductItemListener;

    public ProductAdapter(List<Product> products, OnProductItemListener onProductItemListener) {
        this.products = products;
        this.onProductItemListener = onProductItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(products.get(position).getImageAddress()).into(holder.imgProduct);
        holder.lblName.setText(products.get(position).getName());
        holder.lblPrice.setText(products.get(position).getPrice() + " " + holder.context.getString(R.string.tooman));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView imgProduct;
        private AppCompatTextView lblName;
        private AppCompatTextView lblPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imgProduct = itemView.findViewById(R.id.img_product);
            lblName = itemView.findViewById(R.id.lbl_name);
            lblPrice = itemView.findViewById(R.id.lbl_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProductItemListener.onProductClicked(products.get(getAdapterPosition()));
        }
    }

    public interface OnProductItemListener {
        void onProductClicked(Product product);
    }
}
