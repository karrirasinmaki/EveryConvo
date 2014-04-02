package fi.raka.everyconvo.api.entities;

public abstract class Paged implements PagedInterface {
	Object data;
	String prev;
	String next;
}

interface PagedInterface {
	public void setData(Object o);
	public void setPrevCursor(String prev);
	public void setNextCursor(String next);
}