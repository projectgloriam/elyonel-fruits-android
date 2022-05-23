/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.projectgloriam.elyonelfruits;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectgloriam.elyonelfruits.model.CartItem;
import com.projectgloriam.elyonelfruits.model.Fruit;
import com.projectgloriam.elyonelfruits.model.Order;
import com.projectgloriam.elyonelfruits.util.FruitUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FruitDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        AddToCartDialogFragment.AddToCartDialogListener{

    private static final String TAG = "FruitDetail";

    public static final String KEY_FRUIT_ID = "key_fruit_id";

    private ImageView mImageView;
    private TextView mNameView;
    private TextView mCategoryView;
    private TextView mPriceView;
    private TextView mDescriptionView;

    private AddToCartDialogFragment mAddToCartDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mFruitRef;
    private ListenerRegistration mFruitRegistration;
    FirebaseUser user;
    CollectionReference ordersRef;
    String uid;
    Order order;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);
        
        mImageView = findViewById(R.id.fruit_image);
        mNameView = findViewById(R.id.fruit_name);
        mCategoryView = findViewById(R.id.fruit_category);
        mPriceView = findViewById(R.id.fruit_price);
        mDescriptionView = findViewById(R.id.fruit_description);

        findViewById(R.id.fruit_button_back).setOnClickListener(this);
        findViewById(R.id.fab_add_to_cart_dialog).setOnClickListener(this);

        // Get fruit juice ID from extras
        String fruitId = getIntent().getExtras().getString(KEY_FRUIT_ID);
        if (fruitId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_FRUIT_ID);
        }

        // Initialize Firestore
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

        // Get reference to the restaurant
        mFruitRef = mFirestore.collection("fruits").document(fruitId);

        ordersRef = mFirestore.collection("orders");

        mAddToCartDialog = new AddToCartDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mFruitRegistration = mFruitRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mFruitRegistration != null) {
            mFruitRegistration.remove();
            mFruitRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fruit_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_add_to_cart_dialog:
                onAddToCartClicked(v);
                break;
        }
    }

    /**
     * Listener for the Fruit document ({@link #mFruitRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "fruit:onEvent", e);
            return;
        }

        onFruitLoaded(snapshot.toObject(Fruit.class));
    }

    private void onFruitLoaded(Fruit fruit) {
        mNameView.setText(fruit.getName());
        mCategoryView.setText(fruit.getCategory());
        mDescriptionView.setText(fruit.getDescription());
        mPriceView.setText(FruitUtil.getPriceString(fruit));

        // Background image
        Glide.with(mImageView.getContext())
                .load(fruit.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddToCartClicked(View view) {
        mAddToCartDialog.show(getSupportFragmentManager(), AddToCartDialogFragment.TAG);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSubmitClicked(DialogFragment dialog, Integer quantity) {
        //Adding medicine reference to the cart
        final CartItem cart_item = new CartItem(mFruitRef, quantity);

        //Fetching order of the user which hasn't been checked.
        ordersRef.whereEqualTo("paid", false)
        .whereEqualTo("uid", uid)
        .limit(1)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {

                            //If found, add cart item to its subcollection of carts
                            document.getReference().collection("cartList")
                                    .add(cart_item);
                            //On success, display a Toast message that item is added to the cart
                            Toast.makeText(context, R.string.added_item_to_cart, Toast.LENGTH_SHORT).show();

                        } else {
                            //Create a new order if it doesn't exist
                            order = new Order(uid);
                            ordersRef.add(order)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference document) {

                                    //and add the item to the cart list
                                    document.collection("cartList")
                                    .add(cart_item)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference document) {
                                            //On success of adding cart item, display a Toast message that item is added to the cart
                                            Toast.makeText(context, R.string.added_item_to_cart, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //On failure of adding cart item, display a Toast message telling the user to try again.
                                            Toast.makeText(context, "A problem occurred. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //On failure to add a new order, display a Toast message telling the user to try again.
                                    Toast.makeText(context, "A problem occurred. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    //On failure to fetch order of the user which hasn't been checked, display a Toast message telling the user to try again.
                    Toast.makeText(getApplicationContext(), "An error occurred. Please go back and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.dismiss();
    }

    @Override
    public void onCancelClicked(DialogFragment dialog) {
        dialog.dismiss();
    }
}
