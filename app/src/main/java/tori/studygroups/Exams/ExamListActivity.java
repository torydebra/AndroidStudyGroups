package tori.studygroups.Exams;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.otherClass.Argument;
import tori.studygroups.otherClass.Exam;

public class ExamListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getClass().getSimpleName());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

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

        Button clear = (Button) findViewById(R.id.toggle_button);
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.toggleGroup(ex);
                adapter.toggleGroup(ex2);
                adapter.toggleGroup(ex3);
                adapter.toggleGroup(ex4);

            }
        });
    }
}
