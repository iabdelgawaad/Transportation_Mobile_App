package com.FBLoginSample.activity;

/**
 * Created by rokia on 5/16/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.FBLoginSample.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment implements Communicator{

    MyAdapter mAdapter;
    RecyclerView recyclerView;
    //
    public static ItemData itemsData[] = new ItemData[]{};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);

        return v;
    }

    public Tab1()
    {

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Toast.makeText(getActivity(),"Tab1",Toast.LENGTH_SHORT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this));
        // this is data fro recycler view
//        Bundle extras = getActivity().getIntent().getExtras();
//        String stationarray = extras.getString("stations");
//
//        Log.d("statiiions:",stationarray);




        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. create an adapter
        mAdapter = new MyAdapter(Tab1.itemsData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void setDataFromHomeFragment(String[] station,String[] stationType)
    {
        itemsData = new ItemData[station.length];
        for (int i = 0 ; i < station.length ; i++ )
        {
            itemsData[i] =  new ItemData(station[i]);
        }
        mAdapter = new MyAdapter(itemsData);

        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void respond(String[] stations, String[] stationType) {

    }
}
