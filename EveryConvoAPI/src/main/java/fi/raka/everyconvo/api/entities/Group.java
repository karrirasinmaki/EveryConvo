package fi.raka.everyconvo.api.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class Group extends User {
	
	private static String a = TABLE_GROUPS+".";
	public static String FROM = TABLE_GROUPS;
	public static String FK_USERID = a+COL_USERID;
	public static String[] PROJECTION = ArrayUtils.addAll( new String[] {FK_USERID, a+COL_FULLNAME}, User.PROJECTION );

	private String fullname;
	
	public Group(String fullName) {
		this.fullname = fullName;
	}
	public Group(ResultSet rs) throws SQLException {
		super(rs);
		fullname = rs.getString( COL_FULLNAME );
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
			.insertInto(TABLE_GROUPS, COL_USERID, COL_FULLNAME)
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
	
	public static ArrayList<Group> loadGroups(HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		System.out.println("MOMOMOM");
		ArrayList<Group> groups = new ArrayList<Group>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.select(PROJECTION)
			.from(FROM)
			.innerJoin(User.FROM)
			.on(FK_USERID, User.PK_USERID)
			.exec();
		
		rs.beforeFirst();
		while( rs.next() ) {
			groups.add( new Group(rs) );
		}
		rs.close();
		chain.close();
		return groups;
	}
	
}
