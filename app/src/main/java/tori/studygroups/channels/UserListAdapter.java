package tori.studygroups.channels;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.utils.ImageUtils;

/**
 * A simple adapter that displays a list of Users.
 * usato sia per veder lista partecipanti chat e per lista partecipanti evento
 */
public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private OnItemClickListener mItemClickListener;

    interface OnItemClickListener {
        void onUserItemClick(User user);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public UserListAdapter(Context context) {
        mContext = context;
        mUsers = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UserHolder) holder).bind(mContext, mUsers.get(position), mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void setUserList(List<? extends User> users) {
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    public void addLast(User user) {
        mUsers.add(user);
        notifyDataSetChanged();
    }

    private class UserHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView profileImage;

        public UserHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.text_user_list_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_user_list_profile);
        }

        private void bind(final Context context, final User user,
                          @Nullable final UserListAdapter.OnItemClickListener clickListener) {

            nameText.setText(user.getNickname());
            ImageUtils.displayRoundImageFromUrl(context, user.getProfileUrl(), profileImage);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserItemClick(user);
                    }
                });
            }
        }
    }
}

