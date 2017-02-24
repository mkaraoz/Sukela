package org.bok.mk.sukela.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.helper.CircularRowData;
import org.bok.mk.sukela.helper.callbacks.CircularRowClickCallback;

import java.util.List;

/**
 * Created by mk on 04.09.2016.
 * <p>
 * CircularRow nesnesi içinde gelen verileri satırlara basıyor.
 */
public class CircularRowRvAdapter extends RecyclerView.Adapter<CircularRowRvAdapter.UserHolder> {
    private List<CircularRowData> mRowData;
    private CircularRowClickCallback mCallback;

    public CircularRowRvAdapter(List<CircularRowData> rowData, CircularRowClickCallback callback) {
        mRowData = rowData;
        mCallback = callback;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.circular_row, viewGroup, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        final CircularRowData rowData = mRowData.get(position);
        holder.circle_letter.setText(rowData.CIRCLE_LETTER.toUpperCase());
        holder.box_text.setText(rowData.BOX_TEXT);

        GradientDrawable shape_circle = (GradientDrawable) holder.circle_letter.getBackground();
        GradientDrawable shape_box = (GradientDrawable) holder.box_text.getBackground();
        // Yanlış bir renk kodu gelirse mavi yapıyorum tüm rekleri
        int colorCircle, colorBox;
        try {
            colorCircle = Color.parseColor(rowData.CIRCLE_COLOR);
            colorBox = Color.parseColor(rowData.BOX_COLOR);
        } catch (IllegalArgumentException ex) {
            Log.e("_MK", "Yanlis renk kodu geldi. Maviye boyuyorum", ex);
            colorCircle = Color.parseColor("#FF4285F4");
            colorBox = Color.parseColor("#FF4285F4");
        }
        shape_circle.setColor(colorCircle);
        shape_box.setStroke(3, colorBox);

        holder.rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("_MK", rowData.BOX_TEXT);
                mCallback.rowClicked(rowData.TAG, rowData.BOX_TEXT);
            }
        });

        holder.rel.setLongClickable(true);
        holder.rel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("_MK", rowData.BOX_TEXT);
                mCallback.rowLongClicked(rowData.TAG, rowData.BOX_TEXT);
                return true;
            }
        });
    }

    public void removeItem(int index) {
        mRowData.remove(index);
        notifyItemRemoved(index);
        //notifyItemRangeChanged(index, mRowData.size());
    }

    @Override
    public int getItemCount() {
        return mRowData.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder {
        TextView circle_letter; // soldaki yuvarlak
        TextView box_text; // sağdaki text box
        RelativeLayout rel;// doppler affect için lazım

        UserHolder(final View rowView) {
            super(rowView);
            circle_letter = (TextView) rowView.findViewById(R.id.text_circle);
            box_text = (TextView) rowView.findViewById(R.id.text_box);
            rel = (RelativeLayout) rowView.findViewById(R.id.rel);
        }
    }
}
