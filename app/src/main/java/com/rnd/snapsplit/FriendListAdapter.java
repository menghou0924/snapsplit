package com.rnd.snapsplit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 29/5/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend>{
    private Context context;
    private ArrayList<Friend> data;
    private int layoutResourceId;

    public FriendListAdapter(Context context, int layoutResourceId, ArrayList<Friend> data)  {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.textView1 = (TextView)row.findViewById(R.id.firstName);
            holder.textView2 = (TextView)row.findViewById(R.id.lastName);
            holder.textView3 = (TextView)row.findViewById(R.id.phoneNumber);
            holder.imageView1 = (ImageView)row.findViewById(R.id.friendIcon);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Friend friend = data.get(position);

        holder.textView1.setText(friend.getFirstName());
        holder.textView2.setText(friend.getLastName());
        holder.textView3.setText(friend.getPhoneNumber());
        holder.imageView1.setImageResource(friend.getDisplayPic());

        return row;
    }

    static class ViewHolder
    {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        ImageView imageView1;
    }




    }
