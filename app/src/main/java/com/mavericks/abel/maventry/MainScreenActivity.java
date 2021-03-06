package com.mavericks.abel.maventry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends Activity {

    Button btnViewProducts;
    Button btnNewProduct;
    Button btnLogout;
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        //textview
        tv = (TextView) findViewById(R.id.textView1);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Nero.otf");
        tv.setTypeface(tf);

        // Buttons
        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // view products click event
        btnViewProducts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                startActivity(i);

            }
        });

        // new products click event
        btnNewProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
                startActivity(i);


            }
        });

        // Logout click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching Login
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
}
