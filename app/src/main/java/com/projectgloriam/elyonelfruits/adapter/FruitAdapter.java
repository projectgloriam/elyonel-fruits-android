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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.projectgloriam.elyonelfruits.R;
import com.projectgloriam.elyonelfruits.model.Fruit;
import com.projectgloriam.elyonelfruits.util.FruitUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class FruitAdapter extends FirestoreAdapter<FruitAdapter.ViewHolder> {

    public interface OnFruitSelectedListener {

        void onFruitSelected(DocumentSnapshot restaurant);

    }

    private OnFruitSelectedListener mListener;

    public FruitAdapter(Query query, OnFruitSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_fruit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView descriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fruit_item_image);
            nameView = itemView.findViewById(R.id.fruit_item_name);
            priceView = itemView.findViewById(R.id.fruit_item_price);
            descriptionView = itemView.findViewById(R.id.fruit_item_description);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnFruitSelectedListener listener) {

            Fruit fruit = snapshot.toObject(Fruit.class);
            //Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
            //TODO: Drawable transitions apply
            .load(fruit.getPhoto())
            .into(imageView);

            nameView.setText(fruit.getName());
            descriptionView.setText(fruit.getDescription());
            priceView.setText(FruitUtil.getPriceString(fruit));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onFruitSelected(snapshot);
                    }
                }
            });
        }

    }
}
