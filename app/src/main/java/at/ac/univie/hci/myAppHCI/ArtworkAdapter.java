package at.ac.univie.hci.myAppHCI;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ListFormatter> {

    private final List<Artwork> artworkList;

    private final RecyclerViewInterface recyclerViewInterface;


    public ArtworkAdapter(List<Artwork> artworks, RecyclerViewInterface recyclerViewInterface)
    {
        artworkList = artworks;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ListFormatter extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView authorView;
        public TextView dateView;
        public TextView originView;

        public ListFormatter(View itemView, RecyclerViewInterface recyclerViewInterface)
        {
            //get All the Fields
            super(itemView);
            imageView = itemView.findViewById(R.id.imageArtwork);
            titleView = itemView.findViewById(R.id.textTitle);
            authorView = itemView.findViewById(R.id.textAuthor);
            dateView = itemView.findViewById(R.id.textDate);
            originView = itemView.findViewById(R.id.textOrigin);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null)
                {
                    int pos = getAdapterPosition();

                    if (pos != -1)
                    {
                        recyclerViewInterface.ArtworkExtend(pos);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ListFormatter onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artwork_list_layout, parent, false);
        return new ListFormatter(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(ListFormatter holder, int position)
    {
        Artwork artwork = artworkList.get(position);
        holder.titleView.setText(artwork.getTitle());
        holder.authorView.setText(artwork.getAuthor());
        holder.dateView.setText(artwork.getDate());
        holder.originView.setText(artwork.getOrigin());

        //Handle No Results
        if (!artwork.getImageUrl().isEmpty())
            Picasso.get().load(artwork.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return artworkList.size();
    }
}

