package birintsev.user;

import birintsev.model.LivingSpace;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.concurrent.Future;

public interface LivingSpaceHistory extends Iterable<Future<LivingSpace>> {

    int maxSize();

    Future<LivingSpace> add(Future<LivingSpace> livingSpace);

    Future<LivingSpace> getCurrent();

    HttpSession getUserSession();

    /**
     * @return all the stored living space states
     * */
    Collection<Future<LivingSpace>> clear();
}
