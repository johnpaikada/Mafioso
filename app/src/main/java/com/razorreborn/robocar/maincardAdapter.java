package com.razorreborn.robocar;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.razorreborn.robocar.MapsActivity;
import java.util.List;

/**
 *
 * Created by Kiran Anto aka RazorSharp on 1/26/2016.
 * For more Info Contact
 * Kirananto@gmail.com
 * 9495333724
 * All Copyrights Reserved 2016
 *
 */
public class maincardAdapter extends RecyclerView.Adapter<maincardAdapter.ViewHolder> {

    private final Context context;
    private final List<maincontent> HomeData;

    public maincardAdapter(List<maincontent> HomeData, Context context){
        super();
        //Getting all the notification
        this.HomeData = HomeData;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        maincontent maincontent =  HomeData.get(position);
        holder.Name.setText(maincontent.getName());
        holder.Address.setText(maincontent.getAddress());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ConnectActivity connect = new ConnectActivity();
                    connect.pairDevice(Global.device);
            }
        });
    }


    @Override
    public int getItemCount() {
        return HomeData.size();
    }

    public void clearData() {
        int size = this.HomeData.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.HomeData.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView Name;
        public final TextView Address;
        public final CardView cardView= (CardView) itemView.findViewById(R.id.card_view);
        //TODO cardview is taken to use it for onClick Events
        public ViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.text_title);
            Address = (TextView) itemView.findViewById(R.id.text_content);
        }

        @Override
        public void onClick(View view) {

        }




    }
}