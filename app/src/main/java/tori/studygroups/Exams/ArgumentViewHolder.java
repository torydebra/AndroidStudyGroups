package tori.studygroups.Exams;

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

    public void setArgumentName(String name) {
        argumentNameTextView.setText(name);
    }
}