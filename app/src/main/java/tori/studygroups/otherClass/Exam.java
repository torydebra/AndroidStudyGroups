package tori.studygroups.otherClass;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Exam extends ExpandableGroup<Argument> {

    private int iconResId;

    public Exam(String title, List<Argument> items, int iconResId) {
        super(title, items);
        this.iconResId = iconResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exam)) return false;

        Exam exam = (Exam) o;

        return getIconResId() == exam.getIconResId();

    }

    @Override
    public int hashCode() {
        return getIconResId();
    }
}