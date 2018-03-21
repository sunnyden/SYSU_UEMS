/*
 *  Copyright (C) 2013 - 2018, Haoqing Deng <dhq.sunny@gmail.com>
 *
 *  This file is part of the SYSU UEMS.
 *
 *  SYSU UEMS is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SYSU UEMS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SYSU UEMS; see the file COPYING. If not, see
 *  <http://www.gnu.org/licenses/>.
 */

package com.denghaoqing.sysu.Memo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denghaoqing.sysu.Dashboard.MemoViewHolder;
import com.denghaoqing.sysu.R;

import java.util.ArrayList;

/**
 * Created by sunny on 18-3-12.
 */

public class MemoDefaultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Memo> memoArrayList;
    private Context context;

    public MemoDefaultAdapter(ArrayList<Memo> memos, Context context) {
        this.memoArrayList = memos;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MemoViewHolder) holder).setMemo(memoArrayList.get(position));
    }

    public void removeItem(int position) {
        memoArrayList.remove(position);
    }

    public void restoreItem(int position, Memo memo) {
        memoArrayList.add(position, memo);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return memoArrayList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo_card, parent, false);
        return new MemoViewHolder(view, parent.getContext());
    }

}
