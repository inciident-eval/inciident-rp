
package inciident.util.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import inciident.util.data.Problem.Severity;


public class Result<T> {

    private final T object;

    private final List<Problem> problems;

    public static <T> Result<T> of(T object) {
        return new Result<>(object, null);
    }

    public static <T> Result<T> of(T object, List<Problem> problems) {
        return new Result<>(object, problems);
    }

    public static <T> Result<T> ofOptional(Optional<T> optional) {
        return new Result<>(optional.get(), null);
    }

    public static <T> Result<T> empty(List<Problem> problems) {
        return new Result<>(null, problems);
    }

    public static <T> Result<T> empty(Problem... problems) {
        return new Result<>(null, Arrays.asList(problems));
    }

    public static <T> Result<T> empty(Exception... exceptions) {
        return new Result<>(null, Arrays.stream(exceptions).map(Problem::new).collect(Collectors.toList()));
    }

    public static <T> Result<T> empty() {
        return new Result<>(null, null);
    }

    private Result(T object, List<Problem> problems) {
        this.object = object;
        this.problems = (problems == null) || problems.isEmpty() ? Collections.emptyList() : new ArrayList<>(problems);
    }

    public boolean isEmpty() {
        return object == null;
    }

    public boolean isPresent() {
        return object != null;
    }

    public T get() {
        return object;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(object);
    }

    
    public <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
        return object != null ? mapper.apply(object) : Result.empty(problems);
    }

    
    public <R> Result<R> map(Function<T, R> mapper) {
        if (object != null) {
            try {
                return Result.of(mapper.apply(object), problems);
            } catch (final Exception e) {
                return Result.empty(e);
            }
        } else {
            return Result.empty(problems);
        }
    }

    public Result<T> peek(Consumer<T> consumer) {
        if (object != null) {
            try {
                consumer.accept(object);
            } catch (final Exception e) {
            }
        }
        return this;
    }

    public T orElse(T alternative) {
        return object != null ? object : alternative;
    }

    public T orElse(Supplier<T> alternativeSupplier) {
        return object != null ? object : alternativeSupplier.get();
    }

    public T orElse(Consumer<List<Problem>> errorHandler) {
        if (object != null) {
            return object;
        } else {
            errorHandler.accept(problems);
            return null;
        }
    }

    public T orElse(T alternative, Consumer<List<Problem>> errorHandler) {
        if (object != null) {
            return object;
        } else {
            errorHandler.accept(problems);
            return alternative;
        }
    }

    public T orElse(Supplier<T> alternativeSupplier, Consumer<List<Problem>> errorHandler) {
        if (object != null) {
            return object;
        } else {
            errorHandler.accept(problems);
            return alternativeSupplier.get();
        }
    }

    public <E extends Exception> T orElseThrow(Function<List<Problem>, E> errorHandler) throws E {
        if (object != null) {
            return object;
        } else {
            throw errorHandler.apply(problems);
        }
    }

    public T orElseThrow() throws RuntimeException {
        if (object != null) {
            return object;
        } else {
            throw problems.stream() //
                    .filter(p -> p.getSeverity() == Severity.ERROR) //
                    .findFirst() //
                    .map(this::getError) //
                    .orElseGet(RuntimeException::new);
        }
    }

    private RuntimeException getError(Problem p) {
        return p.getException()
                .map(RuntimeException::new)
                .orElseGet(() -> p.getMessage().map(RuntimeException::new).orElseGet(RuntimeException::new));
    }

    public void ifPresent(Consumer<T> resultHandler) {
        if (object != null) {
            resultHandler.accept(object);
        }
    }

    public void ifPresentOrElse(Consumer<T> resultHandler, Consumer<List<Problem>> errorHandler) {
        if (object != null) {
            resultHandler.accept(object);
        } else {
            errorHandler.accept(problems);
        }
    }

    public List<Problem> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    public boolean hasProblems() {
        return !problems.isEmpty();
    }

    public static Optional<Integer> indexToOptional(int index) {
        return index == -1 ? Optional.empty() : Optional.of(index);
    }

    public static <U, V> Function<U, Optional<V>> wrapInOptional(Function<U, V> function) {
        return t -> Optional.ofNullable(t).flatMap(t2 -> Optional.ofNullable(function.apply(t2)));
    }

    @Override
    public String toString() {
        return "Result{" + get() + "}";
    }
}
