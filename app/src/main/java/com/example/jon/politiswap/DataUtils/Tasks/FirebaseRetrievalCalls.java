package com.example.jon.politiswap.DataUtils.Tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jon.politiswap.DataUtils.Policy;
import com.example.jon.politiswap.DataUtils.Swap;
import com.example.jon.politiswap.MainActivity;
import com.example.jon.politiswap.TabManagement.TopTabManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FirebaseRetrievalCalls {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RetrieveFirebase mRetriever;

    public FirebaseRetrievalCalls(RetrieveFirebase retriever){
        mRetriever = retriever;
    }

    public interface RetrieveFirebase{
        void newPoliciesSent(List<Policy> policies);
        void newSwapsSent(List<Swap> swaps);
    }

    public void getNewPolices(){
        final List<Policy> tempPolicies = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Policies");
        mDatabaseReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(0, thisPolicy);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newPoliciesSent(tempPolicies);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getTopPolicies(){
        final List<Policy> tempPolicies = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Policies");
        mDatabaseReference.orderByChild("netWanted").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(0, thisPolicy);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newPoliciesSent(tempPolicies);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getPolicyAreaSearch(String area){
        final List<Policy> tempPolicies = new ArrayList<>();
        final List<String> tempIDs = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("Subjects/" + area + "/byPolicy").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String policyID = snap.getValue(String.class);
                    tempIDs.add(0,policyID);
                }
                if (tempIDs.size() == 0){
                    mRetriever.newPoliciesSent(tempPolicies);
                } else if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    for (String id : tempIDs) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getUserPolicies() {
        final List<Policy> tempPolicies = new ArrayList<>();
        final Set<String> tempIDs = new HashSet<>();
        tempIDs.addAll(MainActivity.mUserCreated);
        tempIDs.addAll(MainActivity.mUserVoted);
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Policies");
        if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
            for (String id : tempIDs) {
                mDatabaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    public void getInitialLists(){
        final List<String> tempIDs = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("UserPolicies").child(MainActivity.USER_ID);
        mDatabaseReference.child("Created").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String policyID = snap.getValue(String.class);
                    tempIDs.add(policyID);
                }
                MainActivity.mUserCreated.addAll(tempIDs);
                tempIDs.clear();
                mDatabaseReference.child("VotedOn").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            String policyID = snap.getKey();
                            tempIDs.add(policyID);
                        }
                        MainActivity.mUserVoted.addAll(tempIDs);
                        MainActivity.mTopTabManager.setTopTabs();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTopSwaps(){
        final List<Swap> tempSwaps = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Swaps");
        mDatabaseReference.orderByChild("rating").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Swap thisSwap = snap.getValue(Swap.class);
                    tempSwaps.add(0, thisSwap);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newSwapsSent(tempSwaps);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getNewSwaps(){
        final List<Swap> tempSwaps = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Swaps");
        mDatabaseReference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Swap thisSwap = snap.getValue(Swap.class);
                    tempSwaps.add(0, thisSwap);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newSwapsSent(tempSwaps);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getSwapAreaSearch(String area){
        final List<Swap> tempSwaps = new ArrayList<>();
        final List<String> tempIDs = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("Subjects/" + area + "/bySwap").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String swapID = snap.getValue(String.class);
                    tempIDs.add(0,swapID);
                }
                if (tempIDs.size() == 0){
                    mRetriever.newSwapsSent(tempSwaps);
                } else if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    for (String id : tempIDs) {
                        mDatabaseReference.child("Swaps").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                tempSwaps.add(dataSnapshot.getValue(Swap.class));
                                mRetriever.newSwapsSent(tempSwaps);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getUserSwaps(){
        final List<Swap> tempSwaps = new ArrayList<>();
        final Set<String> tempIDs = new HashSet<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("UserSwaps").child(MainActivity.USER_ID);
        mDatabaseReference.child("Created").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Log.i("HGHGHGHGHG", "layer 1 (created) is running");
                    String swapID = snap.getValue(String.class);
                    tempIDs.add(swapID);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mDatabaseReference.child("VotedOn").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                Log.i("HGHGHGHGHG", "layer 2 (votedon) is running");
                                String swapID = snap.getValue(String.class);
                                tempIDs.add(swapID);
                            }
                            if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                                for (String id : tempIDs) {
                                    mFirebaseDatabase.getReference("Swaps").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            tempSwaps.add(dataSnapshot.getValue(Swap.class));
                                            Log.i("HGHGHGHGHG", "layer 3 (tempswaps) is running");
                                            Log.i("HGHGHGHGHG", "current swap is: " + tempSwaps.get(tempSwaps.size()-1).getTimestamp());
                                            mRetriever.newSwapsSent(tempSwaps);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
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
