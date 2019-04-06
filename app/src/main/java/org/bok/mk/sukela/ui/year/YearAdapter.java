package org.bok.mk.sukela.ui.year;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bok.mk.sukela.R;

import java.util.Arrays;
import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder>
{
    private final OnItemClickListener mClickListener;
    private final Context mContext;

    private static final List<String> mDataset = Arrays.asList("2016", "2015", "2014", "2013",
            "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004");

    private static final List<Integer> mColors = Arrays.asList(R.color.a2016, R.color.a2015,
            R.color.a2014, R.color.a2013, R.color.a2012, R.color.a2011, R.color.a2010,
            R.color.a2009, R.color.a2008, R.color.a2007, R.color.a2006, R.color.a2005,
            R.color.a2004);

    private static final List<Integer> mIcons = Arrays.asList(R.drawable.a2016, R.drawable.a2015,
            R.drawable.a2014, R.drawable.a2013, R.drawable.a2012, R.drawable.a2011,
            R.drawable.a2010, R.drawable.a2009, R.drawable.a2008, R.drawable.a2007,
            R.drawable.a2006, R.drawable.a2005, R.drawable.a2004);

    public YearAdapter(OnItemClickListener listener) {
        mClickListener = listener;
        mContext = (Context) listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.year_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvYear.setText(mDataset.get(position));
        holder.ivIcon.setImageResource(mIcons.get(position));
        holder.cBackground.setBackgroundColor(
                mContext.getResources().getColor(mColors.get(position)));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView tvYear;
        final ImageView ivIcon;
        final ConstraintLayout cBackground;

        ViewHolder(View v) {
            super(v);

            ivIcon = v.findViewById(R.id.ivZodiac);
            cBackground = v.findViewById(R.id.background);
            tvYear = v.findViewById(R.id.tvYear);
            tvYear.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(mDataset.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(String year);
    }
}
