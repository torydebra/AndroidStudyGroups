package tori.studygroups.exams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

public class ExamListFragment extends Fragment {

    private TextView textTitleUser;
    private Button createExamButton;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;


    public static ExamListFragment newInstance() {
        ExamListFragment fragment = new ExamListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_personal_page, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ((ActivityExamList) getActivity()).setActionBarTitle(user.getDisplayName());

        textTitleUser = (TextView) rootView.findViewById(R.id.personal_page_hello_message);
        String s = "Pagina personale di " + user.getDisplayName();
        textTitleUser.setText(s) ;
        createExamButton = (Button) rootView.findViewById(R.id.btn_personal_page_add_exam);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());

        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        setupCreateExamButton();


        getExamsFromFirebase(rootView);
//        ArrayList<Exam> examList= new ArrayList<>();
//        ArrayList<Argument> argumentList= new ArrayList<>();
//        argumentList.add(new Argument("Laplaciano", Argument.ArgumentState.COMPLETE));
//        argumentList.add(new Argument("Fibra", Argument.ArgumentState.COMPLETE));
//        argumentList.add(new Argument("Addizioni", Argument.ArgumentState.COMPLETE));
//        final Exam ex = new Exam("Fisica Matematica", argumentList, R.drawable.ic_arrow_down);
//        final Exam ex2 = new Exam("Reti di calcolatori", argumentList, R.drawable.ic_arrow_down);
//        final Exam ex3 = new Exam("Algoritmi", argumentList, R.drawable.ic_arrow_down);
//        final Exam ex4 = new Exam("Calcolatori", argumentList, R.drawable.ic_arrow_down);
//        examList.add(ex);
//        examList.add(ex2);
//        examList.add(ex3);
//        examList.add(ex4);




        setupCreateExamButton();

        return rootView;
    }



    private void setupCreateExamButton() {

        createExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CreateExamFragment.newInstance();

                getFragmentManager().beginTransaction()
                        .replace(R.id.container_personal_page, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    private void getExamsFromFirebase(final View rootView) {

        final LinearLayout loadingBar = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        loadingBar.setVisibility(View.VISIBLE);

        DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
        dbRefUserExams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout noExamContainer = (LinearLayout) rootView.findViewById(R.id.no_exams_find_container);
                if (! dataSnapshot.hasChildren()){
                    noExamContainer.setVisibility(View.VISIBLE);

                } else {

                    Log.d("MAHHHHHH",  Long.toString(dataSnapshot.getChildrenCount()));
                    noExamContainer.setVisibility(View.GONE);
                    ArrayList<Exam> examList = new ArrayList<Exam>();
                    for (DataSnapshot exam : dataSnapshot.getChildren()){

                        Log.d("MAHHHHHH", exam.toString());
                        if (exam.hasChildren()){

                            ArrayList<Argument> argumentList = new ArrayList<Argument>();
                            for (DataSnapshot argument : exam.getChildren()){
                                Log.d("MAHHHHHH", "culerrimo");
                                Log.d("MAHHHHHH", argument.getValue().toString());
                                Map map =  (HashMap) argument.getValue();
                                //Log.d("MAHHH", map.get("name").toString());
                                Argument.ArgumentState state = null;
                                if (map.get("state").toString().equals(Argument.ArgumentState.INCOMPLETE.toString())){
                                    state = Argument.ArgumentState.INCOMPLETE;
                                } else if(map.get("state").toString().equals(Argument.ArgumentState.COMPLETE.toString())){
                                    state = Argument.ArgumentState.COMPLETE;
                                } else if (map.get("state").toString().equals(Argument.ArgumentState.INPROGRESS.toString())){
                                    state = Argument.ArgumentState.INPROGRESS;
                                }

                                Argument arg = new Argument(map.get("name").toString(), state);
                                argumentList.add(arg);
                            }
                            examList.add(new Exam(exam.getKey(), argumentList, R.drawable.ic_arrow_down));

                        } else //no argomenti
                            examList.add(new Exam(exam.getKey(), null, R.drawable.ic_arrow_down));
                    }

                    final ExamAdapter adapter = new ExamAdapter(examList);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                }
                loadingBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
