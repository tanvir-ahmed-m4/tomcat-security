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

public class NetCourierSessionHandler implements SessionHandler {

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

			// NetCourier
			pstmt = conn.prepareStatement("INSERT INTO SE (CRCD, SECD, USID, DATE, TIME, LUPD, STAT, SEID, SESS)"
					+ " VALUES (?, ?, '00000000', ?, ?, ?, 'L', '00000000', ?)");

			String couriercode = (String) ic.lookup("java:comp/env/auth/couriercode");
			Date created = new Date(session.getCreationTime());
			Date updated = new Date(session.getLastAccessedTime());

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat tf = new SimpleDateFormat("hh:mm");

			pstmt.setString(1, couriercode); // CRCD
			pstmt.setString(2, session.getId()); // SECD
			pstmt.setString(3, df.format(created)); // DATE
			pstmt.setString(4, tf.format(created)); // TIME
			pstmt.setString(5, tf.format(updated)); // LUPD
			pstmt.setString(6, session.getId()); // SESS

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
