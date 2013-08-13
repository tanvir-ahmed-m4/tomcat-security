package security;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.sql.DataSource;

public class EOfficeSessionHandler implements SessionHandler {

	@Override
	public void create(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Principal user = httpRequest.getUserPrincipal();
		HttpSession session = httpRequest.getSession();
		// XXX Create a Session record in the database
		Connection conn = null;
		try {
			InitialContext ic = new InitialContext();
			String datasource = (String) ic.lookup("java:comp/env/auth/datasource");
			DataSource ds = (DataSource) ic.lookup(datasource);
			conn = ds.getConnection();

			PreparedStatement pstmt;

			// Empower Office
			pstmt = conn.prepareStatement("INSERT INTO SE (SYST, PGCD, SECD, USCD, DSRT, TSRT, DUPD, LUPD, DEXP, TEXP, STAT)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'L')");

			String system = (String) ic.lookup("java:comp/env/auth/system");
			String programme = (String) ic.lookup("java:comp/env/auth/programme");
			Date created = new Date(session.getCreationTime());
			Date updated = new Date(session.getLastAccessedTime());
			Date expire = new Date(session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000));

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat tf = new SimpleDateFormat("hh:mm");

			pstmt.setString(1, system); // SYST
			pstmt.setString(2, programme); // PGCD
			pstmt.setString(3, session.getId()); // SECD
			pstmt.setString(4, user.getName()); // USCD
			pstmt.setString(5, df.format(created)); // DSRT
			pstmt.setString(6, tf.format(created)); // TSRT
			pstmt.setString(7, df.format(updated)); // DUPD
			pstmt.setString(8, tf.format(updated)); // LUPD
			pstmt.setString(9, df.format(expire)); // DEXP
			pstmt.setString(10, tf.format(expire)); // TEXP

			pstmt.executeUpdate();
			pstmt.close();

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void destroy(HttpSessionEvent se) {
		// TODO Auto-generated method stub

	}

}