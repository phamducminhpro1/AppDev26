package com.example.appdev;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
/*
fragmentDialog defines the dialogs where the user uses their access code
The result is returned to R_profileFragment or S_profileFragment, depending on which fragment they were using at the time.
The access code is hard coded to be: 12345678
The dialog returns information to the correct fragments by interfaces
 */
public class fragmentDialog extends DialogFragment {

    //TAG used to identify this fragment while in another fragment
    private static final String TAG = "fragmentDialog";

    //Interface that returns the filled in access code to the host fragment
    public interface OnInputCorrect{
        void sendCode(String code);
    }

    //Interface that returns the cancel status to the host fragment
    public interface OnInputCancel{
        void sendCancel(boolean Cancel);
    }
    public OnInputCorrect OnInputCorrect;
    public OnInputCancel OnInputCancel;

    private EditText editTextCode;
    public TextView actionEnter, actionCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        actionEnter = view.findViewById(R.id.actionEnter);
        actionCancel = view.findViewById(R.id.actionCancel);
        editTextCode = view.findViewById(R.id.editTextCode);


        //Set cancel button
        //cancel status is set to true
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnInputCancel.sendCancel(true);
                Toast.makeText(getActivity(), "Contact TUE to get access code", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });

        // Set enter button
        //If thew code is correct, it is sent to the hosting fragment
        actionEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCode.getText().toString();

                if (code.equals("12345678")){
                    OnInputCorrect.sendCode(code);
                    OnInputCancel.sendCancel(false);
                }else{
                    OnInputCancel.sendCancel(true);
                    Toast.makeText(getActivity(), "Incorrect access code", Toast.LENGTH_LONG).show();
                }
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            OnInputCorrect = (OnInputCorrect) getTargetFragment();
            OnInputCancel = (OnInputCancel) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }
}
