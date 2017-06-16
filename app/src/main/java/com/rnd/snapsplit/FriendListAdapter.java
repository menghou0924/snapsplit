package com.rnd.snapsplit;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Damian on 29/5/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend>{
    private Context context;
    private String fragmentTag;
    private ArrayList<Friend> data;
    private int layoutResourceId;
    private int[] mMaterialColors;
    private static final Random RANDOM = new Random();
    private final TypedValue mTypedValue = new TypedValue();

    public FriendListAdapter(Context context, int layoutResourceId, ArrayList<Friend> data, String tag)  {
        super(context, layoutResourceId, data);
        this.context = context;
        this.fragmentTag = tag;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
        this.mMaterialColors = context.getResources().getIntArray(R.array.colors);
//        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.mFirstName = (TextView)row.findViewById(R.id.firstName);
            holder.mLastName = (TextView)row.findViewById(R.id.lastName);
            holder.mPhoneNumber = (TextView)row.findViewById(R.id.phoneNumber);
            holder.mAmount = (TextView)row.findViewById(R.id.splitAmount);
            holder.mImageIcon = (ImageView)row.findViewById(R.id.image_icon);
            holder.mTextIcon = (MaterialLetterIcon) row.findViewById(R.id.text_icon);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Friend friend = data.get(position);

        holder.mFirstName.setText(friend.getFirstName());
        holder.mLastName.setText(friend.getLastName());
        holder.mPhoneNumber.setText(friend.getPhoneNumber());

        holder.mTextIcon.setInitials(true);
        holder.mTextIcon.setInitialsNumber(2);
        holder.mTextIcon.setLetterSize(18);
        holder.mTextIcon.setShapeColor(mMaterialColors[RANDOM.nextInt(mMaterialColors.length)]);
        holder.mTextIcon.setLetter((String) friend.getValueWithKey("name"));

        if (friend.getDisplayPic() == 0) {
            holder.mTextIcon.setVisibility(View.VISIBLE);
            holder.mImageIcon.setVisibility(View.INVISIBLE);
        }
        else {
            holder.mTextIcon.setVisibility(View.INVISIBLE);
            holder.mImageIcon.setVisibility(View.VISIBLE);
            holder.mImageIcon.setImageResource(friend.getDisplayPic());
        }

        if (!fragmentTag.equals("AddFriendsFragment")) {
            holder.mAmount.setText(String.format("HKD%.2f",friend.getAmountToPay()));
        }

        return row;
    }

    private static class ViewHolder
    {
        TextView mFirstName;
        TextView mLastName;
        TextView mPhoneNumber;
        TextView mAmount;
        ImageView mImageIcon;
        MaterialLetterIcon mTextIcon;

    }
}
