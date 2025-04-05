package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.ai.openai;

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
                "Generera en lista med önskat antal låtar som liknar låtarna och artisterna som nämns i användarens prompt. Sök igenom spotify innan du ger svar och försäkra dig om att förslagen finns i form av kombinationen låt, artist, album. Var kreativ och unik! När du väljer låt, artist och album ska det vara den som har mest lyssningar på spotify. Formatet på svaret ska vara exakt som till en spotify searchquery, ingen siffra före: track3A%22Låt%20namn%22%20artist%3A%22Artistens%20Namn%22%album%3A%22Albumets%20Namn%22"));
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