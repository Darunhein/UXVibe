package mx.edu.utez.uxvibe.control;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestsServletTest {

    @Test
    void resolvesPageWithinBounds() {
        assertEquals(1, PaginationSupport.resolvePage(null, 4));
        assertEquals(1, PaginationSupport.resolvePage("0", 4));
        assertEquals(2, PaginationSupport.resolvePage("2", 4));
        assertEquals(4, PaginationSupport.resolvePage("9", 4));
    }

    @Test
    void resolvesPageToFirstWhenInvalid() {
        assertEquals(1, PaginationSupport.resolvePage("abc", 3));
        assertEquals(1, PaginationSupport.resolvePage("-3", 3));
        assertEquals(1, PaginationSupport.resolvePage("1", 0));
    }

    @Test
    void paginatesInGroupsOfThree() {
        List<String> items = Arrays.asList("Prueba 1", "Prueba 2", "Prueba 3", "Prueba 4", "Prueba 5", "Prueba 6", "Prueba 7");

        assertEquals(Arrays.asList("Prueba 1", "Prueba 2", "Prueba 3"), PaginationSupport.paginate(items, 1, 3));
        assertEquals(Arrays.asList("Prueba 4", "Prueba 5", "Prueba 6"), PaginationSupport.paginate(items, 2, 3));
        assertEquals(Collections.singletonList("Prueba 7"), PaginationSupport.paginate(items, 3, 3));
        assertEquals(Collections.singletonList("Prueba 7"), PaginationSupport.paginate(items, 4, 3));
    }
}
