package tori.studygroups.otherClass;
import android.os.Parcel;
import android.os.Parcelable;

public class Argument implements Parcelable {

    private String name;
    private boolean isFavorite;

    public Argument(String name, boolean isFavorite) {
        this.name = name;
        this.isFavorite = isFavorite;
    }

    protected Argument(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Argument)) return false;

        Argument argument = (Argument) o;

        if (isFavorite() != argument.isFavorite()) return false;
        return getName() != null ? getName().equals(argument.getName()) : argument.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (isFavorite() ? 1 : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
