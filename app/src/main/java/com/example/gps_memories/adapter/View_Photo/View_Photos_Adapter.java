package com.example.gps_memories.adapter.View_Photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_memories.R;

import java.util.List;

public class View_Photos_Adapter extends RecyclerView.Adapter<View_Photos_Adapter.Viewholder> {



    private Context context;

    private List<String> photos;

    public View_Photos_Adapter(List<String> photos, Context context) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {


        String photos_String = photos.get(position);

        Glide.with(holder.itemView).load(photos_String).into(holder.itemImage);

        //Toast.makeText(context, position+"", Toast.LENGTH_LONG).show();

        // Toast.makeText(context, photoslist.get(0), Toast.LENGTH_LONG).show();


        holder.deletebtn.setVisibility(View.GONE);


        holder.photonumTextView.setText(" "+(position+1)+" / "+photos.size()+" ");


    }


    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }



    public class Viewholder extends RecyclerView.ViewHolder {


        protected ProgressBar progressBar;
        protected ImageView itemImage;
        protected ImageView deletebtn;
        protected TextView photonumTextView;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            deletebtn = (ImageView) itemView.findViewById(R.id.deletebtn);
            photonumTextView = (TextView) itemView.findViewById(R.id.photonumTextView);

        }

    }
}