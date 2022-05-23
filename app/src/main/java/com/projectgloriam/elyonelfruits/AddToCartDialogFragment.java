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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Dialog Fragment containing rating form.
 */
public class AddToCartDialogFragment extends DialogFragment {

    public static final String TAG = "AddToCartDialog";

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FirebaseUser user;
    String uid;

    private EditText mQuantityText;

    public interface AddToCartDialogListener {
        public void onSubmitClicked(DialogFragment dialog, Integer quantity);
        public void onCancelClicked(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AddToCartDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddToCartDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddToCartDialogListener so we can send events to the host
            listener = (AddToCartDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement AddToCartDialogListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_to_cart, null);

        builder.setView(view)
               .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       EditText editText = view.findViewById(R.id.fruit_quantity_text);
                       Integer quantity = Integer.parseInt(editText.getText().toString());

                       //TODO: pass a quantity argument to dialog
                       // Send the positive button event back to the host activity
                       listener.onSubmitClicked(AddToCartDialogFragment.this, quantity);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       listener.onCancelClicked(AddToCartDialogFragment.this);
                   }
               });
        return builder.create();
    }

}
