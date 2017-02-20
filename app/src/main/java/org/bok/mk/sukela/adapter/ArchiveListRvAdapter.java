package org.bok.mk.sukela.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.helper.Contract;

import java.util.List;

/**
 * Created by mk on 05.09.2016.
 */
public class ArchiveListRvAdapter extends RecyclerView.Adapter<ArchiveListRvAdapter.ArchiveRowHolder>
{
    private List<String> mSavedDates;
    private String mTag;

    public ArchiveListRvAdapter(List<String> dates, String tag)
    {
        this.mSavedDates = dates;
        this.mTag = tag;
    }

    @Override
    public ArchiveRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.archive_row, viewGroup, false);
        return new ArchiveRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ArchiveRowHolder holder, int position)
    {
        String date = mSavedDates.get(position);
        TextView row = holder.archive_row;
        row.setText(date);
        int row_color;
        if (mTag.equals(Contract.TAG_ARCHIVE_DAY))
        {
            row_color = row.getContext().getResources().getColor(R.color.pink_500);
        }
        else if (mTag.equals(Contract.TAG_ARCHIVE_WEEK))
        {
            row_color = row.getContext().getResources().getColor(R.color.blue_500);
        }
        else
        {
            throw new RuntimeException("Hatali tag: " + mTag);
        }

        row.setBackgroundColor(row_color);
    }

    @Override
    public int getItemCount()
    {
        return mSavedDates.size();
    }

    static class ArchiveRowHolder extends RecyclerView.ViewHolder
    {
        TextView archive_row;

        ArchiveRowHolder(View v)
        {
            super(v);
            archive_row = (TextView) v.findViewById(R.id.archive_row);
        }


    }
}
