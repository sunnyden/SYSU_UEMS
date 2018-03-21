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

package com.denghaoqing.sysu.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.Memo.Memo;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.ViewMemoActivity;
import com.squareup.picasso.Picasso;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.File;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;

public class MemoViewHolder extends RecyclerView.ViewHolder {
    private Memo memo;
    private TextView title, content, time, reference;
    private ImageView image;
    private RelativeLayout refLayout;
    private CardView cardView;
    private Context context;

    public MemoViewHolder(View view, Context context) {
        super(view);
        title = view.findViewById(R.id.memo_preview_title);
        content = view.findViewById(R.id.memo_preview_content);
        time = view.findViewById(R.id.memo_preview_add_time);
        reference = view.findViewById(R.id.memo_preview_ref_course);
        image = view.findViewById(R.id.memo_preview_img);
        refLayout = view.findViewById(R.id.layout_preview_memo_refCourse);
        cardView = view.findViewById(R.id.item_card_memo);
        refLayout.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        this.context = context;
    }

    public Memo getMemo() {
        return memo;
    }

    public void setMemo(final Memo memo) {
        this.memo = memo;


        final Parser parser = Markwon.createParser();
        final SpannableConfiguration configuration = SpannableConfiguration.create(context);
        final SpannableRenderer renderer = new SpannableRenderer();
        final Node node = parser.parse(memo.getContent());
        String strContent = renderer.render(configuration, node).toString();
        strContent = strContent.replace("\n", " ").replace(" ", "");


        if (memo.hasImage) {
            image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(FileProvider.getUriForFile(context,
                    "com.denghaoqing.sysu.fileprovider", new File(memo.getImagePath())))
                    .fit()
                    .into(image);
        }
        title.setText(memo.getTitle());
        content.setText(strContent);
        time.setText(memo.getTime());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, ViewMemoActivity.class);
                mIntent.putExtra("memoId", memo.getId());
                context.startActivity(mIntent);
            }
        });
    }

}
