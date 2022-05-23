package com.projectgloriam.elyonelfruits.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.projectgloriam.elyonelfruits.R;
import com.projectgloriam.elyonelfruits.model.CartItem;
import com.projectgloriam.elyonelfruits.model.Fruit;
import com.projectgloriam.elyonelfruits.util.FruitUtil;

public class CartAdapter extends FirestoreAdapter<CartAdapter.ViewHolder> {

    public interface OnCartItemDeleteListener {

        void onCartItemDelete(DocumentSnapshot restaurant);

    }

    private OnCartItemDeleteListener mListener;

    public CartAdapter(Query query, OnCartItemDeleteListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView quantityView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fruit_cart_image);
            nameView = itemView.findViewById(R.id.fruit_cart_name);
            priceView = itemView.findViewById(R.id.fruit_cart_price);
            quantityView = itemView.findViewById(R.id.fruit_cart_quantity);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCartItemDeleteListener listener) {

            CartItem cartItem = snapshot.toObject(CartItem.class);
            final Fruit[] fruit = new Fruit[1];
            Resources resources = itemView.getResources();
            cartItem.getFruit().get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    fruit[0] = documentSnapshot.toObject(Fruit.class);
                }
            });

            // Load image
            Glide.with(imageView.getContext())
                    .load(fruit[0].getPhoto())
                    .into(imageView);

            nameView.setText(fruit[0].getName());
            quantityView.setText(fruit[0].getDescription());
            priceView.setText(FruitUtil.getPriceString(fruit[0]));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCartItemDelete(snapshot);
                    }
                }
            });
        }

    }
}