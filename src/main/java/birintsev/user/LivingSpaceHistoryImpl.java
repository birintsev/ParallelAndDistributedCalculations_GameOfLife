package birintsev.user;

import birintsev.model.LivingSpace;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

public class LivingSpaceHistoryImpl implements LivingSpaceHistory {

    private int maxSize;

    private List<Future<LivingSpace>> historyList;

    private final HttpSession userSession;

    public LivingSpaceHistoryImpl(
        int maxSize,
        HttpSession userSession
    ) {
        this.maxSize = maxSize;
        this.userSession = userSession;
        this.historyList = new ArrayList<>(maxSize);
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public Future<LivingSpace> add(Future<LivingSpace> livingSpace) {
        Future<LivingSpace> oldest = null;
        if (maxSize > 0) {
            if (historyList.size() == maxSize) {
                oldest = historyList.get(0);
                historyList = historyList.subList(1, historyList.size());
            }
            historyList.add(livingSpace);
        }
        return oldest;
    }

    @Override
    public Future<LivingSpace> getCurrent() {
        Future<LivingSpace> current = null;
        if (maxSize > 0) {
            current = historyList.get(historyList.size() - 1);
        }
        return current;
    }

    @Override
    public HttpSession getUserSession() {
        return userSession;
    }

    @Override
    public Iterator<Future<LivingSpace>> iterator() {
        return new ArrayList<>(historyList).iterator();
    }

    @Override
    public Collection<Future<LivingSpace>> clear() {
        List<Future<LivingSpace>> oldHistory = historyList;
        historyList = new ArrayList<>(maxSize);
        return oldHistory;
    }
}
