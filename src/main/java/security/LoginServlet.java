package security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// XXX Disallow GET request in /LOGIN
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String sessionCode = request.getSession().getId();

		Document rdfdoc = DocumentHelper.createDocument();
		Element root = rdfdoc.addElement("RDF:RDF");
		root.addNamespace("RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		root.addNamespace("NC", "http://home.netscape.com/NC-rdf#");

		Element description = root.addElement("RDF:Description");
		description.addAttribute("id", "ncSession");
		description.addAttribute("RDF:about", "urn:netcourier/menu/session");

		description.addElement("NC:sessionCode").setText(sessionCode);
		description.addElement("NC:courierCode").setText("040"); // Magic number!
		description.addElement("NC:userCode").setText(request.getUserPrincipal().getName());
		description.addElement("NC:records").setText("");
		description.addElement("NC:courierDesc").setText("Empower Office - All inclusive package for Tour Operators");
		response.setContentType("xml");
		response.getWriter().print(rdfdoc.asXML());
	}

}
