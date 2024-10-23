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
        this.messages.add(new AiMessage("system",
                "Generera en lista med 15 låtar som liknar låtarna och artisterna som nämns i användarens prompt. Var kreativ. Format svaret ska vara i exakt, ingen siffra innan: Låtnamn - Artist"));
        this.messages.add(new AiMessage("user", prompt));
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