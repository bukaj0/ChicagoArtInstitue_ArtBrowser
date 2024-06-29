package at.ac.univie.hci.myAppHCI;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.text.TextWatcher;

public class DateBottomSheet extends BottomSheetDialogFragment
{

    private SearchView searchView;

    //Save Input so pop up is persistant.
    static String year1;
    static String year2;

    static EditText editText1;
    static EditText editText2;
    public static DateBottomSheet newInstance() {
        DateBottomSheet fragment = new DateBottomSheet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //submit Search when dismissing Date
    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Activity activity = getActivity();
        if (activity instanceof ResultsActivity) {
            ResultsActivity resultsActivity = (ResultsActivity) activity;
            String currentQuery = resultsActivity.getCurrentQuery();
            resultsActivity.submitQuery(currentQuery);
            ((ResultsActivity) activity).HideKeyboard();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.filter_layout_date, container, false);
        editText1 = view.findViewById(R.id.editTextNumber);
        editText2 = view.findViewById(R.id.editTextNumber2);

        //worst debugging session ever... String query = "" will OBVIOUSLY make the search view empty upon open
        String input1 = year1;
        String input2 = year2;

        editText1.setText(input1);
        editText2.setText(input2);


        //basic ass edit Text listener
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                year1 = String.valueOf(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                year1 = String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

                year1 = String.valueOf(s);
            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                year2 = String.valueOf(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                year2 = String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                year2 = String.valueOf(s);
            }
        });

        return view;
    }
}
