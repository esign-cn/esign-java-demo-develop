package cn.esign.demo.develop.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.util.AntPathMatcher;

public class SwaggerPathSelectors {
    private static int count = 0;

    private SwaggerPathSelectors() {
        throw new UnsupportedOperationException();
    }

    public static Predicate<String> any() {
        return Predicates.alwaysTrue();
    }

    public static Predicate<String> none() {
        return Predicates.alwaysFalse();
    }

    public static Predicate<String> regex(String pathRegex) {
        return (input) -> {
            boolean flag = input.matches(pathRegex);
            if (flag) {
                ++count;
            }

            return flag;
        };
    }

    public static Predicate<String> ant(String antPattern) {
        return (input) -> {
            boolean flag = (new AntPathMatcher()).match(antPattern, input);
            if (flag) {
                ++count;
            }

            return flag;
        };
    }

    public static int getCount() {
        return count;
    }
}
