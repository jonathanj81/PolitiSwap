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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FirebaseRetrievalCalls {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RetrieveFirebase mRetriever;
    private boolean mFromScroll;

    public FirebaseRetrievalCalls(RetrieveFirebase retriever, boolean fromScroll){
        mRetriever = retriever;
        mFromScroll = fromScroll;
    }

    public interface RetrieveFirebase{
        void newPoliciesSent(List<Policy> policies, boolean fromScroll);
        void newSwapsSent(List<Swap> swaps, boolean fromScroll);
    }

    public void getNewPolicies(){
        final List<Policy> tempPolicies = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Query query = mFirebaseDatabase.getReference("Policies").limitToLast(20);
        if (mFromScroll){
            query = query.orderByKey().endAt(MainActivity.mLastFirebaseNode);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(0, thisPolicy);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newPoliciesSent(tempPolicies, mFromScroll);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getTopPolicies(){
        final List<Policy> tempPolicies = new ArrayList<>();
        final List<Policy> tempPoliciesFinal = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;

        Query query = FirebaseDatabase.getInstance().getReference("Policies")
                .orderByChild("netWanted");

        if (mFromScroll){
            query = query.limitToLast(20 + MainActivity.mLastPolicyNetOffset).endAt(MainActivity.mLastPolicyNetWanted+0.5);
        } else {
            MainActivity.mLastPolicyNetWanted = Integer.MAX_VALUE;
            MainActivity.mLastPolicyNetOffset = 0;
            query = query.limitToLast(20);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int tempOffset = 0;
                int tempNet = Integer.MAX_VALUE;

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Policy thisPolicy = snap.getValue(Policy.class);
                    tempPolicies.add(thisPolicy);
                }
                Collections.reverse(tempPolicies);
                for (Policy policy : tempPolicies){
                    if (MainActivity.mLastPolicyNetOffset > 1){
                        MainActivity.mLastPolicyNetOffset -= 1;
                    } else {
                        tempPoliciesFinal.add(policy);
                        if (policy.getNetWanted() != tempNet){
                            tempNet = policy.getNetWanted();
                            tempOffset = 1;
                        } else {
                            tempOffset += 1;
                        }
                    }
                }
                MainActivity.mLastPolicyNetOffset = tempOffset;
                MainActivity.mLastPolicyNetWanted = tempNet;
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newPoliciesSent(tempPoliciesFinal, mFromScroll);
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
        Query query = mFirebaseDatabase.getReference("Subjects/" + area + "/byPolicy").limitToLast(20);
        if (mFromScroll){
            //Log.i("HGHGHGHGHG", "policy search called from scroll");
            query = query.orderByKey().endAt(MainActivity.mLastFirebaseNode);
        } else {
            //Log.i("HGHGHGHGHG", "policy search called without scroll");
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String policyID = snap.getValue(String.class);
                    tempIDs.add(0,policyID);
                }
                //Log.i("HGHGHGHGHG", tempIDs.toString());
                if (tempIDs.size() == 0){
                    mRetriever.newPoliciesSent(tempPolicies, mFromScroll);
                } else if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    for (String id : tempIDs) {
                        //Log.i("HGHGHGHGHG", id);
                        mFirebaseDatabase.getReference("Policies").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                tempPolicies.add(dataSnapshot.getValue(Policy.class));
                                if (tempPolicies.size() == tempIDs.size()) {
                                    mRetriever.newPoliciesSent(tempPolicies, mFromScroll);
                                }
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
            if (tempIDs.size() == 0){
                mRetriever.newPoliciesSent(new ArrayList<Policy>(), mFromScroll);
            } else {
                for (String id : tempIDs) {
                    mDatabaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tempPolicies.add(dataSnapshot.getValue(Policy.class));
                            mRetriever.newPoliciesSent(tempPolicies, mFromScroll);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        }
    }

    public void getInitialLists(){
        final List<String> tempIDs = new ArrayList<>();
        MainActivity.mUserCreated.clear();
        MainActivity.mUserVoted.clear();
        MainActivity.mUserSwapCreated.clear();
        MainActivity.mUserSwapVoted.clear();

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
                        tempIDs.clear();

                        mDatabaseReference = mFirebaseDatabase.getReference("UserSwaps").child(MainActivity.USER_ID);
                        mDatabaseReference.child("Created").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    String swapID = snap.getValue(String.class);
                                    tempIDs.add(swapID);
                                }
                                MainActivity.mUserSwapCreated.addAll(tempIDs);
                                tempIDs.clear();

                                mDatabaseReference.child("VotedOn").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            String swapID = snap.getKey();
                                            tempIDs.add(swapID);
                                        }
                                        MainActivity.mUserSwapVoted.addAll(tempIDs);
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
        final List<Swap> tempSwapsFinal = new ArrayList<>();
        final String queueIdentifier = UUID.randomUUID().toString();
        MainActivity.mTaskWithPriority = queueIdentifier;

        Query query = FirebaseDatabase.getInstance().getReference("Swaps")
                .orderByChild("rating");

        if (mFromScroll){
            query = query.limitToLast(20 + MainActivity.mLastSwapNetOffset).endAt(MainActivity.mLastSwapNetWanted+0.05);
        } else {
            MainActivity.mLastSwapNetWanted = Integer.MAX_VALUE;
            MainActivity.mLastSwapNetOffset = 0;
            query = query.limitToLast(20);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int tempOffset = 0;
                double tempNet = Integer.MAX_VALUE;

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Swap thisSwap = snap.getValue(Swap.class);
                    tempSwaps.add(thisSwap);
                }
                Collections.reverse(tempSwaps);
                for (Swap swap : tempSwaps){
                    if (MainActivity.mLastSwapNetOffset > 1){
                        MainActivity.mLastSwapNetOffset -= 1;
                    } else {
                        tempSwapsFinal.add(swap);
                        if (swap.getRating() != tempNet){
                            tempNet = swap.getRating();
                            tempOffset = 1;
                        } else {
                            tempOffset += 1;
                        }
                    }
                }
                MainActivity.mLastSwapNetOffset = tempOffset;
                MainActivity.mLastSwapNetWanted = tempNet;
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newSwapsSent(tempSwapsFinal, mFromScroll);
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
        Query query = mFirebaseDatabase.getReference("Swaps").limitToLast(20);
        if (mFromScroll){
            query = query.orderByKey().endAt(MainActivity.mLastFirebaseNode);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Swap thisSwap = snap.getValue(Swap.class);
                    tempSwaps.add(0, thisSwap);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mRetriever.newSwapsSent(tempSwaps, mFromScroll);
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

        Query query = mFirebaseDatabase.getReference("Subjects/" + area + "/bySwap").limitToLast(20);
        if (mFromScroll){
            query = query.orderByKey().endAt(MainActivity.mLastFirebaseNode);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String swapID = snap.getValue(String.class);
                    tempIDs.add(0,swapID);
                }
                if (tempIDs.size() == 0){
                    mRetriever.newSwapsSent(tempSwaps, mFromScroll);
                } else if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    for (String id : tempIDs) {
                        mFirebaseDatabase.getReference("Swaps").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                tempSwaps.add(dataSnapshot.getValue(Swap.class));
                                if (tempSwaps.size() == tempIDs.size()) {
                                    mRetriever.newSwapsSent(tempSwaps, mFromScroll);
                                }
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
                    String swapID = snap.getValue(String.class);
                    tempIDs.add(swapID);
                }
                if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                    mDatabaseReference.child("VotedOn").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                String swapID = snap.getKey();
                                tempIDs.add(swapID);
                            }
                            if (queueIdentifier.equals(MainActivity.mTaskWithPriority)) {
                                for (String id : tempIDs) {
                                    mFirebaseDatabase.getReference("Swaps").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            tempSwaps.add(dataSnapshot.getValue(Swap.class));
                                            mRetriever.newSwapsSent(tempSwaps, mFromScroll);
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
