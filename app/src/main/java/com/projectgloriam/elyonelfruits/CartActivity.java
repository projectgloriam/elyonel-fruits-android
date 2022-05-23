package com.projectgloriam.elyonelfruits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectgloriam.elyonelfruits.adapter.CartAdapter;
import com.projectgloriam.elyonelfruits.adapter.FruitAdapter;

public class CartActivity extends AppCompatActivity implements
        View.OnClickListener,
        //PayDialogFragment.FilterListener,
        CartAdapter.OnCartItemDeleteListener{

    private static final String TAG = "CartActivity";

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FirebaseUser user;
    String uid;

    //private PayDialogFragment mPayDialog;
    private CartAdapter mAdapter;
    private RecyclerView mCartRecycler;
    private ViewGroup mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mCartRecycler = findViewById(R.id.recycler_cart);
        mEmptyView = findViewById(R.id.view_empty);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        //Get current user using Firebase methods
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            //user.getDisplayName();
            //user.getEmail();
            //user.getPhotoUrl();
            uid = user.getUid();
        }

        // Get the the order of the current user which hasn't been checked out
        mFirestore.collection("orders")
        .whereEqualTo("paid", false)
        .whereEqualTo("uid", uid)
        .limit(1).get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //fetch the order reference
                        mQuery = mFirestore.collection("orders").document(document.getId()).collection("cartItems");

                    }
                }
            }
        });

    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new CartAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mCartRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mCartRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mCartRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCartRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay:
                showPaymentDialog();
                displayWebView();
                break;
            case R.id.fruit_button_back:
                onBackArrowClicked(v);
                break;
        }
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @Override
    public void onCartItemDelete(DocumentSnapshot cartItem) {
        mFirestore.collection("orders").document(cartItem.getId())
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Item successfully deleted", Toast.LENGTH_SHORT).show();
            }
        });  
    }

    public void showPaymentDialog(){
        DialogFragment payFragment = new PaymentDialogFragment();
        payFragment.show(getSupportFragmentManager(), "checkout");
    }

    public void displayWebView(){
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/form.html");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        // Initialize the SDK♦
        Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_MAPS_API_KEY); //API key

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
    }
}