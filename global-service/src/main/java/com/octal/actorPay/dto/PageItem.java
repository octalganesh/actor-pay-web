package com.octal.actorPay.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Naveen
 * PageItem - contains pagination information like total number of pages, total data etc
 */
public class PageItem<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public final int totalPages;
	public final long totalItems;
	public final List<T> items;
	public final int pageNumber;
	public final int pageSize;
	
	/**
	 * @param totalPage - total number of page
	 * @param itemCount - total item count
	 * @param items - data
	 * @param pageNumber - page number
	 * @param pageSize - total page size
	 */
	public PageItem(int totalPage, long itemCount, List<T> items, int pageNumber, int pageSize) {
		super();
		this.totalPages = totalPage;
		this.totalItems = itemCount;
		this.items = items;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageItem [totalPages=" + totalPages + ", totalItems=" + totalItems + ", items=" + items
				+ ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + "]";
	}
}