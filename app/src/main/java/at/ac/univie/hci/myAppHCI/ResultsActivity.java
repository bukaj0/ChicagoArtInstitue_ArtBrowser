package at.ac.univie.hci.myAppHCI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import org.json.JSONException;


public class ResultsActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewInterface{

    private ArtworkAdapter adapter;
    private List<Artwork> artworkList;
    private SearchView searchView;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        initRecyclerView();
        initSearchView();



        View rootView = findViewById(R.id.parent);
        View artworkList = findViewById(R.id.recyclerViewArtworks);

        //so the keyboard of the searchview closes when tapping somewhere else
        rootView.setOnClickListener(this);
        artworkList.setOnClickListener(this);
        HideKeyboard();
    }


    //propably redundant but ill just leave it in
    @Override
    public void onClick(View v) {
        HideKeyboard();
    }

    //yet another function to prevent keyboard automatically showing up urgh
    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewArtworks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artworkList = new ArrayList<>();
        adapter = new ArtworkAdapter(artworkList, this);
        recyclerView.setAdapter(adapter);

        //dismiss keyboard when tapping on the recycler view
        recyclerView.setOnTouchListener((v, event) -> {
            HideKeyboard();
            return false; // Do not consume the event, allow other interactions
        });
    }


    //implement submitting query upon changing and dismissing filter popup
    public void submitQuery(String query)
    {
        if (query != null && !query.isEmpty())
        {
            searchView.setQuery(query,true);
        }
        searchView.clearFocus();
    }

    public String getCurrentQuery()
    {
        return searchView.getQuery().toString();
    }

    private void initSearchView() {
        searchView = findViewById(R.id.search_view);
        searchView.setFocusable(false);

        TextView textView = findViewById(R.id.activeFilters);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                HideKeyboard();

                //Upon triggering the search, extract the strings from each button search view
                String artist = FilterBottomSheet.queries.getOrDefault("artist", "");
                String origin = FilterBottomSheet.queries.getOrDefault("origin", "");
                String dateinput1 = (DateBottomSheet.year1 != null) ? DateBottomSheet.year1 : "";
                String dateinput2 = (DateBottomSheet.year2 != null) ? DateBottomSheet.year2 : "";


                assert artist != null;
                updateActiveFilters(artist,origin,dateinput1,dateinput2);



                try {
                    //build query link in SEARCH API
                    query = SearchAPI.createSearchQuery(query,origin, dateinput1,dateinput2,artist);
                    String fin = "https://api.artic.edu/api/v1/artworks/search?params=" + query + "&fields=api_link,title,artist_display,date_display,place_of_origin,image_id,id,date_start,description,dimensions,medium_display,date_end&limit=50";
                    fetchArtwork(fin);
                } catch (UnsupportedEncodingException | JSONException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }


    //Function to hide Keyboard on Submitting Query
    void HideKeyboard()
    {
        InputMethodManager man = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            man.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    // AsyncTask to fetch the artworks
    private void fetchArtwork(String url) {
        artworkList.clear();
        executorService.submit(() ->
        {
            List<Artwork> artworks = SearchAPI.getArtworksFromURL(url);
            Log.d("count", String.valueOf(artworks.size()));
            handler.post(() -> {
                if (!(artworks.isEmpty())) {
                    artworkList.addAll(artworks);
                    adapter.notifyDataSetChanged();

                }
                else
                {
                    Log.d("what","why is this called right now");
                    artworkList.add(new Artwork("", "No Results Found", "", "", "", "","","",""));
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }



    //Display active Filters and make it allll pretty
    private void updateActiveFilters(String artist, String origin, String dateStart, String dateEnd)
    {
        TextView textView = findViewById(R.id.activeFilters);
        SpannableString artistSpan = new SpannableString("");
        SpannableString originSpan = new SpannableString("");
        SpannableString dateSpan = new SpannableString("");
        if (artist.isEmpty() && origin.isEmpty() && dateStart.isEmpty() && dateEnd.isEmpty())
        {
            textView.setText("");
            return;
        }

        SpannableStringBuilder activeFilters = new SpannableStringBuilder("");

        if (!artist.isEmpty())
        {
            artist = artist.isEmpty() ? "None" : artist;
            String artistFilter = " | Artist: " + artist;
            artistSpan = new SpannableString(artistFilter);
            artistSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, artistFilter.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            artistSpan.setSpan(new StyleSpan(Typeface.BOLD), 3, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            artistSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#b50838")), 3, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!origin.isEmpty())
        {
            origin = origin.isEmpty() ? "None" : origin;
            String originFilter = " | Place of Origin: " + origin;
            originSpan = new SpannableString(originFilter);
            originSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, originFilter.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            originSpan.setSpan(new StyleSpan(Typeface.BOLD), 3, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            originSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#b50838")), 3, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!dateStart.isEmpty() || !dateEnd.isEmpty())
        {
            dateStart = dateStart.isEmpty() ? "None" : dateStart;
            dateEnd = dateEnd.isEmpty() ? "None" : dateEnd;
            String dateFilter = " | Date: " + dateStart + "-" + dateEnd + " | ";
            dateSpan = new SpannableString(dateFilter);
            dateSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, dateFilter.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dateSpan.setSpan(new StyleSpan(Typeface.BOLD), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dateSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#b50838")), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        activeFilters.append(artistSpan).append(originSpan).append(dateSpan);

        textView.setText(activeFilters);
    }



    //Clear Filters And Query Button
    public void onClickClear(View view)
    {
        FilterBottomSheet.queries.clear();

        searchView.setQuery("",false);

        if (DateBottomSheet.editText1 != null && DateBottomSheet.editText2 != null) {
            DateBottomSheet.editText1.setText("");
            DateBottomSheet.editText2.setText("");
        }
        updateActiveFilters("","","","");
        artworkList.clear();
        adapter.notifyDataSetChanged();
    }
    //On Click artwork more information
    @Override
    public void ArtworkExtend(int pos)
    {
        //open up new Activity and pass on artwork information
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("image_url", artworkList.get(pos).getImageUrl());
        intent.putExtra("title", artworkList.get(pos).getTitle());
        intent.putExtra("artist", artworkList.get(pos).getAuthor());
        intent.putExtra("date", artworkList.get(pos).getDate());
        intent.putExtra("origin", artworkList.get(pos).getOrigin());
        intent.putExtra("apilink", artworkList.get(pos).getApiLink());
        intent.putExtra("medium", artworkList.get(pos).getMedium());
        intent.putExtra("dimensions", artworkList.get(pos).getDimensions());
        intent.putExtra("description", artworkList.get(pos).getDescription());

        startActivity(intent);
    }

    // Origin and Artist Popup
    public void showArtistOriginFilter(View view)
    {
        String filterType = view.getTag().toString();
        Log.d("ass",filterType);
        //this opens up the individual pop up windows for each filter type
        FilterBottomSheet bottomSheet = FilterBottomSheet.newInstance(filterType);
        bottomSheet.show(getSupportFragmentManager(), filterType);
    }

    //Date popup
    public void showDateFilter(View view)
    {
        //this opens the date button filter
        DateBottomSheet datebottomSheet = DateBottomSheet.newInstance();
        datebottomSheet.show(getSupportFragmentManager(), "dateBottomSheetTag");
    }

}
