/*
 * Copyright (c) 2020 Al Shakib (shakib@alshakib.dev)
 *
 * This file is part of Android DText Library
 *
 * Android DText Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android DText Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android DText Library.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.alshakib.dtext.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SingleListAdapter singleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        singleListAdapter.submitList(fetchData());
    }

    private void setupRecyclerView() {
        RecyclerView singleListRecyclerView = findViewById(R.id.main_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        singleListRecyclerView.setLayoutManager(linearLayoutManager);
        singleListAdapter = new SingleListAdapter(getApplicationContext());
        singleListRecyclerView.setAdapter(singleListAdapter);
    }

    private List<String> fetchData() {
        String[] dataSet = getResources().getStringArray(R.array.countries_array);
        return Arrays.asList(dataSet);
    }
}