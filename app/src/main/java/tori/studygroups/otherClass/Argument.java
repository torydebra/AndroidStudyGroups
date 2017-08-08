package tori.studygroups.otherClass;
import android.os.Parcel;
import android.os.Parcelable;

public class Argument implements Parcelable {

    public enum ArgumentState {INCOMPLETE, INPROGRESS, COMPLETE}

    private String name;
    private ArgumentState state;

    public Argument(String name, ArgumentState state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public ArgumentState getState() {
        return state;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Argument)) return false;
//
//        Argument argument = (Argument) o;
//
//        if (getState() != argument.getState()) return false;
//        return getName() != null ? getName().equals(argument.getName()) : argument.getName() == null;
//
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(state);
    }


    public static final Creator<Argument> CREATOR = new Creator<Argument>() {
        @Override
        public Argument createFromParcel(Parcel in) {
            return new Argument(in);
        }

        @Override
        public Argument[] newArray(int size) {
            return new Argument[size];
        }
    };

    protected Argument(Parcel in) {
        name = in.readString();
        state = (ArgumentState) in.readSerializable();
    }
}
