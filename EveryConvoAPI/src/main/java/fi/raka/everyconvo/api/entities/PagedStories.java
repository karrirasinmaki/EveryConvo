package fi.raka.everyconvo.api.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import fi.raka.everyconvo.utils.ServeletUtils;

public class PagedStories extends Paged {

	public PagedStories(String[] users, HttpServletRequest req, Integer limit, Integer page, Long startTimeMillis) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		if( limit == null || limit < 0 ) limit = 10;
		if( limit > 30 ) limit = 30;
		if( page == null || page <= 0 ) page = 1;
		if( startTimeMillis == null ) startTimeMillis = new Date().getTime();
		
		String cursorStart = ServeletUtils.getFullURL(req) + "?limit=" + limit + "&page=";
		ArrayList<Story> stories = Story.loadStories(users, req, limit, startTimeMillis, (page-1)*limit);
		
		setData( stories );
		if( page > 1 ) setPrevCursor( cursorStart + (page-1) );
		if( stories.size() >= limit ) setNextCursor( cursorStart + (page+1) );
	}

	@Override
	public void setData(Object o) {
		data = o;
	}

	@Override
	public void setPrevCursor(String prev) {
		this.prev = prev;
	}

	@Override
	public void setNextCursor(String next) {
		this.next = next;
	}
	
}
