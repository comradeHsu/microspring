package org.microspring.core.env;

import org.microspring.lang.Nullable;
import org.microspring.util.Assert;
import org.microspring.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;

final class ProfilesParser {

    private ProfilesParser() {
    }

    static Profiles parse(String... expressions) {
        Assert.notEmpty(expressions, "Must specify at least one profile");
        Profiles[] parsed = new Profiles[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            parsed[i] = parseExpression(expressions[i]);
        }
        return new ParsedProfiles(expressions, parsed);
    }

    private static Profiles parseExpression(String expression) {
        Assert.hasText(expression, () -> "Invalid profile expression [" + expression + "]: must contain text");
        StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
        return parseTokens(expression, tokens);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens) {
        List<Profiles> elements = new ArrayList<>();
        Operator operator = null;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.isEmpty()) {
                continue;
            }
            switch (token) {
                case "(":
                    elements.add(parseTokens(expression, tokens));
                    break;
                case "&":
                    assertWellFormed(expression, operator == null || operator == Operator.AND);
                    operator = Operator.AND;
                    break;
                case "|":
                    assertWellFormed(expression, operator == null || operator == Operator.OR);
                    operator = Operator.OR;
                    break;
                case "!":
                    elements.add(not(parseTokens(expression, tokens)));
                    break;
                case ")":
                    Profiles merged = merge(expression, elements, operator);
                    elements.clear();
                    elements.add(merged);
                    operator = null;
                    break;
                default:
                    elements.add(equals(token));
            }
        }
        return merge(expression, elements, operator);
    }

    private static Profiles merge(String expression, List<Profiles> elements, @Nullable Operator operator) {
        assertWellFormed(expression, !elements.isEmpty());
        if (elements.size() == 1) {
            return elements.get(0);
        }
        Profiles[] profiles = elements.toArray(new Profiles[0]);
        return (operator == Operator.AND ? and(profiles) : or(profiles));
    }

    private static void assertWellFormed(String expression, boolean wellFormed) {
        Assert.isTrue(wellFormed, () -> "Malformed profile expression [" + expression + "]");
    }

    private static Profiles or(Profiles... profiles) {
        return activeProfile -> Arrays.stream(profiles).anyMatch(isMatch(activeProfile));
    }

    private static Profiles and(Profiles... profiles) {
        return activeProfile -> Arrays.stream(profiles).allMatch(isMatch(activeProfile));
    }

    private static Profiles not(Profiles profiles) {
        return activeProfile -> !profiles.matches(activeProfile);
    }

    private static Profiles equals(String profile) {
        return activeProfile -> activeProfile.test(profile);
    }

    private static Predicate<Profiles> isMatch(Predicate<String> activeProfile) {
        return profiles -> profiles.matches(activeProfile);
    }

    private enum Operator {AND, OR}


    private static class ParsedProfiles implements Profiles {

        private final String[] expressions;

        private final Profiles[] parsed;

        ParsedProfiles(String[] expressions, Profiles[] parsed) {
            this.expressions = expressions;
            this.parsed = parsed;
        }

        @Override
        public boolean matches(Predicate<String> activeProfiles) {
            for (Profiles candidate : this.parsed) {
                if (candidate.matches(activeProfiles)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.arrayToDelimitedString(this.expressions, " or ");
        }
    }

}
