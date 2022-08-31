package org.neshan.delivery.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.neshan.delivery.R;
import org.neshan.delivery.adapter.ProductAdapter;
import org.neshan.delivery.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;

    private SQLiteDatabase db;

    private List<Product> products;

    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initLayoutReferences();

        products = new ArrayList<>();
        Product product = new Product();
        product.setImageAddress("https://dkstatics-public.digikala.com/digikala-products/2790394.jpg?x-oss-process=image/resize,m_lfit,h_600,w_600/quality,q_90")
                .setPrice(5900)
                .setId(UUID.randomUUID().toString())
                .setName("نمک تصفیه کریستاله یددار گلستان مقدار ۵۰۰ گرم");

        products.add(product);

        productAdapter = new ProductAdapter(products, new ProductAdapter.OnProductItemListener() {
            @Override
            public void onProductClicked(Product product) {
                Intent intent = new Intent(StoreActivity.this, ProductActivity.class);
                intent.putExtra(ProductActivity.PRODUCT_MODEL, new Gson().toJson(product));
                startActivity(intent);
            }
        });

        recyclerProducts.setAdapter(productAdapter);
    }

    private void initLayoutReferences() {
        initViews();
    }

    private void initViews() {
        recyclerProducts = findViewById(R.id.recycler_products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(StoreActivity.this, LinearLayoutManager.VERTICAL, false));
    }

}