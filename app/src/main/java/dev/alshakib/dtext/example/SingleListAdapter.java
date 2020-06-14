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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import dev.alshakib.dtext.DText;

class SingleListAdapter extends ListAdapter<String, SingleListAdapter.SingleListViewHolder> {

    protected SingleListAdapter() {
        super(new StringDiffCallback());
    }

    @NonNull
    @Override
    public SingleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_single_list, parent, false);
        return new SingleListAdapter.SingleListViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleListViewHolder holder, int position) {
        String text = getItem(position);
        holder.displayTitle.setText(text);
        DText.Builder builder = new DText.Builder();
        builder.setDefaults();
        builder.drawAsCircle();
        builder.setText(text);
        builder.bold();
        builder.randomBackgroundColor();
        builder.firstCharOnly();
        holder.displayIcon.setImageDrawable(builder.build());
    }

    public static class SingleListViewHolder extends RecyclerView.ViewHolder {
        private ImageView displayIcon;
        private TextView displayTitle;

        SingleListViewHolder(@NonNull View itemView) {
            super(itemView);
            displayIcon = itemView.findViewById(R.id.display_icon);
            displayTitle = itemView.findViewById(R.id.display_title);
        }
    }

    private static class StringDiffCallback extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }
}
