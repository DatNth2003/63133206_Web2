package DoGetExample;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Servlet implementation class DoGet
 */
public class DoGet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public DoGet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String value1 =  request.getParameter("param1");
		String value2 =  request.getParameter("param2");
		PrintWriter traVe = response.getWriter();
		traVe.append("Giá trị tham số 1 =");
		traVe.append(value1 + "\n");
		traVe.append("Giá trị tham số 2 =");
		traVe.append(value2+ "\n");
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

}
