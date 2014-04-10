package fi.raka.everyconvo.api.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class Person extends User {
	
	private static String a = TABLE_PERSONS+".";
	public static String FROM = TABLE_PERSONS;
	public static String FK_USERID = a+COL_USERID;
	public static String[] OWN_PROJECTION = {FK_USERID, a+COL_FIRSTNAME, a+COL_LASTNAME};
	public static String[] PROJECTION = ArrayUtils.addAll( OWN_PROJECTION, User.PROJECTION );

	private String firstname;
	private String lastname;
	
	public Person(String firstName, String lastName, String userName, String description, String websiteUrl, String location, Integer visibility) {
		super( userName, description, websiteUrl, location, visibility );
		this.firstname = firstName;
		this.lastname = lastName;
	}
	public Person(ResultSet rs) throws SQLException {
		super(rs);
		firstname = rs.getString( COL_FIRSTNAME );
		lastname = rs.getString( COL_LASTNAME );
	}
	public Person(ResultSet rs, User u) throws SQLException {
		super( u.getUserName(), u.getDescription(), u.getWebsiteUrl(), u.getLocation(), u.getVisibility(), u.getImageUrl() );
		setIsMe(u.isMe());
		firstname = rs.getString( COL_FIRSTNAME );
		lastname = rs.getString( COL_LASTNAME );
	}
	public Person(String firstName, String lastName, User u) {
		super( u.getUserName(), u.getDescription(), u.getWebsiteUrl(), u.getLocation(), u.getVisibility(), u.getImageUrl() );
		setIsMe(u.isMe());
		this.firstname = firstName;
		this.lastname = lastName;
	}
	
	/**
	 * Add new group row to groups table
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void addPersonRow() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain().open(DATABASE_URL)
			.insertInto(FROM, COL_USERID, COL_FIRSTNAME, COL_LASTNAME)
			.values(""+getUserId(), firstname, lastname)
			.update();
	}
	
	@Override
	public void register(String password) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		super.register(password);
		addPersonRow();
	}
	
	public static ArrayList<Person> loadPersons(HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Person> persons = new ArrayList<Person>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.select(PROJECTION)
			.from(FROM)
			.innerJoin(User.FROM)
			.on(FK_USERID, User.PK_USERID)
			.exec();
		
		rs.beforeFirst();
		while( rs.next() ) {
			persons.add( new Person(rs) );
		}
		rs.close();
		chain.close();
		return persons;
	}
	

	public static Person loadPerson(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User u = getSessionUser(req);
		if( userName == null ) {
			if( u == null ) return null;
		}
		u = User.loadUser(userName, req);
		if( u == null ) return null;
		
		Person person = null;
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.select(OWN_PROJECTION)
			.from(FROM)
			.whereIs(FK_USERID, ""+u.getUserId())
			.exec();
		
		if( rs.next() ) {
			person = new Person( rs, u );
		}
		rs.close();
		chain.close();
		
		return person;
	}
	
}
