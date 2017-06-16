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
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.rnd.snapsplit.Friend;
import com.rnd.snapsplit.FriendListAdapter;
import com.rnd.snapsplit.R;

import java.util.ArrayList;
import java.util.List;
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
public class AddFriendsFragment extends ListFragment {

    // Defines a tag for identifying log entries
    private static final String TAG = "AddFriendsFragment";

    private static final Random RANDOM = new Random();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ArrayList<Friend> friendsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.view_add_friend, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        friendsList = Friend.getFriendsListFromFile(getContext());

        this.setListAdapter(new FriendListAdapter(getContext(), R.layout.list_add_friend, friendsList, TAG));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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



}
