package org.snorochevskiy;

public interface IMapper<T1, T2> {
    T2 map(T1 obj);
}
