package mx.edu.utez.uxvibe.control;

import java.util.ArrayList;
import java.util.List;

final class PaginationSupport {
    static final int PAGE_SIZE = 3;

    private PaginationSupport() {
    }

    static int countPages(int totalItems, int pageSize) {
        if (totalItems <= 0 || pageSize <= 0) {
            return 0;
        }
        return (totalItems + pageSize - 1) / pageSize;
    }

    static int resolvePage(String pageParameter, int totalPages) {
        if (totalPages <= 0) {
            return 1;
        }

        int currentPage;
        try {
            currentPage = Integer.parseInt(pageParameter);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        if (currentPage < 1) {
            return 1;
        }
        if (currentPage > totalPages) {
            return totalPages;
        }
        return currentPage;
    }

    static <T> List<T> paginate(List<T> items, int currentPage, int pageSize) {
        if (items == null || items.isEmpty() || pageSize <= 0) {
            return new ArrayList<>();
        }

        int totalPages = countPages(items.size(), pageSize);
        int safePage = currentPage < 1 ? 1 : Math.min(currentPage, totalPages);
        int fromIndex = (safePage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return new ArrayList<>(items.subList(fromIndex, toIndex));
    }
}
