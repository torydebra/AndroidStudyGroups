package tori.studygroups.exams;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

import android.view.LayoutInflater;

public class ExamAdapter extends ExpandableRecyclerViewAdapter<ExamViewHolder, ArgumentViewHolder> {

    private OnItemLongClickListener mItemLongClickListener;

    private Context context;

    interface OnItemLongClickListener {
        void onExamLongClick(Exam exam);
        void onArgumentLongClick(Argument argument);
    }

    void setOnItemLongClickListener(ExamAdapter.OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public ExamAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
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
        holder.bind(context, argument, mItemLongClickListener);
    }

    @Override
    public void onBindGroupViewHolder(ExamViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        holder.setExamTitle(group);
        holder.bind(context, (Exam)group, mItemLongClickListener);

    }
}