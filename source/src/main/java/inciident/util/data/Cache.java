
package inciident.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inciident.util.job.InternalMonitor;
import inciident.util.job.NullMonitor;


public class Cache {
    
    private final HashMap<Identifier<?>, Map<Object, Object>> map = new HashMap<>();

    
    public <T> Result<T> get(Provider<T> provider) {
        return get(provider, null);
    }

    
    @SuppressWarnings("unchecked")
    public <T> Result<T> get(Provider<T> provider, InternalMonitor monitor) {
        monitor = monitor != null ? monitor : new NullMonitor();
        try {
            if (provider.storeInCache()) {
                final Map<Object, Object> cachedElements = getCachedElement(provider.getIdentifier());
                synchronized (cachedElements) {
                    T element = (T) cachedElements.get(provider.getParameters());
                    if (element == null) {
                        final Result<T> computedElement = computeElement(provider, monitor);
                        if (computedElement.isPresent()) {
                            element = computedElement.get();
                            cachedElements.put(provider.getParameters(), element);
                        } else {
                            return computedElement;
                        }
                    }
                    return Result.of(element);
                }
            } else {
                return computeElement(provider, monitor);
            }
        } finally {
            monitor.done();
        }
    }

    public <T> Result<T> set(Provider<T> provider) {
        return set(provider, null);
    }

    public <T> Result<T> set(Provider<T> provider, InternalMonitor monitor) {
        monitor = monitor != null ? monitor : new NullMonitor();
        try {
            final Map<Object, Object> cachedElements = getCachedElement(provider.getIdentifier());
            synchronized (cachedElements) {
                final Result<T> computedElement = computeElement(provider, monitor);
                if (computedElement.isPresent()) {
                    final T element = computedElement.get();
                    cachedElements.put(provider.getParameters(), element);
                    return Result.of(element);
                } else {
                    return computedElement;
                }
            }
        } finally {
            monitor.done();
        }
    }

    public <T> Result<T> get(Identifier<T> identifier) {
        return get(identifier, Provider.defaultParameters);
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T> get(Identifier<T> identifier, Object parameters) {
        final Map<Object, Object> cachedElements = getCachedElement(identifier);
        synchronized (cachedElements) {
            return Result.of((T) cachedElements.get(parameters));
        }
    }

    public <T> void reset(Provider<T> provider) {
        reset(provider, null);
    }

    public <T> void reset(Provider<T> provider, InternalMonitor monitor) {
        Map<Object, Object> cachedElement;
        monitor = monitor != null ? monitor : new NullMonitor();
        try {
            synchronized (map) {
                map.clear();
                cachedElement = getCachedElement(provider.getIdentifier());
            }
            synchronized (cachedElement) {
                final Result<T> computedElement = computeElement(provider, monitor);
                if (computedElement.isPresent()) {
                    final T element = computedElement.get();
                    cachedElement.put(provider.getParameters(), element);
                }
            }
        } finally {
            monitor.done();
        }
    }

    public void reset() {
        synchronized (map) {
            map.clear();
        }
    }

    public <T> void reset(Object identifier) {
        synchronized (map) {
            map.remove(identifier);
        }
    }

    public <T> void reset(Object identifier, Object parameters) {
        synchronized (map) {
            final Map<Object, Object> cachedElements = map.get(identifier);
            if (cachedElements != null) {
                cachedElements.remove(parameters);
            }
        }
    }

    private <T> Result<T> computeElement(Provider<T> provider, InternalMonitor monitor) {
        try {
            return provider.apply(this, monitor);
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    private Map<Object, Object> getCachedElement(Identifier<?> identifier) {
        synchronized (map) {
            Map<Object, Object> cachedElement = map.get(identifier);
            if (cachedElement == null) {
                cachedElement = new HashMap<>(3);
                map.put(identifier, cachedElement);
            }
            return cachedElement;
        }
    }

    public void execute(Operation operation) {
        ArrayList<Identifier<?>> identifiers;
        synchronized (map) {
            identifiers = new ArrayList<>(map.keySet());
        }
        for (final Identifier<?> identifier : identifiers) {
            Map<Object, Object> cachedElements;
            synchronized (map) {
                cachedElements = map.get(identifier);
            }
            if (cachedElements != null) {
                synchronized (cachedElements) {
                    cachedElements.replaceAll((k, v) -> operation.apply(identifier, v, k));
                }
            }
        }
    }
}
