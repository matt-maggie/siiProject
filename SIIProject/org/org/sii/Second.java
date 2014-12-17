package org.sii;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.examples.Constants;
import org.jinstagram.Instagram;

/**
 * Servlet implementation class Second
 */
@WebServlet("/second")
public class Second extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Second() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String tag = request.getParameter("tag");
		long cont = 500;
		HttpSession session = request.getSession();
		String go = request.getParameter("go");
		DBManager createConnection = new DBManager();
		//createConnection.createTable();
		if(go!=null && tag!=null && !tag.isEmpty()){
			Instagram instagram =(Instagram) session.getAttribute(Constants.INSTAGRAM_OBJECT);
		
			IncreaseFollowers increase= new IncreaseFollowers();
			/*try {
				increase.increaseFollowerByDB(instagram);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//increase.increaseFollowersLike(tag,cont,instagram);
			//increase.increaseFollowersFollow(tag,cont,instagram);

			try {
				increase.searchFollowersByNumber(tag, cont, instagram);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/done.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
