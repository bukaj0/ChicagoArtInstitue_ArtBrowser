package at.ac.univie.hci.myAppHCI;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilterBottomSheet extends BottomSheetDialogFragment
{

    private String filtertype;

    //Save Input so pop up is persistent. Hashmap is here so different buttons have their own string in the search view
    static Map<String, String> queries = new HashMap<>();
    public static FilterBottomSheet newInstance(String filtertype)
    {
        FilterBottomSheet fragment = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putString("filter_type", filtertype);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.filter_layout, container, false);
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setFocusable(false);

        if (getArguments() != null) {
            filtertype = getArguments().getString("filter_type");
        }

        if(Objects.equals(filtertype, "artist"))
        {
            TextView textView = view.findViewById(R.id.textview);
            textView.setText("SET ARTIST FILTER");
        }

        else
        {
            TextView textView = view.findViewById(R.id.textview);
            textView.setText("SET ORIGIN FILTER");
        }


        //worst debugging session ever... String query = "" will OBVIOUSLY make the search view empty upon open
        String query = queries.getOrDefault(filtertype, "");
        searchView.setQuery(query, false);



        //basic ass search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextSubmit(String query) {
                queries.put(filtertype, query);
                HideKeyboard();
                dismiss();

                return true;
            }
            public boolean onQueryTextChange(String query) {
                queries.put(filtertype, query);
                return true;
            }
        });

        return view;
    }

    public void onDismiss(@NonNull DialogInterface dialogInterface)
    {
        super.onDismiss(dialogInterface);

        Activity activity = getActivity();
        if (activity instanceof ResultsActivity)
        {
            String currentQuery = ((ResultsActivity) activity).getCurrentQuery();
            ((ResultsActivity) activity).submitQuery(currentQuery);
            ((ResultsActivity) activity).HideKeyboard();

        }

    }

    //function to close the keyboard on submit because of course this is not default
    private void HideKeyboard()
    {
        //need get Context because fragments work differently apparently
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        //Documentation sucks for this
        if (inputMethodManager != null && getView() != null && getView().getWindowToken() != null)
        {
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
}
