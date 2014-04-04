package fi.raka.everyconvo.api.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class Group extends User {

	private String fullname;
	
	public Group(String fullName) {
		this.fullname = fullName;
	}
	public Group(ResultSet rs) throws SQLException {
		fullname = rs.getString( COL_FULLNAME );
	}
	
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
	
	public static void add(String fullName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		new Group( fullName ).add();
	}
	
	public static ArrayList<Group> loadGroups() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Group> groups = new ArrayList<Group>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.select(COL_USERID, COL_FULLNAME)
			.from(TABLE_GROUPS)
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
