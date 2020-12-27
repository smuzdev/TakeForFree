package com.smuzdev.takeforfree;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smuzdev.takeforfree.fragments.DetailsFragment;
import com.smuzdev.takeforfree.models.Thing;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ThingViewHolder> {
    FragmentManager fragmentManager;
    private Context mContext;
    private List<Thing> thingList;
    private int lastPosition = -1;

    public Adapter(Context mContext, List<Thing> thingList) {
        this.mContext = mContext;
        this.thingList = thingList;
    }

    @Override
    public ThingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row_item, viewGroup, false);

        return new ThingViewHolder(mView);


    }

    @Override
    public void onBindViewHolder(@NonNull final ThingViewHolder thingViewHolder, final int position) {

        Glide.with(mContext)
                .load(thingList.get(position).getThingImage())
                .into(thingViewHolder.imageView);

        thingViewHolder.mThingTitle.setText(thingList.get(position).getThingName());
        thingViewHolder.mhThingDescription.setText(thingList.get(position).getThingDescription());
        thingViewHolder.mThingUseDuration.setText(thingList.get(position).getThingUseDuration());

        thingViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString("ThingName", thingList.get(thingViewHolder.getAdapterPosition()).getThingName());
                bundle.putString("ThingDescription", thingList.get(thingViewHolder.getAdapterPosition()).getThingDescription());
                bundle.putString("ThingPublicationDate", thingList.get(thingViewHolder.getAdapterPosition()).getThingPublicationDate());
                bundle.putString("ThingImage", thingList.get(thingViewHolder.getAdapterPosition()).getThingImage());
                bundle.putString("ThingUseDuration", thingList.get(thingViewHolder.getAdapterPosition()).getThingUseDuration());
                bundle.putString("ThingPickupPoint", thingList.get(thingViewHolder.getAdapterPosition()).getThingPickupPoint());
                bundle.putString("KeyValue", thingList.get(thingViewHolder.getAdapterPosition()).getKey());
                bundle.putString("UserName", thingList.get(thingViewHolder.getAdapterPosition()).getUserName());
                bundle.putString("UserEmail", thingList.get(thingViewHolder.getAdapterPosition()).getUserEmail());
                bundle.putString("UserPhone", thingList.get(thingViewHolder.getAdapterPosition()).getUserPhone());
                Log.d("tag_debug_lab", thingList.get(thingViewHolder.getAdapterPosition()).getKey());

                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(bundle);

                fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.mainFragment, detailsFragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        setAnimation(thingViewHolder.itemView, position);

    }


    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {

            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;

        }

    }

    @Override
    public int getItemCount() {
        return thingList.size();
    }

    public void filteredList(ArrayList<Thing> filterList) {

        thingList = filterList;
        notifyDataSetChanged();
    }
}

class ThingViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    ImageView imageView;
    TextView mThingTitle, mhThingDescription, mThingUseDuration;
    CardView mCardView;

    public ThingViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.ivThingImage);
        mThingTitle = itemView.findViewById(R.id.tvThingName);
        mhThingDescription = itemView.findViewById(R.id.tvThingDescription);
        mThingUseDuration = itemView.findViewById(R.id.tvThingUseDuration);
        mCardView = itemView.findViewById(R.id.myCardView);
        mCardView.setOnCreateContextMenuListener(this);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select option");
        menu.add(this.getAdapterPosition(), 1, 0, "Edit");
        menu.add(this.getAdapterPosition(), 2, 1, "Delete");
    }
}
