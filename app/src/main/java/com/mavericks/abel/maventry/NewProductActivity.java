package com.mavericks.abel.maventry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class NewProductActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    //CoordinatorLayout coordinatorLayout;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_products = "http://mavericks.ga/php/get_all_products.php"; //"http://192.168.0.18/android_connect/get_all_products.php"; //"http://ebhojan.ga/get_all_products.php"; //"http://warferns.5gbfree.com/android_connect/get_all_products.php";

    // JSON Node names
    //private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_QTY = "quantity";
    private static final String TAG_SOURCE = "source";
    private static final String TAG_DESCRIPTION = "description";

    // products JSONArray
    JSONArray products = null;


    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputQuantity;
    EditText inputSource;
    EditText inputDesc;

    // url to create new product
    private static String url_create_product = "http://mavericks.ga/php/create_product.php";  //"http://192.168.0.18/android_connect/create_product.php";

    // url to update product
    private static final String url_update_product = "http://mavericks.ga/php/update_product.php"; //"http://192.168.0.18/android_connect/update_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);
        inputQuantity = (EditText) findViewById(R.id.inputQuantity);
        inputSource = (EditText) findViewById(R.id.inputSource);
        inputDesc = (EditText) findViewById(R.id.inputDesc);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

        //For Snackbar
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread


                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if (cd.isConnectingToInternet()) // true or false
                {
                    new CreateNewProduct().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "NO INTERNET :|",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Background Async Task to Create new product
     */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewProductActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        boolean flag = false;
        String name = inputName.getText().toString();
        String quantity = inputQuantity.getText().toString();
        String source = inputSource.getText().toString();
        String description = inputDesc.getText().toString();


        /**
         * Creating product / Updating existing product
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> Params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject Json = jParser.makeHttpRequest(url_all_products, "GET", Params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", Json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = Json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = Json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String dbname = c.getString(TAG_NAME);
                        String dbquantity = c.getString(TAG_QTY);

                        //If product exists, Update the quantity of existing product else, Create new product.
                        if (name.equalsIgnoreCase(dbname)) {
                            String finalQuantity = Integer.toString(Integer.parseInt(quantity) + Integer.parseInt(dbquantity));
                            // Building Parameters
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair(TAG_PID, id));
                            params.add(new BasicNameValuePair(TAG_NAME, dbname));
                            params.add(new BasicNameValuePair(TAG_QTY, finalQuantity));
                            params.add(new BasicNameValuePair(TAG_SOURCE, c.getString(TAG_SOURCE)));
                            params.add(new BasicNameValuePair(TAG_DESCRIPTION, c.getString(TAG_DESCRIPTION)));

                            // sending modified data through http request
                            // Notice that update product url accepts POST method
                            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                                    "POST", params);

                            // check json success tag
                            try {
                                success = json.getInt(TAG_SUCCESS);

                                if (success == 1) {
                                    // successfully updated
                                    Intent j = getIntent();
                                    // send result code 100 to notify about product update
                                    setResult(100, j);
                                    finish();
                                } else {
                                    // failed to update product
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            flag = true;
                            return null;
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "NO INTERNET :|",
                        Toast.LENGTH_LONG).show();
            }


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("quantity", quantity));
            params.add(new BasicNameValuePair("source", source));
            params.add(new BasicNameValuePair("description", description));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {

            if (flag) {
                Context context = getApplicationContext();
                CharSequence text = "Product entered already exists. Updating product...";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                /*
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Product entered already exists. Updating product...", Snackbar.LENGTH_LONG);

                snackbar.show();
                */
            }
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}
