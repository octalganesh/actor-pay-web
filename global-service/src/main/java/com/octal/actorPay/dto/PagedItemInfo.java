package com.octal.actorPay.dto;

import java.io.Serializable;

/**
 * @author Naveen
 * PagedItemInfo - contains all the request pagination parameter, on the basis of these parameter data loads from database
 */
public class PagedItemInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	public final int items;
    public final int page;
    public final String sortingField;
    public final boolean isSortingAsc;
    public final String filterQuery;

    /**
     * @param page - page number
     * @param items - number of items on a single page
     * @param sortingField - sorting field
     * @param sortingAsc - true/false, sort data on the basis of asc/desc
     * @param filterQuery - load data on the basis of filter like equals, greater than etc
     */
    public PagedItemInfo(int page, int items, String sortingField, boolean sortingAsc, String filterQuery) {
        this.page = page;
        this.items = items;
        this.sortingField = sortingField;
        isSortingAsc = sortingAsc;
        this.filterQuery = filterQuery;
    }

	@Override
	public String toString() {
		return "PagedItemInfo [items=" + items + ", page=" + page + ", sortingField=" + sortingField + ", isSortingAsc="
				+ isSortingAsc + ", filterQuery=" + filterQuery + "]";
	}
}