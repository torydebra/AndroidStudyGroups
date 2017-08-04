package tori.studygroups.exams;


import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import tori.studygroups.R;
import tori.studygroups.otherClass.Exam;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ExamViewHolder extends GroupViewHolder {

    private TextView examName;
    private ImageView arrow;
    //private ImageView icon;

    public ExamViewHolder(View itemView) {
        super(itemView);
        examName = (TextView) itemView.findViewById(R.id.list_item_exam_name);
        //icon = (ImageView) itemView.findViewById(R.id.list_item_exam_icon);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_exam_arrow);
    }

    public void setExamTitle(ExpandableGroup exam) {
        if (exam instanceof Exam) {
            examName.setText(exam.getTitle());
            //icon.setBackgroundResource(((Exam) exam).getIconResId());
        }
   
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}