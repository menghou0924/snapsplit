package com.rnd.snapsplit;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Damian on 29/5/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend> implements View.OnTouchListener{
    private Context context;
    private String fragmentTag;
    private ArrayList<Friend> data;
    private int layoutResourceId;
    private static final Random RANDOM = new Random();
    private final TypedValue mTypedValue = new TypedValue();

    public FriendListAdapter(Context context, int layoutResourceId, ArrayList<Friend> data, String tag)  {
        super(context, layoutResourceId, data);
        this.context = context;
        this.fragmentTag = tag;
        this.data = data;
        this.layoutResourceId = layoutResourceId;

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
            holder.mName = (TextView)row.findViewById(R.id.txt_name);
            holder.mPhoneNumber = (TextView)row.findViewById(R.id.txt_phone);
            holder.mAmount = (TextView)row.findViewById(R.id.splitAmount);
            holder.mImageIcon = (ImageView)row.findViewById(R.id.image_icon);
            holder.mTextIcon = (MaterialLetterIcon) row.findViewById(R.id.text_icon);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        row.setOnTouchListener((new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                EditText editText = (EditText) view.findViewById(R.id.splitAmount);
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                return false;
            }
        }));
        if (holder.mAmount != null){
            holder.mAmount.setOnTouchListener((new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    EditText editText = (EditText) view;
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    return false;
                }
            }));

        }

        Friend friend = data.get(position);

        holder.mName.setText(friend.getFirstName() + " " + friend.getLastName());
        holder.mPhoneNumber.setText(friend.getPhoneNumber());
        if (fragmentTag.equals("ConfirmationFragment")){
            holder.mAmount.setText(String.format("%.2f",friend.getAmountToPay())+"HKD");
        }

        holder.mTextIcon.setInitials(true);
        holder.mTextIcon.setInitialsNumber(2);
        holder.mTextIcon.setLetterSize(18);
        holder.mTextIcon.setShapeColor(friend.getDisplayColor());
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
            holder.mAmount.setShowSoftInputOnFocus(false);
            holder.mAmount.setText(String.format("HKD%.2f",friend.getAmountToPay()));
        }

        return row;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
        } else {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.mAmount.setFocusable(false);
            holder.mAmount.setFocusableInTouchMode(false);
        }
        return false;
    }

    private static class ViewHolder
    {
        TextView mName;
        TextView mPhoneNumber;
        TextView mAmount;
        ImageView mImageIcon;
        MaterialLetterIcon mTextIcon;

    }
}
