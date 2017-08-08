package tori.studygroups.exams;

import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

import android.view.LayoutInflater;

public class ExamAdapter extends ExpandableRecyclerViewAdapter<ExamViewHolder, ArgumentViewHolder> {

    public ExamAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public ExamViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public ArgumentViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_argument, parent, false);
        return new ArgumentViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ArgumentViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        final Argument argument = ((Exam) group).getItems().get(childIndex);
        holder.setArgumentName(argument.getName(), argument.getState());
    }

    @Override
    public void onBindGroupViewHolder(ExamViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        holder.setExamTitle(group);
    }
}