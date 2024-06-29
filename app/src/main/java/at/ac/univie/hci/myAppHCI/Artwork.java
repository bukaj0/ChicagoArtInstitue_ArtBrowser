package at.ac.univie.hci.myAppHCI;

import android.util.Log;

public class Artwork {

    private final String apiLink;
    private final String imageUrl;
    private final String title;

    private final String date;
    private final String author;
    private final String origin;

    private final String medium;

    private final String dimensions;

    private final String description;

    public Artwork(String imageUrl, String title, String date, String author, String origin, String apiLink, String medium, String dimensions, String description) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.date = date;
        this.author = author;
        this.origin = origin;
        this.apiLink = apiLink;

        this.medium = medium;
        this.dimensions = dimensions;
        this.description = description;
    }

    // Getters and setters
    public String getImageUrl()
    {
        Log.d("imageURL", imageUrl);
        return imageUrl;
    }

    public String getTitle()
    {
        return title;
    }



    public String getAuthor()
    {
        return author;
    }



    public String getOrigin()
    {
        return origin;
    }



    public String getDate()
    {
        return date;
    }


    public String getApiLink() {
        return apiLink;
    }



    public String getDimensions() {
        return dimensions;
    }



    public String getDescription() {
        return description;
    }



    public String getMedium() {
        return medium;
    }



}


