package com.example.appdev;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/*
Dialog for the register activity
The dialog is created when the user presses the register button
The dialog has an EditText where the user can fill in their access code
 */
public class CodeDialog extends AppCompatDialogFragment {
    private CodeDialogListener listener;
    private EditText editCode;

    //Define the creation of the dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editCode = view.findViewById(R.id.editCode);

        builder.setView(view)
                .setTitle("Enter access code")
                //Set the negative button, on click the dialog closes
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })//Set Enter button, on click, send the string to the activity
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String code = editCode.getText().toString().trim();

                        listener.sendCode(code);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CodeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement CodeDialogListener");
        }
    }

    //Through this interface, the code is sent to the register activity
    public interface CodeDialogListener{
        void sendCode(String code);
    }
}
