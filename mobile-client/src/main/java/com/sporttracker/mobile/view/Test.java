import com.gluonhq.charm.glisten.control.CharmListView;
import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) {
        for(Method m : CharmListView.class.getMethods()) {
            if(m.getName().toLowerCase().contains("select"))
                System.out.println(m.getName());
        }
    }
}
