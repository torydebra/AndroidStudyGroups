package tori.studygroups.exams;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import tori.studygroups.R;
import tori.studygroups.mainActivities.SignupActivity;
import tori.studygroups.otherClass.Argument;


public class CreateExamFragment extends Fragment{

    private EditText examNameEditText;
    private ImageButton addArgumentButton;
    private ImageButton removeArgumentButton;
    private Button createExamButton;
    private LinearLayout layoutContainerArgumentList;

    private ArrayList<Argument> argumentList;
    private ArrayList<Integer> layoutIdList;
    private ArrayList<Integer> argumentEditTextIdList;
    private ProgressDialog progressDialog;

    public static CreateExamFragment newInstance() {
        CreateExamFragment fragment = new CreateExamFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_exam, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        examNameEditText = (EditText) rootView.findViewById(R.id.edittext_exam_name);
        addArgumentButton = (ImageButton) rootView.findViewById(R.id.image_button_add_argument);
        removeArgumentButton = (ImageButton) rootView.findViewById(R.id.image_button_remove_argument);
        createExamButton = (Button) rootView.findViewById(R.id.button_create_exam);
        layoutContainerArgumentList = (LinearLayout) rootView.findViewById(R.id.layout_container_arguments);

        layoutIdList = new ArrayList<>();
        argumentEditTextIdList = new ArrayList<>();
        argumentList = new ArrayList<>();

        //aggiunge primo argomento che c'è di default;
        layoutIdList.add(R.id.layout_first_argument);
        argumentEditTextIdList.add(R.id.first_argument_name);
        setupAddArgBtn();
        setupDeleteArgBtn(rootView, layoutContainerArgumentList);
        setupCreateBtn(rootView);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione esame...");

        return rootView;

    }



    private void setupAddArgBtn() {
        addArgumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int layoutId = View.generateViewId();
                int editTextId = View.generateViewId();
                layoutIdList.add(layoutId);
                argumentEditTextIdList.add(editTextId);

                TextInputLayout layoutEditTextArgument = new TextInputLayout(getContext());
                TextInputLayout.LayoutParams layoutParam = new TextInputLayout.LayoutParams(TextInputLayout.LayoutParams.WRAP_CONTENT,
                        TextInputLayout.LayoutParams.WRAP_CONTENT);
                layoutEditTextArgument.setLayoutParams(layoutParam);

                TextInputEditText editTextArgument = new TextInputEditText(getContext());
                editTextArgument.setId(editTextId);
                editTextArgument.setHint("Nome Argomento");
                editTextArgument.setTextSize(18);

                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        600, LinearLayout.LayoutParams.WRAP_CONTENT);
                editTextArgument.setLayoutParams(editTextParams);

                layoutEditTextArgument.setId(layoutId);
                layoutEditTextArgument.addView(editTextArgument);

                layoutContainerArgumentList.addView(layoutEditTextArgument);


            }
        });

    }

    private void setupDeleteArgBtn(final View rootView, final LinearLayout parentLinearLayout) {

        removeArgumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutIdList.size() > 0){
                    int idToDelete = layoutIdList.remove(layoutIdList.size()-1);
                    argumentEditTextIdList.remove(argumentEditTextIdList.size()-1);
                    if (argumentList.size()>0){
                        argumentList.remove(argumentList.size()-1);
                    }


                    TextInputLayout layoutEditTextArgumentToDelete =
                            (TextInputLayout) rootView.findViewById(idToDelete);

                    parentLinearLayout.removeView(layoutEditTextArgumentToDelete);

                }
            }
        });
    }


    private void setupCreateBtn(final View rootView) {

        createExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                createExamButton.setEnabled(false);

                if (examNameEditText.getText().toString().isEmpty()){
                    examNameEditText.setError("Inserire un nome");
                    createExamButton.setEnabled(true);
                    progressDialog.dismiss();
                    return;

                } else {

                    final String examName = examNameEditText.getText().toString();
                    for (int ediTextId : argumentEditTextIdList){

                        TextInputEditText editTextArg = (TextInputEditText) rootView.findViewById(ediTextId);
                        if (! editTextArg.getText().toString().isEmpty()) {
                            String name = editTextArg.getText().toString().toLowerCase();
                            argumentList.add(new Argument(name, Argument.ArgumentState.INCOMPLETE, examName));
                        }
                    }

                    Log.d("MAHH", argumentList.toString());

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference dbRefUserExams = FirebaseDatabase.getInstance().getReference().child("userExams").child(auth.getCurrentUser().getUid());

                    dbRefUserExams.child(examName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null){
                                Log.d("MAHH", "nulll");
                                if (argumentList.size() > 0) {
                                    for (int i = 0; i < argumentList.size(); i++) {
                                        dataSnapshot.getRef().push().setValue(argumentList.get(i));
                                        //dbRefUserExams.child(examName).push().setValue(argumentList.get(i));
                                    }
                                } else {
                                    dataSnapshot.getRef().setValue("true");

                                }
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Esame aggiunto", Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();


                            } else {
                                examNameEditText.setError("Hai già inserito un esame con questo nome");
                                examNameEditText.requestFocus();
                                createExamButton.setEnabled(true);
                                progressDialog.dismiss();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }
        });

    }


}
