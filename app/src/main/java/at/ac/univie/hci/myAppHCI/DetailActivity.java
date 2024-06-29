package at.ac.univie.hci.myAppHCI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private String imageUrl;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get all the information about artworks
        imageUrl = getIntent().getStringExtra("image_url");
        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");
        String date = getIntent().getStringExtra("date");
        String origin = getIntent().getStringExtra("origin");
        String medium = getIntent().getStringExtra("medium");
        String dimensions = getIntent().getStringExtra("dimensions");
        String description = getIntent().getStringExtra("description");
        //Set image to 70percent of screen size for appearance
        setImage(findViewById(R.id.artwork));

        TextView textView = findViewById(R.id.title);
        textView.setText(title);

        textView = findViewById(R.id.artist);
        textView.setText(artist);

        textView = findViewById(R.id.date);
        textView.setText(date);

        textView = findViewById(R.id.origin);
        textView.setText(origin);

        textView = findViewById(R.id.medium);
        textView.setText(medium);

        textView = findViewById(R.id.dimensions);
        textView.setText(dimensions);

        textView = findViewById(R.id.description);
        assert description != null;
        if (!description.isEmpty() && !(description.equals("null")) ) {
            textView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            textView.setText("No Description Available");
        }
        //so one can open up links
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        ImageView imageView = findViewById(R.id.artwork);
        imageView.setOnClickListener(view -> Zoom());
    }

    private void setImage(ImageView imageView)
    {
        if (!imageUrl.isEmpty())
            Picasso.get().load(imageUrl).into(imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        ViewGroup.LayoutParams test = imageView.getLayoutParams();
        test.height = (int) (height * 0.7);
        imageView.setLayoutParams(test);
    }

    //Zoomable PopupPicture
    private void Zoom() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.zoom_layout);

        PhotoView photoView = dialog.findViewById(R.id.zoom_image);
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(photoView);
        }
        //dismiss on tap outside
        photoView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}