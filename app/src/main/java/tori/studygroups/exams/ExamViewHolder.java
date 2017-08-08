package tori.studygroups.exams;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ExamViewHolder extends GroupViewHolder {

    private TextView examName;
    private ImageView arrow;
    private FrameLayout containerExamName;
    //private ImageView icon;

    public ExamViewHolder(View itemView) {
        super(itemView);
        examName = (TextView) itemView.findViewById(R.id.list_item_exam_name);
        containerExamName = (FrameLayout) itemView.findViewById((R.id.container_list_item_exam_name));
        arrow = (ImageView) itemView.findViewById(R.id.list_item_exam_arrow);
    }

    public void setExamTitle(ExpandableGroup exam) {

        int examColor = Color.WHITE; //trasparent
        String examTitle = exam.getTitle();

        if (exam.getItemCount() > 0){
            examColor = Color.rgb(32, 219, 0);
            examTitle += "  (completato)";
            ListIterator<Argument> iterator = exam.getItems().listIterator();

            externalLoop : while (iterator.hasNext()){
                //Log.d("MAHH", iterator.next().getName());
                switch (iterator.next().getState()) {
                    case COMPLETE:
                        break;
                    case INCOMPLETE:
                        examColor = Color.RED;
                        examTitle = exam.getTitle() + "  (da completare)";
                        break externalLoop;
                    case INPROGRESS:
                        examTitle = exam.getTitle() + "  (in corso)";
                        examColor = Color.rgb(249,241,0);
                        break;
                    default:
                        break;
                }
            }
        }

        examTitle = Character.toUpperCase(examTitle.charAt(0)) + examTitle.substring(1);
        examName.setText(examTitle);
        containerExamName.setBackgroundColor(examColor);


   
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


    // Binds message details to ViewHolder item
    void bind(Context context, final Exam exam,
              @Nullable final ExamAdapter.OnItemLongClickListener longClickListener) {

        if (longClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onExamLongClick(exam);
                    return true;
                }
            });
        }
    }
}