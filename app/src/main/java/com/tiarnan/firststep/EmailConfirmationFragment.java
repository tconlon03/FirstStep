package com.tiarnan.firststep;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.tiarnan.firststep.utilities.formatUtilities.timestampToDayMonthYear;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSendConfirmationListener} interface
 * to handle interaction events.
 * Use the {@link EmailConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailConfirmationFragment extends Fragment {

    private static final String email_confirmation_tag = "EMAIL_FRAGMENT_LOGGING";

    private static final String ARG_NAME = "Name";
    private static final String ARG_DOB = "DateofBirth";
    private static final String ARG_EMAIL = "Email";
    private static String mName;
    private static long mDOB;
    private static String mEmail;
    private Button btn_send_email;
    private TextView tv_confirm_name;
    private TextView tv_confirm_dob;
    private TextView tv_confirm_email;

    private OnSendConfirmationListener mListener;

    public EmailConfirmationFragment() {
        // Required empty public constructor
    }

    public static EmailConfirmationFragment newInstance(String name, long dob, String email) {
        EmailConfirmationFragment fragment = new EmailConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DOB, dob);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        Log.i(email_confirmation_tag, "Email Confirmation Constructor");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(email_confirmation_tag, "Email Confirmation onCreate");
        if (getArguments() != null) {
            Log.i(email_confirmation_tag, "Arguments not null");
            mName = getArguments().getString(ARG_NAME);
            mDOB = getArguments().getLong(ARG_DOB);
            mEmail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_confirmation, container, false);
        btn_send_email = view.findViewById(R.id.buttonSendConfirmEmail);
        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendConfirmationEmail();
            }
        });
        tv_confirm_name = view.findViewById(R.id.textViewConfirmName);
        tv_confirm_name.setText(mName);
        Log.i(email_confirmation_tag, "mName : " + mName);
        tv_confirm_dob =  view.findViewById(R.id.textViewConfirmDOB);
        int dmy[] = timestampToDayMonthYear(mDOB);
        tv_confirm_dob.setText(Integer.toString(dmy[0]) + '/' + Integer.toString(dmy[1]) + '/' + Integer.toString(dmy[2]));
        tv_confirm_email = view.findViewById(R.id.textViewConfirmEmail);
        tv_confirm_email.setText(mEmail);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSendConfirmationListener) {
            mListener = (OnSendConfirmationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
