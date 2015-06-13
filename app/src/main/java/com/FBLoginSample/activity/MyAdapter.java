package com.FBLoginSample.activity;

/**
 * Created by sarah on 5/18/2015.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.FBLoginSample.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    static  ItemData[] itemsData;

    Context  context;
    public MyAdapter(ItemData[] itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null);
        context = parent.getContext();

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.txtViewTitle.setText(itemsData[position].getTitle());
        viewHolder.counter.setText(itemsData[position].getCounter());
        viewHolder.buslinenum.setText(itemsData[position].getBuslineNum());
        viewHolder.status.setText(itemsData[position].getitemstatus());
        viewHolder.imgViewIcon.setImageDrawable(itemsData[position].getimgdrwable());
//        viewHolder.imgViewIcon.setImageResource(itemsData[position].getImageUrl());


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView counter,buslinenum,status;

        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            counter= (TextView) itemLayoutView.findViewById(R.id.counter);
            buslinenum= (TextView) itemLayoutView.findViewById(R.id.buslinenumtxt);
            status= (TextView) itemLayoutView.findViewById(R.id.typetxt);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.img_fav);

//          imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);


            imgViewIcon.setOnClickListener(new View.OnClickListener() {


                boolean toggle=false;

                @Override
                public void onClick(View view) {


//                    itemsData[getPosition()].getItemposotion();
                    Toast.makeText(view.getContext(), ""+itemsData[getPosition()].getid()+itemsData[getPosition()].getTitle()+itemsData[getPosition()].getitemstatus(),Toast.LENGTH_SHORT).show();
                    Drawable new_image1= view.getResources().getDrawable(R.drawable.fav);
                    Drawable new_image2= view.getResources().getDrawable(R.drawable.favo);


                        if(toggle)
                        {
                            imgViewIcon.setImageDrawable(new_image1);
                            toggle=false;
                        }
                        else
                        {
                            imgViewIcon.setImageDrawable(new_image2);
                            toggle=true;
                        }




                }


            });
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}
