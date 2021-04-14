package com.albin.blockblitz.framework;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.enums.Statistic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirestoreHandler {
    private static final String TAG = FirestoreHandler.class.getSimpleName();
    private static Map<String, Object> userDoc;
    private static Map<String, Object> statistics;
    private static ArrayList<Pair<String, Long>> globalScores;

    public static MutableLiveData<ResponseCode> getUserData(String uid) {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (Objects.requireNonNull(document).exists()) {
                            parseDocument(document);
                            status.setValue(ResponseCode.SUCCESS);
                        }
                        else {
                            Log.d(TAG, "No such document");
                            createDocuments(status);
                        }
                    }
                    else {
                        Log.d(TAG, "get failed with ", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }

    private static void parseDocument(DocumentSnapshot doc) {
        userDoc = doc.getData();
        statistics = new HashMap<>();
        Map<String, Object> stats = (Map<String, Object>) userDoc.get("statistics");

        for(Map.Entry<String, Object> entry : stats.entrySet()) {
            String key = entry.getKey();
            int value = ((Long)entry.getValue()).intValue();
            statistics.put(key, value);
        }
    }

    private static void createDocuments(MutableLiveData<ResponseCode> status) {
        Map<String, Object> leaderboardDoc = new HashMap<>();
        leaderboardDoc.put("user", "user");
        leaderboardDoc.put("score", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("leaderboard")
                .add(leaderboardDoc)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        createUserDocument(task.getResult().getId(), status);
                    }
                    else {
                        Log.d(TAG, "Write to leaderboard collection failed", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });
    }

    private static void createUserDocument(String leaderboard_id, MutableLiveData<ResponseCode> status) {
        userDoc = new HashMap<>();
        userDoc.put("name", "user");
        userDoc.put("leaderboard_id", leaderboard_id);

        statistics = new HashMap<>();
        statistics.put(String.valueOf(Statistic.HIGHSCORE).toLowerCase(), 0);
        statistics.put(String.valueOf(Statistic.GAMES_PLAYED).toLowerCase(), 0);
        statistics.put(String.valueOf(Statistic.LINES_CLEARED).toLowerCase(), 0);
        statistics.put(String.valueOf(Statistic.AVERAGE_BOARD_FILL).toLowerCase(), 0);
        userDoc.put("statistics", statistics);

        userDoc.put("private_leaderboard", new ArrayList<>());
        updateDb(status);
    }

    private static void updateDb(MutableLiveData<ResponseCode> status) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userDoc.put("statistics", statistics);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(uid)
                    .set(userDoc)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            status.setValue(ResponseCode.SUCCESS);
                        }
                        else {
                            Log.w(TAG, "Error writing document", task.getException());
                            status.setValue(ResponseCode.FAILURE);
                        }
                    });
        }
    }

    public static void updateDb() { //updateDB method variant that does not give feedback when db call is done
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userDoc.put("statistics", statistics);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(uid)
                    .set(userDoc)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                        else {
                            Log.w(TAG, "Error writing document", task.getException());
                        }
                    });
        }
    }

    public static void updateStatistic(Statistic statistic, int value) {
        statistics.put(String.valueOf(statistic).toLowerCase(), value);
    }

    public static int getStatistic(Statistic statistic) {
        return (int) statistics.getOrDefault(String.valueOf(statistic).toLowerCase(), 0);
    }

    public static void incrementStatistic(Statistic statistic) {
        updateStatistic(statistic, getStatistic(statistic) + 1);
    }

    public static void incrementStatistic(Statistic statistic, int value) {
        updateStatistic(statistic, getStatistic(statistic) + value);
    }

    public static void updateScores(int score) {
        //updates highscore if score is greater than highscore
        int highscore = getStatistic(Statistic.HIGHSCORE);
        if (score > highscore) {
            updateStatistic(Statistic.HIGHSCORE, score);
        }

        //adds score to private leaderboard if it is among the
        //user's best 10 scores
        ArrayList<Long> leaderboard = (ArrayList<Long>) userDoc.get("private_leaderboard");
        leaderboard.add((long) score);
        Collections.sort(leaderboard);
        Collections.reverse(leaderboard);

        //leaderboard is overfull
        if (leaderboard.size() > 10) {
            leaderboard.remove(leaderboard.size()-1);
        }

        //update global leaderboard if new score is better than highscore
        if (score > highscore) {
            updateGlobalLeaderboard(score);
        }
    }

    private static void updateGlobalLeaderboard(int newScore) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String leaderboard_id = (String) userDoc.get("leaderboard_id");

        db.collection("leaderboard")
                .document(leaderboard_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> doc = task.getResult().getData();
                        long oldScore = (long) doc.get("score");

                        if (newScore > oldScore) {
                            doc.put("score", newScore);
                            db.collection("leaderboard")
                                    .document(leaderboard_id)
                                    .set(doc)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, "Global leaderboard profile successfully updated!");
                                        }
                                        else {
                                            Log.d(TAG, "Failed to update global leaderboard profile", task2.getException());
                                        }
                                    });
                        }
                    }
                    else {
                        Log.d(TAG, "Failed to download global leaderboard profile", task.getException());
                    }
                });
    }

    public static MutableLiveData<ResponseCode> getGlobalLeaderboardFromDb() {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("leaderboard")
                .orderBy("score", Query.Direction.ASCENDING)
                .limitToLast(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        globalScores = new ArrayList<>();

                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            String user = String.valueOf(doc.getData().get("user"));
                            Long score = (Long)doc.getData().get("score");
                            globalScores.add(0, new Pair<>(user, score));
                        }
                        status.setValue(ResponseCode.SUCCESS);
                    }
                    else {
                        Log.d(TAG, "Failed to get global leaderboard", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }

    public static ArrayList<Pair<String, Long>> getGlobalLeaderboard() {
        return globalScores;
    }

    public static ArrayList<Long> getPrivateLeaderboard() {
        return (ArrayList<Long>) userDoc.get("private_leaderboard");
    }

    public static void updateBoardFill(int average) {
        int prevAverage = getStatistic(Statistic.AVERAGE_BOARD_FILL);
        updateStatistic(Statistic.AVERAGE_BOARD_FILL, (average + prevAverage) / 2);
    }

    public static MutableLiveData<ResponseCode> updateUsername(String username) {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        userDoc.put("name", username);
        updateDb();

        String leaderboard_id = (String) userDoc.get("leaderboard_id");
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("user", username);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("leaderboard")
                .document(leaderboard_id)
                .update(tmp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        status.setValue(ResponseCode.SUCCESS);
                    }
                    else {
                        Log.d(TAG, "Failed to update username", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }

    public static String getUsername() {
        return String.valueOf(userDoc.get("name"));
    }
}