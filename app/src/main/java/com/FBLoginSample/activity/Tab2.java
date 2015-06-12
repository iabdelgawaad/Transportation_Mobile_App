package com.FBLoginSample.activity;

/**
 * Created by rokia on 5/16/2015.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.FBLoginSample.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends Fragment implements Communicator {

    MyAdapter mAdapter;
    RecyclerView recyclerView;
    ItemData itemsData[] = new ItemData[]{};


    @Override
    public void respond(String[] stations, String[] stationType) {
        ItemData[] station = new ItemData[stations.length];

        int j = 0;
        for (int i = 0 ; i < stations.length ; i++ )
        {
            Log.d("station", HomeFragment.stationsType[i]);

            if(HomeFragment.stationsType[i].equals("Metro")) {

                station[j]  = new ItemData(stations[i]);
                j++;
            }
        }

        ItemData[] myData = new ItemData[j];

        //
        for (int i = 0 ; i<j ; i++)
        {
            myData[i] = station[i];

        }

        mAdapter = new MyAdapter(myData);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void openMetroLineFragment(int stationId, Fragment fragment) {

    }

    @Override
    public void openBusLineFragment(int stationId, Fragment fragment) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);

        return v;
    }

    public Tab2()
    {

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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
    ItemData itemsData[] = new ItemData[Tab1.itemsData.length ];
        for (int i = 0 ; i < Tab1.itemsData.length ; i++ )
        {
            if(HomeFragment.stationsType[i]=="Metro") {
                itemsData[i] = new ItemData(station[i]);
            }
        }
        mAdapter = new MyAdapter(itemsData);

        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

}
