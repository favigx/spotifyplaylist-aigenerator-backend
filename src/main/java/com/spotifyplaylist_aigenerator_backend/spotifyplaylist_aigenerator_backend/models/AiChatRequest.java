package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models;

import java.util.List;
import java.util.ArrayList;

public class AiChatRequest {

    private String model;
    private List<AiMessage> messages;
    private int n;

    public AiChatRequest(String model, String prompt, int n) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages
                .add(new AiMessage("system",
                        "Du genererar en spellista baserat på liknande låtar och artister som prompten innehåller"));
        this.messages
                .add(new AiMessage("user", prompt));
        this.n = n;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<AiMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<AiMessage> messages) {
        this.messages = messages;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}