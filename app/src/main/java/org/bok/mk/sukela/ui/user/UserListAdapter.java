package org.bok.mk.sukela.ui.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bok.mk.sukela.R;
import org.bok.mk.sukela.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {
    private final List<User> mUserList = new ArrayList<>();
    private final UserRowClickListener mListener;

    public UserListAdapter(UserRowClickListener listener) {
        mListener = listener;
    }

    public void setData(List<User> list) {
        mUserList.clear();
        mUserList.addAll(list);
    }

    public void add(User u, int index) {
        mUserList.add(index, u);
        this.notifyItemInserted(index);
    }

    public void remove(int index) {
        mUserList.remove(index);
        notifyItemRemoved(index);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rect_row, viewGroup, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, int position) {
        holder.letter.setText(mUserList.get(position).getUserName().substring(0,1).toUpperCase());
        holder.name.setText(mUserList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView letter; // soldaki kutu, ilk harf
        final TextView name; // sağdaki kutu, yazar adı

        UserHolder(final View rowView) {
            super(rowView);

            rowView.setOnClickListener(this);
            rowView.setLongClickable(true);
            rowView.setOnLongClickListener(this);

            letter = rowView.findViewById(R.id.tvFirstLetter);
            name = rowView.findViewById(R.id.tvUserName);
        }

        @Override
        public void onClick(View view) {
            mListener.rowClicked(mUserList.get(getAdapterPosition()).getUserName());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.rowLongClicked(mUserList.get(getAdapterPosition()).getUserName());
            return true;
        }
    }

    public interface UserRowClickListener
    {
        void rowClicked(final String username);

        void rowLongClicked(final String username);
    }
}
