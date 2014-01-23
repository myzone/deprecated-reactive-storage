package com.myzone.utils;

import com.google.common.base.Objects;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * @author myzone
 * @date 11.01.14
 */
public class Matchers extends UtilityClass {

    public static @NotNull <T> TransformationMatcher<T> transformationMatcher() {
        return new TransformationMatcher<>();
    }

    public static @NotNull <T> TransformationMatcher<T> transformationMatcher(Class<T> clazz) {
        return new TransformationMatcher<>();
    }

    public static class TransformationMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

        public static <T, K> Transformation<T, K> namedTransformation(@NotNull String name, @NotNull Function<T, K> transformFunction) {
            return new Transformation<T, K>() {
                public @Override @NotNull String getName() {
                    return name;
                }

                public @Override @NotNull Function<T, K> getFunction() {
                    return transformFunction;
                }
            };
        }

        public static <T, K> Transformation<T, K> anonymousTransformation(@NotNull String name, @NotNull Function<T, K> transformFunction) {
            return namedTransformation("anonymous transformation", transformFunction);
        }

        protected final @NotNull LinkedHashSet<TransformationData<T, ?>> transformations;

        public TransformationMatcher() {
            this(new LinkedHashSet<>());
        }

        protected TransformationMatcher(@NotNull LinkedHashSet<TransformationData<T, ?>> transformations) {
            this.transformations = transformations;
        }

        public <K> TransformationMatcher<T> with(Transformation<T, K> transformation, Matcher<K> transformationMatcher) {
            LinkedHashSet<TransformationData<T, ?>> newTransformations = new LinkedHashSet<>(transformations);

            newTransformations.add(new TransformationData<>(transformation.getName(), transformation.getFunction(), transformationMatcher));

            return new TransformationMatcher<>(newTransformations);
        }

        protected @Override boolean matchesSafely(T item, Description mismatchDescription) {
            boolean ok = true;

            for (TransformationData<T, ?> transformation : transformations) {
                Matcher<?> transformationMatcher = transformation.getTransformationMatcher();
                Object transformed = transformation.getTransformationFunction().apply(item);

                if (!transformationMatcher.matches(transformed)) {
                    mismatchDescription.appendText("because of ")
                            .appendText(transformation.getName())
                            .appendText(" ")
                            .appendDescriptionOf(transformationMatcher);

                    ok = false;
                }
            }

            return ok;
        }

        public @Override void describeTo(Description description) {
            Iterator<TransformationData<T, ?>> iterator = transformations.iterator();
            while (iterator.hasNext()) {
                TransformationData<T, ?> transformation = iterator.next();
                description.appendText(transformation.getName())
                        .appendText(" transformation should be ")
                        .appendDescriptionOf(transformation.getTransformationMatcher());

                if (iterator.hasNext()) {
                    description.appendText(",\n\t");
                }
            }
        }

        public interface Transformation<T, K> {

            @NotNull String getName();

            @NotNull Function<T, K> getFunction();

        }

        protected static @Immutable class TransformationData<T, K> {

            private final @NotNull String name;
            private final @NotNull Function<T, K> transformationFunction;
            private final @NotNull Matcher<K> transformationMatcher;

            public TransformationData(@NotNull String name, @NotNull Function<T, K> transformationFunction, @NotNull Matcher<K> transformationMatcher) {
                this.name = name;
                this.transformationFunction = transformationFunction;
                this.transformationMatcher = transformationMatcher;
            }

            public @NotNull String getName() {
                return name;
            }

            public @NotNull Function<T, K> getTransformationFunction() {
                return transformationFunction;
            }

            public @NotNull Matcher<K> getTransformationMatcher() {
                return transformationMatcher;
            }

            public @Override boolean equals(Object o) {
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;

                TransformationData that = (TransformationData) o;

                if (!name.equals(that.name)) return false;
                if (!transformationFunction.equals(that.transformationFunction)) return false;
                if (!transformationMatcher.equals(that.transformationMatcher)) return false;

                return true;
            }

            public @Override int hashCode() {
                int result = name.hashCode();
                result = 31 * result + transformationFunction.hashCode();
                result = 31 * result + transformationMatcher.hashCode();
                return result;
            }

            public @Override String toString() {
                return Objects.toStringHelper(this)
                        .add("name", name)
                        .add("transformationFunction", transformationFunction)
                        .add("transformationMatcher", transformationMatcher)
                        .toString();
            }

        }

    }

}