package com.jarviswiz;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIProcessor {

    private static final String TAG = "AIProcessor";
    private static final String API_KEY = BuildConfig.OPENAI_API_KEY;
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void processInput(final Context context, final String input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String aiReply = getAIResponse(input);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (aiReply != null) {
                            new SpeechHelper(context).speak(aiReply);
                        } else {
                            new SpeechHelper(context).speak("Sorry, I couldn't reach the AI service.");
                        }
                    }
                });
            }
        }).start();
    }

    private static String getAIResponse(String input) {
        OkHttpClient client = new OkHttpClient();
        try {
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", input);

            JSONArray messages = new JSONArray();
            messages.put(messageObj);

            JSONObject body = new JSONObject();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", messages);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "Unexpected code " + response);
                return null;
            }
            String respStr = response.body().string();
            JSONObject respJson = new JSONObject(respStr);
            String content = respJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return content.trim();

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error calling OpenAI", e);
            return null;
        }
    }
}
