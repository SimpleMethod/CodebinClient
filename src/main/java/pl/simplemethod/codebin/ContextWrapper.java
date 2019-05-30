package pl.simplemethod.codebin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ContextWrapper {
    private static ApplicationContext context;

    @Autowired
    public static void initWrapper(ApplicationContext ac) {
        context = ac;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
