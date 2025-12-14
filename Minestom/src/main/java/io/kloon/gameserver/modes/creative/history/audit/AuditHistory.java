package io.kloon.gameserver.modes.creative.history.audit;

import java.util.ArrayList;
import java.util.List;

public class AuditHistory {
    private final List<AuditRecord> records;

    public static final int LIMIT = 1080;

    public AuditHistory() {
        this.records = new ArrayList<>();
    }

    public AuditHistory(List<AuditRecord> records) {
        this.records = records;
    }

    public List<AuditRecord> getRecords() {
        return records;
    }

    public void push(AuditRecord record) {
        records.addFirst(record);
        if (records.size() > LIMIT) {
            records.removeLast();
        }
    }

    public void clear() {
        records.clear();
    }
}
