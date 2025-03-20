package lucashs.dev.common;

import java.util.List;

public class PagedList<T> {
    public List<T> data;
    public int page;
    public int totalPages;
    public int size;
    public long totalItems;

    public PagedList(List<T> data, int page, int totalPages, int size, long totalItems) {
        this.data = data;
        this.page = page;
        this.totalPages = totalPages;
        this.size = size;
        this.totalItems = totalItems;
    }
}
