package com.example.jon.politiswap.DataUtils.Tasks;

import android.support.annotation.NonNull;

import com.example.jon.politiswap.DataUtils.Policy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRetrievalCalls {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RetrieveFirebase mRetriever;

    public FirebaseRetrievalCalls(RetrieveFirebase retriever){
        mRetriever = retriever;
    }

    public interface RetrieveFirebase{
        void newPoliciesSent(List<Policy> policies);
    }

    public void getNewPolices(){
        final List<Policy> tempPolicies = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Policies");
        mDatabaseReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(0, thisPolicy);
                }
                mRetriever.newPoliciesSent(tempPolicies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getTopPolicies(){
        final List<Policy> tempPolicies = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Policies");
        mDatabaseReference.orderByChild("netWanted").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(0, thisPolicy);
                }
                mRetriever.newPoliciesSent(tempPolicies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getPolicyAreaSearch(String area){

        final List<Policy> tempPolicies = new ArrayList<>();
        final List<String> tempIDs = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("Subjects/" + area).limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String policyID = snap.getValue(String.class);
                    tempIDs.add(0,policyID);
                }
                if (tempIDs.size() == 0){
                    mRetriever.newPoliciesSent(null);
                }
                for (String id : tempIDs){
                    mDatabaseReference.child("Policies").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tempPolicies.add(dataSnapshot.getValue(Policy.class));
                            mRetriever.newPoliciesSent(tempPolicies);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
