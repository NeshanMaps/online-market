package org.neshan.delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.neshan.delivery.R;
import org.neshan.delivery.model.Product;

public class ProductActivity extends AppCompatActivity {

    public static final String PRODUCT_MODEL = "PRODUCT_MODEL";

    private ImageView imgProduct;
    private AppCompatTextView lblName;
    private AppCompatTextView lblPrice;
    private AppCompatButton btnCompleteOrder;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initLayoutReferences();

        if (getIntent() != null && getIntent().getStringExtra(PRODUCT_MODEL) != null) {
            product = new Gson().fromJson(getIntent().getStringExtra(PRODUCT_MODEL), Product.class);
            Picasso.get().load(product.getImageAddress()).into(imgProduct);
            lblName.setText(product.getName());
            lblPrice.setText(product.getPrice() + " " + getString(R.string.tooman));
            btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProductActivity.this, AddressActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            finish();
        }
    }

    private void initLayoutReferences() {
        initViews();
    }

    private void initViews() {
        imgProduct = findViewById(R.id.img_product);
        lblName = findViewById(R.id.lbl_name);
        lblPrice = findViewById(R.id.lbl_price);
        btnCompleteOrder = findViewById(R.id.btn_complete_order);
    }
}