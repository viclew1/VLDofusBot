package fr.lewon.dofus.export.manager;

import java.util.HashMap;
import java.util.Map;

public abstract class IdByNameManager {

    private final Map<String, Integer> idByName = new HashMap<>();
    private final Map<Integer, String> nameById = new HashMap<>();

    public synchronized void addPair(String messageName, int messageId) {
        idByName.put(messageName, messageId);
        nameById.put(messageId, messageName);
    }

    public Integer getId(String name) {
        return idByName.get(name);
    }

    public String getName(int id) {
        return nameById.get(id);
    }

    public void clearAll() {
        idByName.clear();
        nameById.clear();
    }
}
