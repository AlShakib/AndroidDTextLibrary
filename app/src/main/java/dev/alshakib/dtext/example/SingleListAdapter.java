/*
 * MIT License
 *
 * Copyright (c) 2020 Al Shakib (shakib@alshakib.dev)
 *
 * This file is part of Android DText Library
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.alshakib.dtext.example;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private Context context;

    protected SingleListAdapter(Context context) {
        super(new StringDiffCallback());
        this.context = context;
    }

    private Drawable createDrawable(String text) {
        DText.Builder builder = new DText.Builder();
        builder.setText(text);
        builder.drawAsRound();
        builder.useSpAndDp(context);
        builder.boldText();
        builder.randomBackgroundColor();
        builder.firstCharOnly();
        return builder.build();
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
        holder.displayIcon.setImageDrawable(createDrawable(text));
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
