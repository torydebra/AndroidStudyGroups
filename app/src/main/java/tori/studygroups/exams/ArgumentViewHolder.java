package tori.studygroups.exams;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;

public class ArgumentViewHolder extends ChildViewHolder {

    private TextView argumentNameTextView;

    public ArgumentViewHolder(View itemView) {
        super(itemView);
        argumentNameTextView = (TextView) itemView.findViewById(R.id.list_item_argument_name);
    }

    public void setArgumentName(String name, Argument.ArgumentState state) {
        argumentNameTextView.setText(name);
        switch (state) {
            case INCOMPLETE:
                argumentNameTextView.setTextColor(Color.RED);
                break;
            case COMPLETE:
                argumentNameTextView.setTextColor(Color.rgb(42,226,13));
                break;
            case INPROGRESS:
                argumentNameTextView.setTextColor(Color.rgb(209, 205, 9));
                break;
            default:
                break;

        }
    }


    void bind(Context context, final Argument argument,
              @Nullable final ExamAdapter.OnItemLongClickListener longClickListener) {

        if (longClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onArgumentLongClick(argument);
                    return true;
                }
            });
        }
    }
}