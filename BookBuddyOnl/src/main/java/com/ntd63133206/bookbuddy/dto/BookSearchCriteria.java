package com.ntd63133206.bookbuddy.dto;

import java.time.LocalDate;
import java.util.Set;

public class BookSearchCriteria {

    private String keyword;
    private Set<Long> authorIds;
    private Set<Long> tagIds;
    private Double minPrice;
    private Double maxPrice;
    private LocalDate updatedAtStart;
    private LocalDate updatedAtEnd;
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder; 
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Set<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Set<Long> authorIds) {
        this.authorIds = authorIds;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDate getUpdatedAtStart() {
        return updatedAtStart;
    }

    public void setUpdatedAtStart(LocalDate updatedAtStart) {
        this.updatedAtStart = updatedAtStart;
    }

    public LocalDate getUpdatedAtEnd() {
        return updatedAtEnd;
    }

    public void setUpdatedAtEnd(LocalDate updatedAtEnd) {
        this.updatedAtEnd = updatedAtEnd;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
