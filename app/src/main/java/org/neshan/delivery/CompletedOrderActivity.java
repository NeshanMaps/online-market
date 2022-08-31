package org.neshan.delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.IntentCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.neshan.delivery.activity.MainActivity;

public class CompletedOrderActivity extends AppCompatActivity {

    private AppCompatButton btnCompleteOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_order);

        initLayoutreferences();

        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents = new Intent(CompletedOrderActivity.this, MainActivity.class);
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intents);
                finish();
            }
        });
    }

    private void initLayoutreferences() {
        initViews();
    }

    private void initViews() {
        btnCompleteOrder = findViewById(R.id.btn_complete_order);
    }
}