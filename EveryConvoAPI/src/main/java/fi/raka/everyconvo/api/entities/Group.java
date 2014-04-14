package fi.raka.everyconvo.api.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class Group extends User {
	
	private static String a = TABLE_GROUPS+".";
	public static String FROM = TABLE_GROUPS;
	public static String FK_USERID = a+COL_USERID;
	public static String[] PROJECTION = {FK_USERID, a+COL_FULLNAME};

	private String fullname;
	
	public Group(String fullName, String userName, String description, String websiteUrl, String location, Integer visibility) {
		super( userName, description, websiteUrl, location, visibility );
		this.fullname = fullName;
	}
	public Group(String fullName) {
		super(fullName.toLowerCase().trim(), "", "", "", 1);
		this.fullname = fullName;
	}
	public Group(ResultSet rs) throws SQLException {
		super(rs);
		fullname = rs.getString( COL_FULLNAME );
	}
	public Group(ResultSet rs, User u) throws SQLException {
		super( u );
		setIsMe(u.isMe());
		fullname = rs.getString( COL_FULLNAME );
	}
	Group(String fullName, User u) {
		super( u );
		setIsMe(u.isMe());
		this.fullname = fullName;
	}
	
	/**
	 * Add new group row to groups table
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void addGroupRow() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain().open(DATABASE_URL)
			.insertInto(FROM, COL_USERID, COL_FULLNAME)
			.values(""+getUserId(), fullname)
			.update();
	}
	
	@Override
	public void add() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		super.add();
		addGroupRow();
	}
	
	@Override
	public void register(String password) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		super.register(password);
		addGroupRow();
	}
	
	public static ArrayList<Group> loadGroups(HttpServletRequest req, String query) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Group> groups = new ArrayList<Group>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		SelectChain sel = User.loadAllSelect( req, chain, PROJECTION )
			.innerJoin(FROM)
			.on(FK_USERID, User.PK_USERID);
			whereLike( sel, query );
		ResultSet rs = sel.exec();
		
		rs.beforeFirst();
		while( rs.next() ) {
			groups.add( new Group(rs) );
		}
		rs.close();
		chain.close();
		return groups;
	}
	
	private static SelectChain whereLike(SelectChain chain, String query) {
		if( query != null ) {
			User.loadAllWhere(chain, query)
			.or()
			.whereLike(a+COL_FULLNAME, query);
		}
		return chain;
	}
	
	public static Group loadGroup(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User u = getSessionUser(req);
		if( userName == null ) {
			if( u == null ) return null;
		}
		u = User.loadUser(userName, req);
		if( u == null ) return null;
		
		Group group = null;
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.select(OWN_PROJECTION)
			.from(FROM)
			.whereIs(FK_USERID, ""+u.getUserId())
			.exec();
		
		if( rs.next() ) {
			group = new Group( rs, u );
		}
		rs.close();
		chain.close();
		
		return group;
	}
	
}
