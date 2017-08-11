package tori.studygroups.exams;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private LinearLayout noExamContainer;

    private ExamAdapter examAdapter;
    private ArrayList<Exam> examList;

    private String userId;


    public static ExamListFragment newInstance() {
        ExamListFragment fragment = new ExamListFragment();
        return fragment;
    }

    public static ExamListFragment newInstance(String userId) { //pagina di un altro utente
        ExamListFragment fragment = new ExamListFragment();
        Bundle args = new Bundle();
        args.putString(ActivityExamList.USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_personal_page, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        try{
            userId = getArguments().getString(ActivityExamList.USERID);
        } catch (NullPointerException e){
            userId = null;
        }


        textTitleUser = (TextView) rootView.findViewById(R.id.personal_page_hello_message);
        noExamContainer = (LinearLayout) rootView.findViewById(R.id.no_exams_find_container);
        createExamButton = (Button) rootView.findViewById(R.id.btn_personal_page_add_exam);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());

        if (userId == null){

            user = FirebaseAuth.getInstance().getCurrentUser();
            ((ActivityExamList) getActivity()).setActionBarTitle(user.getDisplayName());
            String s = "Pagina personale di " + user.getDisplayName();
            textTitleUser.setText(s);
            createExamButton.setVisibility(View.VISIBLE);
            setupCreateExamButton();


        } else {
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (user.getUid().equals(userId)){ //mia pag
                userId = null;
                ((ActivityExamList) getActivity()).setActionBarTitle(user.getDisplayName());
                String s = "Pagina personale di " + user.getDisplayName();
                textTitleUser.setText(s);
                createExamButton.setVisibility(View.VISIBLE);
                setupCreateExamButton();


            } else {
                FirebaseDatabase.getInstance().getReference("users")
                        .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        ((ActivityExamList) getActivity()).setActionBarTitle(username);
                        String s = "Pagina personale di " + username;
                        textTitleUser.setText(s);
                        createExamButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        getExamsFromFirebase(rootView);
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

        DatabaseReference dbRefUserExams = null;
        if (userId == null){
            dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());

        } else {
            dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(userId);

        }
        dbRefUserExams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (! dataSnapshot.hasChildren()){
                    noExamContainer.setVisibility(View.VISIBLE);

                } else {

                    noExamContainer.setVisibility(View.GONE);
                    examList = new ArrayList<Exam>();
                    for (DataSnapshot exam : dataSnapshot.getChildren()){

                        if (exam.hasChildren()){

                            ArrayList<Argument> argumentList = new ArrayList<Argument>();
                            for (DataSnapshot argument : exam.getChildren()){
                                Log.d("MAHHHHHH", argument.getValue().toString());
                                Map map =  (HashMap) argument.getValue();
                                Argument.ArgumentState state = null;
                                if (map.get("state").toString().equals(Argument.ArgumentState.INCOMPLETE.toString())){
                                    state = Argument.ArgumentState.INCOMPLETE;
                                } else if(map.get("state").toString().equals(Argument.ArgumentState.COMPLETE.toString())){
                                    state = Argument.ArgumentState.COMPLETE;
                                } else if (map.get("state").toString().equals(Argument.ArgumentState.INPROGRESS.toString())){
                                    state = Argument.ArgumentState.INPROGRESS;
                                }

                                Argument arg = new Argument(map.get("name").toString(), state, exam.getKey());
                                argumentList.add(arg);
                            }
                            examList.add(new Exam(exam.getKey(), argumentList, R.drawable.ic_arrow_down));

                        } else //no argomenti
                            examList.add(new Exam(exam.getKey(), null, R.drawable.ic_arrow_down));
                    }

                    setupAdapter();

                }
                loadingBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupAdapter() {

        examAdapter = new ExamAdapter(examList, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(examAdapter);

        if (userId == null){
            examAdapter.setOnItemLongClickListener(new ExamAdapter.OnItemLongClickListener() {
                @Override
                public void onExamLongClick(final Exam exam) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Opzioni")
                            .setItems(R.array.exam_long_clic_options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: //aggiungi argo
                                            addArgument(exam);
                                            break;
                                        case 1: //cancella esame
                                            deleteExam(exam);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .create()
                            .show();

                }

                @Override
                public void onArgumentLongClick(final Argument argument) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Opzioni")
                            .setItems(R.array.argument_long_clic_options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: //cambia stato
                                            changeArgumentState(argument);
                                            break;
                                        case 1: //cancella argomento
                                            deleteArgument(argument);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .create()
                            .show();
                }
            });
        }

    }



    private void addArgument(final Exam exam) {

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_exam_argument)
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
                        dbRefUserExams.child(exam.getTitle()).orderByChild("name").equalTo(input.getText().toString().toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("MAH", Boolean.toString(dataSnapshot.exists()));
                                if (dataSnapshot.exists()){
                                    Toast toast = Toast.makeText(getContext(), "Argomento già inserito per questo esame", Toast.LENGTH_LONG);
                                    toast.show();

                                } else {
                                    DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
                                    dbRefUserExams.child(exam.getTitle()).push()
                                            .setValue(new Argument(input.getText().toString(), Argument.ArgumentState.INCOMPLETE, exam.getTitle()));


                                    //refresh frag
                                    Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container_personal_page);
                                    if (currentFragment instanceof ExamListFragment) {
                                        FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                                        fragTransaction.detach(currentFragment);
                                        fragTransaction.attach(currentFragment);
                                        fragTransaction.commit();
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("MAHH", databaseError.getMessage());

                            }
                        });


                        //refresh fragent

                    }
                })
                .create()
                .show();



    }


    private void deleteExam(final Exam exam) {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_exam_question)
                .setNegativeButton(R.string.delete_message_cancel, null)
                .setPositiveButton(R.string.delete_message_confirmation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
                        dbRefUserExams.child(exam.getTitle()).removeValue();

                        //refresh fragment
                        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container_personal_page);
                        if (currentFragment instanceof ExamListFragment) {
                            FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
                            fragTransaction.detach(currentFragment);
                            fragTransaction.attach(currentFragment);
                            fragTransaction.commit();}
                    }
                })
                .create()
                .show();

    }


    private void changeArgumentState(final Argument argument) {

        int defaultSelection;
        switch (argument.getState()) {
            case INCOMPLETE:
                defaultSelection = 0;
                break;
            case INPROGRESS:
                defaultSelection = 1;
                break;
            case COMPLETE:
                defaultSelection = 2;
                break;
            default:
                defaultSelection = 0;
                break;
        }


        new AlertDialog.Builder(getActivity())
            .setTitle(R.string.argument_change_state)
            .setSingleChoiceItems(R.array.argument_option_states, defaultSelection,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            })
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ListView lw = ((AlertDialog)dialog).getListView();
                    final Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                    DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
                    dbRefUserExams.child(argument.getExamFather()).orderByChild("name").
                            equalTo(argument.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                            Argument arg = null;
                            switch (checkedItem.toString()){
                                case "Da completare":
                                    arg = new Argument(argument.getName(), Argument.ArgumentState.INCOMPLETE, argument.getExamFather());
                                    iterator.next().getRef().setValue(arg);
                                    break;
                                case "In corso":
                                    arg = new Argument(argument.getName(), Argument.ArgumentState.INPROGRESS, argument.getExamFather());
                                    iterator.next().getRef().setValue(arg);
                                    break;
                                case "Completato":
                                    arg = new Argument(argument.getName(), Argument.ArgumentState.COMPLETE, argument.getExamFather());
                                    iterator.next().getRef().setValue(arg);
                                    break;
                            }

                            //refresh fragment
                            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container_personal_page);
                            if (currentFragment instanceof ExamListFragment) {
                                FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            })
            .create()
            .show();
    }


    private void deleteArgument(final Argument argument) {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_argument_question)
                .setNegativeButton(R.string.delete_message_cancel, null)
                .setPositiveButton(R.string.delete_message_confirmation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference("userExams").child(user.getUid());
                        dbRefUserExams.child(argument.getExamFather()).orderByChild("name").
                                equalTo(argument.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                iterator.next().getRef().removeValue();

                                //refresh fragment
                                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container_personal_page);
                                if (currentFragment instanceof ExamListFragment) {
                                    FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
                                    fragTransaction.detach(currentFragment);
                                    fragTransaction.attach(currentFragment);
                                    fragTransaction.commit();}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                })
                .create()
                .show();

    }

}

