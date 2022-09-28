package com.octal.actorPay.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Naveen
 * PageItem - contains pagination information like total number of pages, total data etc
 */
public class PageItem<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public  int totalPages;
	public long totalItems;
	public List<T> items;
	public int pageNumber;
	public int pageSize;

	public PageItem(int totalPage, long itemCount, List<T> items, int pageNumber, int pageSize) {
		super();
		this.totalPages = totalPage;
		this.totalItems = itemCount;
		this.items = items;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public PageItem() {
	}

	@Override
	public String toString() {
		return "PageItem [totalPages=" + totalPages + ", totalItems=" + totalItems + ", items=" + items
				+ ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + "]";
	}
}