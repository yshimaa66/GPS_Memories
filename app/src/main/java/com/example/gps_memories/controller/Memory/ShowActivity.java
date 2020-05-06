package com.example.gps_memories.controller.Memory;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_memories.R;
import com.example.gps_memories.adapter.Memory_Adapter;
import com.example.gps_memories.model.Memory_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowActivity extends AppCompatActivity {

    protected ProgressBar progressBar;
    protected RecyclerView recyclerviewMemories;

    Memory_Adapter memory_adapter;

    List<Memory_Model> memory_models;


    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_show);
        initView();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        recyclerviewMemories.setHasFixedSize(true);
        recyclerviewMemories.setLayoutManager(new LinearLayoutManager(ShowActivity.this));



        memory_models = new ArrayList<>();

        read_memories();

    }



    private void read_memories() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Memories");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //post_models.clear();

                Memory_Model memory_model = dataSnapshot.getValue(Memory_Model.class);

                if(memory_model.getUserid().equals(firebaseUser.getUid())) {

                    memory_models.add(memory_model);
                }



                sort_memories(memory_models);



                memory_adapter = new Memory_Adapter(memory_models, ShowActivity.this);
                recyclerviewMemories.setAdapter(memory_adapter);

                progressBar.setVisibility(View.GONE);




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Memory_Model memory_model = dataSnapshot.getValue(Memory_Model.class);



                for (int i=0;i<memory_models.size();i++){

                    if(memory_models.get(i).getId().equals(memory_model.getId())){





                        memory_models.set(i,memory_model);

                        memory_adapter.notifyItemChanged(i);


                        break;


                    }



                }






            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                Memory_Model memory_model = dataSnapshot.getValue(Memory_Model.class);



                for (int i=0;i<memory_models.size();i++){

                    if(memory_models.get(i).getId().equals(memory_model.getId())){

                        memory_models.remove(i);

                        memory_adapter.notifyItemRemoved(i);


                        break;


                    }



                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });







    }


    private void sort_memories(List<Memory_Model> memory_models) {


        for(int i=0;i<memory_models.size();i++){

            for(int j=0;j<memory_models.size();j++){

                DateFormat format = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.ENGLISH);

                Date datei = null;
                Date datej = null;
                try {
                    datei = format.parse(memory_models.get(i).getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    datej = format.parse(memory_models.get(j).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(datei.compareTo(datej) > 0) {
                    //System.out.println("Date 1 occurs after Date 2");

                    Collections.swap(memory_models, j, i);

                }


            }




        }




    }


    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerviewMemories = (RecyclerView) findViewById(R.id.recyclerview_memories);
    }
}
