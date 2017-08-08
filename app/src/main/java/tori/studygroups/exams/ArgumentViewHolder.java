package tori.studygroups.exams;

import android.graphics.Color;
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
                argumentNameTextView.setTextColor(Color.GREEN);
                break;
            case INPROGRESS:
                argumentNameTextView.setTextColor(Color.rgb(237, 221, 0));
                break;
            default:
                break;

        }



    }
}