package tori.studygroups.channels;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import org.json.JSONException;
import org.json.JSONObject;

import tori.studygroups.R;
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.utils.DateUtils;
import tori.studygroups.utils.ImageUtils;
import tori.studygroups.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for a RecyclerView that displays messages in an Channel.
 */

class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 10;
    private static final int VIEW_TYPE_EVENT_MESSAGE = 15;
    private static final int VIEW_TYPE_FILE_MESSAGE = 20;
    private static final int VIEW_TYPE_ADMIN_MESSAGE = 30;
    private static final String CUSTOM_TYPE_MESSAGE_TEXT_NORMAL = "normal";
    private static final String CUSTOM_TYPE_MESSAGE_TEXT_EVENT = "event";

    private Context mContext;
    private List<BaseMessage> mMessageList;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    /**
     * An interface to implement item click callbacks in the activity or fragment that
     * uses this adapter.
     */
    interface OnItemClickListener {
        void onUserMessageItemClick(UserMessage message);

        void onFileMessageItemClick(FileMessage message);

        void onAdminMessageItemClick(AdminMessage message);
    }

    interface OnItemLongClickListener {
        void onUserMessageLongClick(UserMessage message);
        void onFileMessageLongClick(FileMessage message);
    }


    ChatAdapter(Context context) {
        mMessageList = new ArrayList<>();
        mContext = context;
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    void setMessageList(List<BaseMessage> messages) {
        mMessageList = messages;
        notifyDataSetChanged();
    }

    void addFirst(BaseMessage message) {
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    void addLast(BaseMessage message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat_message_user, parent, false);
            return new UserMessageHolder(view);

        } else if (viewType == VIEW_TYPE_ADMIN_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat_message_admin, parent, false);
            return new AdminMessageHolder(view);

        } else if (viewType == VIEW_TYPE_FILE_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat_message_file, parent, false);
            return new FileMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_EVENT_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat_message_event_created, parent, false);
            return new EventMessageHolder(view);
        }

        // Theoretically shouldn't happen.
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position) instanceof UserMessage) {

            //primo per caricare messaggi versione vecchia che sono senza customType
            if ( ((UserMessage)mMessageList.get(position)).getCustomType().equals(CUSTOM_TYPE_MESSAGE_TEXT_NORMAL)) {
                    return VIEW_TYPE_USER_MESSAGE;

            } else if( ((UserMessage)mMessageList.get(position)).getCustomType().equals(CUSTOM_TYPE_MESSAGE_TEXT_EVENT)) {
                return VIEW_TYPE_EVENT_MESSAGE;
            }

        } else if (mMessageList.get(position) instanceof AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE;
        } else if (mMessageList.get(position) instanceof FileMessage) {
            return VIEW_TYPE_FILE_MESSAGE;
        }

        // Unhandled message type.
        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = mMessageList.get(position);

        boolean isNewDay = false;

        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() - 1) {
            BaseMessage prevMessage = mMessageList.get(position + 1);

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateUtils.hasSameDate(message.getCreatedAt(), prevMessage.getCreatedAt())) {
                isNewDay = true;
            }

        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE:
                ((UserMessageHolder) holder).bind(mContext, (UserMessage) message, isNewDay,
                        mItemClickListener, mItemLongClickListener);
                break;
            case VIEW_TYPE_ADMIN_MESSAGE:
                ((AdminMessageHolder) holder).bind((AdminMessage) message, isNewDay,
                        mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE:
                ((FileMessageHolder) holder).bind(mContext, (FileMessage) message, isNewDay,
                        mItemClickListener, mItemLongClickListener);
                break;
            case VIEW_TYPE_EVENT_MESSAGE:
                ((EventMessageHolder) holder).bind(mContext, (UserMessage) message, isNewDay,
                        mItemClickListener, mItemLongClickListener);
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    private static class UserMessageHolder extends RecyclerView.ViewHolder {
        TextView nicknameText, messageText, timeText, dateText;
        ImageView profileImage;

        UserMessageHolder(View itemView) {
            super(itemView);

            nicknameText = (TextView) itemView.findViewById(R.id.text_chat_nickname);
            messageText = (TextView) itemView.findViewById(R.id.text_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_chat_time);
            profileImage = (ImageView) itemView.findViewById(R.id.image_chat_profile);
            dateText = (TextView) itemView.findViewById(R.id.text_chat_date);
        }

        // Binds message details to ViewHolder item
        void bind(Context context, final UserMessage message, boolean isNewDay,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {

            User sender = message.getSender();

            // If current user sent the message, display name in different color
            if (sender.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameMe));
            } else {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameOther));
            }

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            nicknameText.setText(message.getSender().getNickname());
            messageText.setText(message.getMessage());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            // Get profile image and display it
            ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onUserMessageLongClick(message);
                        return true;
                    }
                });
            }
        }
    }

    private static class AdminMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText;

        AdminMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_chat_message);
            dateText = (TextView) itemView.findViewById(R.id.text_chat_date);
        }

        void bind(final AdminMessage message, boolean isNewDay, final OnItemClickListener listener) {
            messageText.setText(message.getMessage());

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAdminMessageItemClick(message);
                }
            });
        }
    }

    private static class FileMessageHolder extends RecyclerView.ViewHolder {
        TextView nicknameText, timeText, fileNameText, fileSizeText, dateText;
        ImageView profileImage, fileThumbnail;

        FileMessageHolder(View itemView) {
            super(itemView);

            nicknameText = (TextView) itemView.findViewById(R.id.text_chat_nickname);
            timeText = (TextView) itemView.findViewById(R.id.text_chat_time);
            profileImage = (ImageView) itemView.findViewById(R.id.image_chat_profile);
            fileNameText = (TextView) itemView.findViewById(R.id.text_chat_file_name);
            fileSizeText = (TextView) itemView.findViewById(R.id.text_chat_file_size);
            fileThumbnail = (ImageView) itemView.findViewById(R.id.image_chat_file_thumbnail);
            dateText = (TextView) itemView.findViewById(R.id.text_chat_date);
        }

        // Binds message details to ViewHolder item
        void bind(final Context context, final FileMessage message, boolean isNewDay,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {
            User sender = message.getSender();

            // If current user sent the message, display name in different color
            if (sender.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameMe));
            } else {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameOther));
            }

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Get profile image and display it
            ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

            fileNameText.setText(message.getName());
            fileSizeText.setText(FileUtils.toReadableFileSize(message.getSize()));
            nicknameText.setText(message.getSender().getNickname());

            // If image, display thumbnail
            if (message.getType().toLowerCase().startsWith("image")) {
                // Get thumbnails from FileMessage
                ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

                // If thumbnails exist, get smallest (first) thumbnail and display it in the message
                if (thumbnails.size() > 0) {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnail, thumbnails.get(0).getUrl());
                    } else {
                        ImageUtils.displayImageFromUrl(context, thumbnails.get(0).getUrl(), fileThumbnail);
                    }
                } else {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnail, (String) null);
                    } else {
                        ImageUtils.displayImageFromUrl(context, message.getUrl(), fileThumbnail);
                    }
                }

            } else if (message.getType().toLowerCase().startsWith("video")) {
                // Get thumbnails from FileMessage
                ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

                // If thumbnails exist, get smallest (first) thumbnail and display it in the message
                if (thumbnails.size() > 0) {
                    ImageUtils.displayImageFromUrlWithPlaceHolder(
                            context, thumbnails.get(0).getUrl(), fileThumbnail, R.drawable.ic_file_message);
                } else {
                    fileThumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                }

            } else {
                fileThumbnail.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file_message));
            }

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onFileMessageItemClick(message);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onFileMessageLongClick(message);
                        return true;
                    }
                });
            }

        }
    }

    private static class EventMessageHolder extends RecyclerView.ViewHolder {
        TextView nicknameText, messageText, timeText, dateText, nameEventText, locationEventText,
                dayEventText, timeEventText;
        ImageView profileImage;

        EventMessageHolder(View itemView) {
            super(itemView);

            nicknameText = (TextView) itemView.findViewById(R.id.text_chat_nickname);
            timeText = (TextView) itemView.findViewById(R.id.text_chat_time);
            profileImage = (ImageView) itemView.findViewById(R.id.image_chat_profile);
            dateText = (TextView) itemView.findViewById(R.id.text_chat_date);
            nameEventText = (TextView) itemView.findViewById(R.id.text_chat_event_name);
            locationEventText = (TextView) itemView.findViewById(R.id.text_chat_event_location);
            dayEventText = (TextView) itemView.findViewById(R.id.text_chat_event_day);
            timeEventText = (TextView) itemView.findViewById(R.id.text_chat_event_time);
        }

        // Binds message details to ViewHolder item
        void bind(Context context, final UserMessage message, boolean isNewDay,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {

            User sender = message.getSender();

            JSONObject jsonDataEvent = null;
            try {
                jsonDataEvent = new JSONObject(message.getData()).getJSONObject("event");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // If current user sent the message, display name in different color
            if (sender.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameMe));
            } else {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.ChatNicknameOther));
            }

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            nicknameText.setText(message.getSender().getNickname());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            try {
                nameEventText.setText(jsonDataEvent.getString("name"));
                locationEventText.setText(jsonDataEvent.getString("location"));
                dayEventText.setText(jsonDataEvent.getString("day"));
                timeEventText.setText(jsonDataEvent.getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Get profile image and display it
            ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onUserMessageLongClick(message);
                        return true;
                    }
                });
            }
        }
    }


}
