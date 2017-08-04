package tori.studygroups.exams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

public class ExamListFragment extends Fragment {

    private TextView textTitleUser;
    private Button createExamButton;


    public static ExamListFragment newInstance() {
        ExamListFragment fragment = new ExamListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_personal_page, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        textTitleUser = (TextView) rootView.findViewById(R.id.personal_page_hello_message);
        createExamButton = (Button) rootView.findViewById(R.id.btn_personal_page_add_exam);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }


        ArrayList<Exam> examList= new ArrayList<>();
        ArrayList<Argument> argumentList= new ArrayList<>();
        argumentList.add(new Argument("Laplaciano", false));
        argumentList.add(new Argument("Fibra", false));
        argumentList.add(new Argument("Addizioni", false));
        final Exam ex = new Exam("Fisica Matematica", argumentList, R.drawable.ic_arrow_down);
        final Exam ex2 = new Exam("Reti di calcolatori", argumentList, R.drawable.ic_arrow_down);
        final Exam ex3 = new Exam("Algoritmi", argumentList, R.drawable.ic_arrow_down);
        final Exam ex4 = new Exam("Calcolatori", argumentList, R.drawable.ic_arrow_down);
        examList.add(ex);
        examList.add(ex2);
        examList.add(ex3);
        examList.add(ex4);

        final ExamAdapter adapter = new ExamAdapter(examList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Button clear = (Button) rootView.findViewById(R.id.toggle_button);
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.toggleGroup(ex);
                adapter.toggleGroup(ex2);
                adapter.toggleGroup(ex3);
                adapter.toggleGroup(ex4);

            }
        });

        setupCreateExamButton();

        return rootView;
    }

    private void setupCreateExamButton() {

        createExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment fragment = createExamFragment.newInstance();
//                FragmentManager manager = getSupportFragmentManager();
//                manager.popBackStack();
//                manager.beginTransaction()
//                        .replace(R.id.container_channels_list, fragment)
//                        .commit();
            }
        });

    }
}
