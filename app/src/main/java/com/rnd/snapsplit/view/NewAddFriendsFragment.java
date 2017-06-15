/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rnd.snapsplit.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.rnd.snapsplit.BuildConfig;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.R;
import com.rnd.snapsplit.util.ImageLoader;
import com.rnd.snapsplit.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This fragment displays a list of contacts stored in the Contacts Provider. Each item in the list
 * shows the contact's thumbnail photo and display name. On devices with large screens, this
 * fragment's UI appears as part of a two-pane layout, along with the UI of
 * {link ContactDetailFragment}. On smaller screens, this fragment's UI appears as a single pane.
 *
 * This Fragment retrieves contacts based on a search string. If the user doesn't enter a search
 * string, then the list contains all the contacts in the Contacts Provider. If the user enters a
 * search string, then the list contains only those contacts whose data matches the string. The
 * Contacts Provider itself controls the matching algorithm, which is a "substring" search: if the
 * search string is a substring of any of the contacts data, then there is a match.
 *
 * On newer API platforms, the search is implemented in a SearchView in the ActionBar; as the user
 * types the search string, the list automatically refreshes to display results ("type to filter").
 * On older platforms, the user must enter the full string and trigger the search. In response, the
 * trigger starts a new Activity which loads a fresh instance of this fragment. The resulting UI
 * displays the filtered list and disables the search feature to prevent furthering searching.
 */
public class NewAddFriendsFragment extends Fragment {

    // Defines a tag for identifying log entries
    private static final String TAG = "NewAddFriendsFragment";

    private RecyclerView recyclerView;
    private static final Random RANDOM = new Random();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ArrayList<Friend> desuNoto = new ArrayList<>();

//    for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
//            String key = (String) iterator.next();
//            System.out.println(jsonObject.get(key));
//        }
//            {
//            "Alane Avey", "Belen Brewster", "Brandon Brochu", "Carli Carrol", "Della Delrio",
//            "Esther Echavarria", "Etha Edinger", "Felipe Flecha", "Ilse Island", "Kecia Keltz",
//            "Lourie Lucas", "Lucille Leachman", "Mandi Mcqueeney", "Murray Matchett", "Nadia Nero",
//            "Nannie Nipp", "Ozella Otis", "Pauletta Poehler", "Roderick Rippy", "Sherril Sager",
//            "Taneka Tenorio", "Treena Trentham", "Ulrike Uhlman", "Virgina Viau", "Willis Wysocki"
//    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.view_add_friend_new, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        desuNoto = (new Friend(getContext())).getFriendsList();
//        Cursor cur = getContacts();
//
//        ListView lv = getListView();
//
//        String[] fields = new String[] {ContactsContract.Data.DISPLAY_NAME};
//
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                this,
//                R.layout.view_add_friend_new,
//                cur,
//                fields,
//                new int[] {R.id.txt_add_friend_name},
//                "FLAG_AUTO_REQUERY");
//        lv.setAdapter(adapter);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView();
//        setContactsAdapter(desuNoto);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(getContext(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
//
//    private Cursor getContacts() {
//        // Run query
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//
//        String[] projection =
//                new String[]{ ContactsContract.Contacts._ID,
//                        ContactsContract.Contacts.DISPLAY_NAME };
//        String selection = null;
//        String[] selectionArgs = null;
//        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
//                " COLLATE LOCALIZED ASC";
//        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
//    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        setContactsAdapter(desuNoto);
    }

    private void setContactsAdapter(ArrayList<Friend> arrayList) {
        recyclerView.setAdapter(
                new SimpleStringRecyclerViewAdapter(getContext(), arrayList));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Friend> mValues;
        private int[] mMaterialColors;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final MaterialLetterIcon mIcon;
            public final TextView mTextViewName;
            public final TextView mTextViewPhoneNumber;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIcon = (MaterialLetterIcon) view.findViewById(R.id.icon);
                mTextViewName = (TextView) view.findViewById(R.id.name);
                mTextViewPhoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
            }

            @Override public String toString() {
                return super.toString() + " '" + mTextViewName.getText();
            }
        }

        public String getValueAt(int position, String key) {
            return mValues.get(position).getValueWithKey(key);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Friend> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mMaterialColors = context.getResources().getIntArray(R.array.colors);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_add_friend_new, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mIcon.setInitials(true);
            holder.mIcon.setInitialsNumber(2);
            holder.mIcon.setLetterSize(18);
            holder.mBoundString = getValueAt(position, "name");
            holder.mIcon.setShapeColor(mMaterialColors[RANDOM.nextInt(mMaterialColors.length)]);
            holder.mTextViewName.setText(getValueAt(position, "name"));
            holder.mTextViewPhoneNumber.setText(getValueAt(position, "phoneNumber"));
            holder.mIcon.setLetter(getValueAt(position, "name"));
        }

        @Override public int getItemCount() {
            return mValues.size();
        }

    }
}
