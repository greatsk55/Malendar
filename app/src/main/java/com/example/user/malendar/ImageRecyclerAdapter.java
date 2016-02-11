package com.example.user.malendar;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 16. 2. 11.
 */
public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {

    private Context mContext;
    public ArrayList<ImageList> list;
    private LinearLayoutManager linearLayoutManager;

    public ArrayList<ImageList> getContactsList() {
        return list;
    }

    public ImageRecyclerAdapter(Context context, ArrayList<ImageList> _dataSet, LinearLayoutManager linearLayoutManager) {
        mContext = context;
        list = _dataSet;
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.content_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Picasso.with(mContext).load(sAlbumArtUri).placeholder(R.drawable.ic_no_album_sm).error(R.drawable.ic_no_album_sm).into(holder.albumArt);
        Picasso.with(mContext).load(new File(list.get(position).path)).into(holder.album);
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void add(ImageList song, int position) {
        list.add(position, song);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView album;

        public ViewHolder(View itemView) {
            super(itemView);
            album = (ImageView) itemView.findViewById(R.id.album_art1);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // song is selected
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            //TODO 전화걸기
        }

    }
}