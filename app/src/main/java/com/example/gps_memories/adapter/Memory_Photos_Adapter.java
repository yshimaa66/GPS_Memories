package com.example.gps_memories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_memories.R;


import java.util.List;

import static com.example.gps_memories.controller.Memory.AddActivity.memoryphotoslist;



public class Memory_Photos_Adapter extends RecyclerView.Adapter<Memory_Photos_Adapter.Viewholder> {


    private Context context;

    private List<String> photos;

    public Memory_Photos_Adapter(List<String> photos, Context context) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photomini_item, parent, false);
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {


        String photos_String = photos.get(position);

        Glide.with(holder.itemView).load(photos_String).into(holder.itemImage);

            holder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    memoryphotoslist.remove(memoryphotoslist.get(position));

                    if (memoryphotoslist.size() > 0) {


                        holder.itemImage.setVisibility(View.VISIBLE);

                        String photos_String = photos.get(0);

                        Glide.with(holder.itemView).load(photos_String).into(holder.itemImage);


                    } else {

                        holder.itemImage.setVisibility(View.GONE);

                    }

                    Toast.makeText(context, "You removed a photo", Toast.LENGTH_LONG).show();


                }
            });

        }



    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }




    public class Viewholder extends RecyclerView.ViewHolder {


        protected ProgressBar progressBar;
        protected ImageView itemImage;
        protected ImageView deletebtn;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            deletebtn = (ImageView) itemView.findViewById(R.id.deletebtn);

        }
    }
}
