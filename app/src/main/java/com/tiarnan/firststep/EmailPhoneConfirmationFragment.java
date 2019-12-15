package com.tiarnan.firststep;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.tiarnan.firststep.utilities.formatUtilities.timestampToDayMonthYear;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSendConfirmationListener} interface
 * to handle interaction events.
 * Use the {@link EmailPhoneConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailPhoneConfirmationFragment extends Fragment{

    private static final String ARG_NAME = "Name";
    private static final String ARG_DOB = "DateofBirth";
    private static final String ARG_EMAIL = "Email";
    private static final String ARG_PHONE = "Phone";
    private static String mName;
    private static Long mDOB;
    private static String mEmail;
    private static String mPhone;
    private Button btn_send_email;
    private Button btn_send_text;
    private TextView tv_confirm_name;
    private TextView tv_confirm_dob;
    private TextView tv_confirm_email;
    private TextView tv_confirm_phone;
    private EditText et_verification_code;
    private OnSendConfirmationListener mListener;
    private TextValidator codeValidator;

    public EmailPhoneConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * @return A new instance of fragment EmailPhoneConfirmationFragment.
     */
    public static EmailPhoneConfirmationFragment newInstance(String name, long dob, String email, String phone) {
        EmailPhoneConfirmationFragment fragment = new EmailPhoneConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DOB, dob);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mDOB = getArguments().getLong(ARG_DOB);
            mEmail = getArguments().getString(ARG_EMAIL);
            mPhone = getArguments().getString(ARG_PHONE);
        }
        codeValidator = new TextValidator() {
            @Override
            public void afterTextChanged(Editable s) {
                String formatted_s = formatVerificationCode(s.toString());
                s.replace(0, formatted_s.length(), formatted_s);
                if (formatted_s.length() == 6){
                    mListener.validateEnteredVerificationCode(s.toString());
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_phone_confirmation, container, false);
        btn_send_email = view.findViewById(R.id.buttonSendConfirmEmail);
        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendConfirmationEmail();
            }
        });
        btn_send_text = view.findViewById(R.id.buttonSendConfirmText);
        btn_send_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendConfirmationText();
            }
        });
        tv_confirm_name = view.findViewById(R.id.textViewConfirmName);
        tv_confirm_name.setText(mName);
        tv_confirm_dob =  view.findViewById(R.id.textViewConfirmDOB);
        int dmy[] = timestampToDayMonthYear(mDOB);
        tv_confirm_dob.setText(Integer.toString(dmy[0]) + '/' + Integer.toString(dmy[1]) + '/' + Integer.toString(dmy[2]));
        tv_confirm_email = view.findViewById(R.id.textViewConfirmEmail);
        tv_confirm_email.setText(mEmail);
        tv_confirm_phone = view.findViewById(R.id.textViewConfirmPhone);
        tv_confirm_phone.setText(mPhone);
        et_verification_code  = view.findViewById(R.id.editTextEnterVerification);
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

    public void onReceiveCode(){
        et_verification_code.setVisibility(View.VISIBLE);
        btn_send_text.setText(R.string.send_another_text);
        et_verification_code.addTextChangedListener(codeValidator);
    }

    public void onAutoReceiveCode(String autoRetreivedCode) {
        et_verification_code.setText(autoRetreivedCode);
    }

    public void onIncorrectCode(){
        et_verification_code.setText("");
        btn_send_text.setText(R.string.send_another_text);
        Toast toast = Toast.makeText(getActivity(), "Sorry you entered an incorrect code" +
                "\n Please request a new code when ready", Toast.LENGTH_LONG);
        toast.show();
    }
}
