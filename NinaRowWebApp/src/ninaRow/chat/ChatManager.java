package ninaRow.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

    private final List<SingleChatEntry> chatDataList;

    public ChatManager() {
        chatDataList = new ArrayList<>();
    }

    public void addChatString(String chatString, String username) {
        chatDataList.add(new SingleChatEntry(chatString, username));
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex >= chatDataList.size()) {
            return null;
        }
        else {
            return chatDataList.subList(fromIndex, chatDataList.size());
        }
    }

    public void reset() {
        chatDataList.clear();
    }

}