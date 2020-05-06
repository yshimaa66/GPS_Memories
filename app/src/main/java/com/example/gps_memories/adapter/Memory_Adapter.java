package com.example.gps_memories.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_memories.R;
import com.example.gps_memories.controller.Memory.EditActivity;
import com.example.gps_memories.controller.Memory.View_MemoryActivity;
import com.example.gps_memories.model.Memory_Model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Memory_Adapter extends RecyclerView.Adapter<Memory_Adapter.Viewholder> {



    private Context context;

    private List<Memory_Model> memory_models;


    public Memory_Adapter(List<Memory_Model> memory_models, Context context) {
        this.context = context;
        this.memory_models = memory_models;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.memory_item, parent, false);
        return new Viewholder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


        final Memory_Model memory_model = memory_models.get(position);


        Glide.with(holder.itemView).load(memory_model.getPhotos().get(0)).into(holder.memoryItemPhoto);

        holder.titleTextView.setText(memory_model.getTitle());

        holder.timeTextView.setText(memory_model.getTime());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, View_MemoryActivity.class);


                intent.putExtra("memory_id", memory_model.getId());


                context.startActivity(intent);

                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });

        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, EditActivity.class);


                intent.putExtra("memory_id", memory_model.getId());


                context.startActivity(intent);

                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });


        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(context)
                        .setTitle("Delete Memory")
                        .setMessage("Are you sure you want to delete this memory?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final DatabaseReference referencee = FirebaseDatabase.getInstance().getReference("Memories")
                                        .child(memory_model.getId());

                                referencee.removeValue();


                                Toast.makeText(context, "Memory deleted successfully", Toast.LENGTH_LONG).show();






                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }

        });


    }

    @Override
    public int getItemCount() {
        return memory_models == null ? 0 : memory_models.size();
    }




    public class Viewholder extends RecyclerView.ViewHolder {


        protected ImageView deletebtn;
        protected ImageView editbtn;

        protected ImageView memoryItemPhoto;
        protected TextView titleTextView;

        protected TextView timeTextView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            deletebtn = (ImageView) itemView.findViewById(R.id.deletebtn);
            editbtn = (ImageView) itemView.findViewById(R.id.editbtn);
            memoryItemPhoto = (ImageView) itemView.findViewById(R.id.memory_item_photo);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);

            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        }
    }


}
