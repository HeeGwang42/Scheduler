package com.example.scheduler;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleStorage {

    private static final String PREF_NAME = "multi_schedules";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static String makeKey(long dateMillis) {
        return "schedule_" + DATE_FORMAT.format(new Date(dateMillis));
    }

    public static void save(Context ctx, long dateMillis,
                            String subject, String day,
                            String start, String end, String detail) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String existing = sp.getString(key, "[]");

            JSONArray array = new JSONArray(existing);

            JSONObject newItem = new JSONObject();
            newItem.put("subject", subject);
            newItem.put("day", day);
            newItem.put("start", start);
            newItem.put("end", end);
            newItem.put("detail", detail);

            array.put(newItem);
            sp.edit().putString(key, array.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadSubjects(Context ctx, long dateMillis) {
        List<String> result = new ArrayList<>();
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String data = sp.getString(key, "[]");
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                result.add(item.getString("subject"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> loadAll(Context ctx, long dateMillis) {
        List<String> result = new ArrayList<>();
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String data = sp.getString(key, "[]");
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String line = item.getString("day") + "/" + item.getString("subject") + "\n" +
                        item.getString("start") + " ~ " + item.getString("end");
                result.add(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray loadJsonArray(Context ctx, long dateMillis) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String data = sp.getString(key, "[]");
            return new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void update(Context ctx, long dateMillis, int index,
                              String subject, String day, String start, String end, String detail) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String data = sp.getString(key, "[]");
            JSONArray array = new JSONArray(data);

            if (index >= 0 && index < array.length()) {
                JSONObject newItem = new JSONObject();
                newItem.put("subject", subject);
                newItem.put("day", day);
                newItem.put("start", start);
                newItem.put("end", end);
                newItem.put("detail", detail);

                array.put(index, newItem);
                sp.edit().putString(key, array.toString()).apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Context ctx, long dateMillis, int index) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = makeKey(dateMillis);
            String data = sp.getString(key, "[]");
            JSONArray array = new JSONArray(data);

            if (index >= 0 && index < array.length()) {
                array.remove(index);
                sp.edit().putString(key, array.toString()).apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
